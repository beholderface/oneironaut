package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.casting.environments.ReverbRodCastEnv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReverberationRod extends ItemPackagedHex  {

    public static final ResourceLocation CASTING_PREDICATE =  ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "is_casting");
    private static final Map<UUID, ReverbRodCastEnv> ROD_ENV_MAP = new HashMap<>();
    public ReverberationRod(Properties settings){
        super(settings);
    }

    @Override
    public boolean breakAfterDepletion() {
        return false;
    }

    @Override
    public int cooldown() {
        return 1;
    }

    @Override
    public boolean canDrawMediaFromInventory(ItemStack stack) {
        return false;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand usedHand) {
        var stack = player.getItemInHand(usedHand);
        if (!hasHex(stack)) {
            return InteractionResultHolder.fail(stack);
        }
        Stat<?> stat = Stats.ITEM_USED.get(this);
        player.awardStat(stat);
        if (!world.isClientSide && player instanceof ServerPlayer serverPlayer){
            ROD_ENV_MAP.put(serverPlayer.getUUID(), new ReverbRodCastEnv(serverPlayer, usedHand, true));
        }
        player.startUsingItem(usedHand);
        //cast immediately on use rather than waiting for the next tick
        stack.onUseTick(world, player, stack.getUseDuration());
        return InteractionResultHolder.consume(stack);
    }
    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user.isAlwaysTicking() && !world.isClientSide){
            ServerPlayer sPlayer = (ServerPlayer) user; //world.getPlayerByUuid(user.getUuid()).getServer().getPlayerManager().getPlayer(user.getUuid());
            InteractionHand usedHand;
            if(sPlayer.getItemInHand(InteractionHand.MAIN_HAND) == stack){
                usedHand = InteractionHand.MAIN_HAND;
            } else {
                usedHand = InteractionHand.OFF_HAND;
            }
            ReverbRodCastEnv env = ROD_ENV_MAP.get(sPlayer.getUUID());
            env.setCastInProgress(true);
            try {
                if(!castHex(stack, (ServerLevel) world, sPlayer, usedHand)){
                    sPlayer.releaseUsingItem();
                }
            } finally {
                env.setCastInProgress(false);
            }
        }
    }

    private boolean castHex(ItemStack stack, ServerLevel world, ServerPlayer sPlayer, InteractionHand usedHand){
        List<Iota> instrs = getHex(stack, world);
        assert instrs != null;
        ReverbRodCastEnv env = ROD_ENV_MAP.get(sPlayer.getUUID());
        int delay = env.getDelay();
        if (delay <= 0){
            if (delay < 0){
                env.setDelay(0);
            }
            var ctx = ROD_ENV_MAP.get(sPlayer.getUUID());
            var harness = CastingVM.empty(ctx);
            var info = harness.queueExecuteAndWrapIotas(instrs, ctx.getWorld());
            var sound = ctx.getSound().sound();
            if (sound != null) {
                var soundPos = sPlayer.position();
                if (world.getGameTime() >= ctx.lastSoundTimestamp + 30 || ctx.getSound() == HexEvalSounds.MISHAP){
                    sPlayer.level().playSound(null, soundPos.x, soundPos.y, soundPos.z, sound, SoundSource.PLAYERS, 1f, 1f);
                    ctx.updateLastSoundtimestamp();
                } else {
                    sPlayer.level().playSound(null, soundPos.x, soundPos.y, soundPos.z, SoundEvents.AMETHYST_BLOCK_CHIME,
                            SoundSource.PLAYERS, 1f, 1f);
                }
            }
            if (info.getResolutionType().equals(ResolvedPatternType.ERRORED)){
                env.setResetCooldown(20);
                return false;
            }
            return env.getCurrentlyCasting();
        } else {
            env.adjustDelay(-1);
        }
        return true;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (user.isAlwaysTicking() && !world.isClientSide){
            ServerPlayer sPlayer = (ServerPlayer) user;//world.getPlayerByUuid(user.getUuid()).getServer().getPlayerManager().getPlayer(user.getUuid());
            //assert sPlayer != null;
            //assert stack.getNbt() != null;
            ReverbRodCastEnv env = ROD_ENV_MAP.get(user.getUUID());
            sPlayer.getCooldowns().addCooldown(this, env.getResetCooldown());
            env.setCurrentlyCasting(false);
            ROD_ENV_MAP.remove(user.getUUID());
            //Oneironautfinal.LOGGER.info("Stopped casting from rod.");
        }
    }

    public static ReverbRodCastEnv getEnv(Entity user){
        return ROD_ENV_MAP.getOrDefault(user.getUUID(), null);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 20 * 60 * 60;
    }
}
