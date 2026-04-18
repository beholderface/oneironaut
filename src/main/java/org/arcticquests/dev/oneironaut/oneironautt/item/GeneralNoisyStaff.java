package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.common.items.ItemStaff;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GeneralNoisyStaff extends ItemStaff {
    public final SoundEvent openSound;
    public final SoundEvent resetSound;
    private static final Float[] defaultSoundModifiers = {0.5f, 1f, 0.5f, 1f};
    private final Float[] soundModifiers;
    public GeneralNoisyStaff(Properties pProperties, SoundEvent openSound, SoundEvent resetSound, @Nullable Float[] soundModifiers) {
        super(pProperties);
        this.openSound = openSound;
        this.resetSound = resetSound;
        this.soundModifiers = soundModifiers == null ? defaultSoundModifiers : soundModifiers;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        super.use(world, player, hand);
        if (world.isClientSide){
            if (player.isShiftKeyDown()){
                player.playSound(this.resetSound, this.soundModifiers[0], this.soundModifiers[1]);
            } else {
                player.playSound(this.openSound, this.soundModifiers[2], this.soundModifiers[3]);
            }
        }
        /*if (player.isSneaking()) {
            player.playSound(this.resetSound, this.soundModifiers[0], this.soundModifiers[1]);
            if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
                IXplatAbstractions.INSTANCE.clearCastingData(serverPlayer);
            }
        } else {
            player.playSound(this.openSound, this.soundModifiers[2], this.soundModifiers[3]);
        }

        if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {
            var harness = IXplatAbstractions.INSTANCE.getHarness(serverPlayer, hand);
            var patterns = IXplatAbstractions.INSTANCE.getPatterns(serverPlayer);
            var descs = harness.generateDescs();

            IXplatAbstractions.INSTANCE.sendPacketToPlayer(serverPlayer,
                    new MsgOpenSpellGuiAck(hand, patterns, descs.getFirst(), descs.getSecond(), descs.getThird(),
                            harness.getParenCount()));
        }

        player.incrementStat(Stats.USED.getOrCreateStat(this));
//        player.gameEvent(GameEvent.ITEM_INTERACT_START);*/

        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
