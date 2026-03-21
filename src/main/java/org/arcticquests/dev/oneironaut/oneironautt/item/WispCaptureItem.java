package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import at.petrak.hexcasting.common.lib.HexSounds;
import at.petrak.hexcasting.common.msgs.MsgCastParticleS2C;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import kotlin.collections.CollectionsKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ram.talia.hexal.common.entities.BaseCastingWisp;
import ram.talia.hexal.common.entities.ProjectileWisp;
import ram.talia.hexal.common.entities.TickingWisp;
import ram.talia.hexal.common.lib.HexalEntities;
import ram.talia.hexal.common.network.MsgParticleLinesAck;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Predicate;

public class WispCaptureItem extends ItemMediaHolder {

    public static final ResourceLocation FILLED_PREDICATE =  ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "contains_wisp");

    public WispCaptureItem(Properties settings) {
        super(settings);
    }

    public static String WISP_DATA_TAG = "contained_wisp";
    public static String WISP_TYPE_TAG = "wisp_type";
    private static final int COOLDOWN = 20;
    private static final boolean debugMessages = false;

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        CompoundTag data = stack.getOrCreateTag();
        //check for uninitialized item
        if (!data.contains(TAG_MAX_MEDIA)){
            //does uninitialized wranger already contain nonzero media? probably only going to happen for wranglers that existed before the new init method
            if (this.getMedia(stack) > 0){
                data.putLong(TAG_MAX_MEDIA, MediaConstants.DUST_UNIT * 640);
                return InteractionResultHolder.sidedSuccess(stack, false);
            } else {
                BaseCastingWisp initWisp = this.wispRaycast(user);
                if (initWisp != null){
                    if (initWisp.owner().equals(user.getUUID())){
                        long wispMedia = initWisp.getMedia();
                        long roundedMax = (long) (Math.ceil((float) wispMedia / MediaConstants.DUST_UNIT) * MediaConstants.DUST_UNIT);
                        data.putLong(TAG_MAX_MEDIA, roundedMax);
                        if (this.getMedia(stack) < wispMedia - MediaConstants.SHARD_UNIT){
                            this.setMedia(stack, wispMedia - MediaConstants.SHARD_UNIT);
                        }
                        initWisp.kill();
                        return InteractionResultHolder.sidedSuccess(stack, true);
                    }
                }
                return InteractionResultHolder.fail(stack);
            }
        } else if (!this.hasWisp(stack, null)){
            user.getCooldowns().addCooldown(this, COOLDOWN);
            BaseCastingWisp wisp = this.wispRaycast(user);
            if (wisp != null){
                boolean captured = this.captureWisp(stack, wisp, user);
                return captured ? InteractionResultHolder.sidedSuccess(stack, true) : InteractionResultHolder.fail(stack);
            } else {
                Oneironaut.boolLogger("Raycast did not find anything." + world.isClientSide, debugMessages);
            }
        } else if (user.isShiftKeyDown() && !world.isClientSide && world instanceof ServerLevel serverWorld){
            user.getCooldowns().addCooldown(this, COOLDOWN / 2);
            this.discardWisp(stack, user);
            return InteractionResultHolder.sidedSuccess(stack, false);
        } else if (this.getWispType(stack) == HexalEntities.PROJECTILE_WISP){
            user.getCooldowns().addCooldown(this, COOLDOWN);
            boolean released = this.releaseWisp(stack, user.getEyePosition().add(user.getLookAngle().scale(0.25)), user);
            return released ? InteractionResultHolder.sidedSuccess(stack, true) : InteractionResultHolder.fail(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Nullable
    private BaseCastingWisp wispRaycast(Player user){
        //idk how to make it get the user's actual reach, but this will do well enough IMO
        Vec3 rayVec = user.getLookAngle().scale((user.isCreative() ? 5.2 : 4.5) * (user.getBbHeight() / (user.isShiftKeyDown() ? 1.5 : 1.8) /*in case of pekhui or something*/));
        Vec3 endPos = user.getEyePosition().add(rayVec);
        AABB box = AABB.unitCubeFromLowerCorner(user.getEyePosition()).inflate(rayVec.length() + 1);
        Predicate<Entity> predicate = (entity)-> entity instanceof TickingWisp || entity instanceof ProjectileWisp;
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(user, user.getEyePosition(), endPos, box, predicate, 999999);
        if (hit != null){
            return (BaseCastingWisp) hit.getEntity();
        }
        return null;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        CompoundTag nbt = stack.getOrCreateTag();
        Level world = context.getLevel();
        Player user = context.getPlayer();
        //release the wisp adjacent to the clicked block
        boolean sneaking = user != null && user.isShiftKeyDown();
        if (this.hasWisp(stack, world) && nbt.contains(ItemMediaHolder.TAG_MEDIA) && !sneaking){
            if (user != null){
                user.getCooldowns().addCooldown(this, COOLDOWN);
                BlockPos spawnPos = context.getClickedPos().offset(context.getClickedFace().getNormal());
                Vec3 spawnVec = Vec3.atCenterOf(new Vec3i(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()));
                boolean released  = this.releaseWisp(stack, spawnVec, user);
                return released ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }

    private boolean captureWisp(ItemStack stack, BaseCastingWisp wisp, @NotNull Player user){
        Level world = user.level();
        CompoundTag stackNbt = stack.getOrCreateTag();
        long cost = MediaConstants.SHARD_UNIT;
        if (wisp.getCaster() != user){
            cost = (long) Math.ceil(wisp.getMedia() * 1.5);
        }
        if (!world.isClientSide){
            if ((this.getMedia(stack) >= cost || user.isCreative()) && !wisp.getSeon()){
                this.deductMedia(stack, cost, user);
                CompoundTag wispData = wisp.saveWithoutId(new CompoundTag());
                NBTHelper.putCompound(stackNbt, WISP_DATA_TAG, wispData);
                ((Entity) wisp).kill();
                Oneironaut.boolLogger("Captured wisp for " + cost / MediaConstants.DUST_UNIT + " dust", debugMessages);
                IXplatAbstractions.INSTANCE.sendPacketNear(user.getEyePosition(), 128.0, (ServerLevel) world,
                        new MsgParticleLinesAck(CollectionsKt.listOf(user.getEyePosition(), ((Entity) wisp).position().add(0.0, 0.05, 0.0)), wisp.pigment()));
                world.playSeededSound(null, user, Holder.direct(HexSounds.CAST_HERMES), SoundSource.PLAYERS, 1f, 1f, world.random.nextLong());
                if (wisp instanceof TickingWisp tickingWisp){
                    stackNbt.putString(WISP_TYPE_TAG, "ticking");
                } else if (wisp instanceof ProjectileWisp projectileWisp){
                    stackNbt.putString(WISP_TYPE_TAG, "projectile");
                }
                return true;
            } else {
                world.playSeededSound(null, user, Holder.direct(HexSounds.CAST_FAILURE), SoundSource.PLAYERS, 1f, 1f, world.random.nextLong());
            }
        }
        return false;
    }
    private boolean releaseWisp(ItemStack stack, Vec3 spawnPos, @NotNull Player user){
        CompoundTag nbt = stack.getOrCreateTag();
        Level world = user.level();
        if (this.getMedia(stack) >= MediaConstants.SHARD_UNIT || user.isCreative()) {
            Oneironaut.boolLogger("Releasing contained wisp", debugMessages);
            this.deductMedia(stack, MediaConstants.SHARD_UNIT, user);
            EntityType<?> wispType = this.getWispType(stack);
            BaseCastingWisp wisp = null;
            if (wispType == HexalEntities.TICKING_WISP){
                wisp = new TickingWisp(HexalEntities.TICKING_WISP, world);
            } else if (wispType == HexalEntities.PROJECTILE_WISP){
                wisp = new ProjectileWisp(HexalEntities.PROJECTILE_WISP, world);
            }
            if (wisp != null){
                if (!world.isClientSide && world instanceof ServerLevel serverWorld) {
                    CompoundTag storedNbt = this.getWispData(stack, world);
                    ListTag posList = new ListTag();
                    posList.add(DoubleTag.valueOf(spawnPos.x));
                    posList.add(DoubleTag.valueOf(spawnPos.y));
                    posList.add(DoubleTag.valueOf(spawnPos.z));
                    NBTHelper.putList(storedNbt, "Pos", posList);
                    serverWorld.playSeededSound(null, user, Holder.direct(HexSounds.CAST_HERMES),
                            SoundSource.PLAYERS, 1f, 1f, world.random.nextLong());
                    if (wisp instanceof ProjectileWisp projectileWisp){
                        double speed = projectileWisp.getDeltaMovement().length();
                        Vec3 direction = user.getLookAngle();
                        ListTag motionList = new ListTag();
                        motionList.add(DoubleTag.valueOf(direction.x));
                        motionList.add(DoubleTag.valueOf(direction.y));
                        motionList.add(DoubleTag.valueOf(direction.z));
                        NBTHelper.putList(storedNbt, "Motion", motionList);
                        //projectileWisp.setVelocity(direction.multiply(speed));
                    }
                    wisp.load(storedNbt);
                }
                nbt.remove(WISP_DATA_TAG);
                world.addFreshEntity(wisp);
                if (!world.isClientSide && world instanceof ServerLevel serverWorld) {
                    IXplatAbstractions.INSTANCE.sendPacketNear(user.getEyePosition(), 128.0, serverWorld,
                            new MsgParticleLinesAck(CollectionsKt.listOf(user.getEyePosition(), ((Entity) wisp).position().add(0.0, 0.05, 0.0)), wisp.pigment()));
                    return true;
                }
            }
        } else {
            Oneironaut.boolLogger("Insufficient media to release wisp", debugMessages);
            if (!world.isClientSide && world instanceof ServerLevel serverWorld) {
                serverWorld.playSeededSound(null, user, Holder.direct(HexSounds.CAST_FAILURE),
                        SoundSource.PLAYERS, 1f, 1f, world.random.nextLong());
            }
        }
        return false;
    }
    private void discardWisp(ItemStack stack, @Nullable Player user){
        CompoundTag data = stack.getOrCreateTag();
        CompoundTag formerWispData = this.getWispData(stack, null);
        assert formerWispData != null;
        FrozenPigment colorizer = FrozenPigment.fromNBT(formerWispData.getCompound("colouriser"));
        //int media = formerWispData.getInt("media");
        data.remove(WISP_DATA_TAG);
        if (user != null){
            Level world = user.level();
            if (!world.isClientSide && world instanceof ServerLevel serverWorld){
                world.playSeededSound(null, user, Holder.direct(HexSounds.ABACUS_SHAKE), SoundSource.PLAYERS, 1f, 1f, world.random.nextLong());
                IXplatAbstractions.INSTANCE.sendPacketNear(user.getEyePosition(), 128.0, serverWorld, new MsgCastParticleS2C
                        (ParticleSpray.burst(user.position().add(0.0, 0.125, 0.0), 1.0, 64), colorizer));
            }
        }

    }

    @Nullable
    public CompoundTag getWispData(ItemStack stack, @Nullable Level world){
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.contains(WISP_DATA_TAG)){
            return nbt.getCompound(WISP_DATA_TAG);
        }
        return null;
    }

    @Nullable
    public EntityType<?> getWispType(ItemStack stack){
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.contains(WISP_TYPE_TAG)){
            String typeString = nbt.getString(WISP_TYPE_TAG);
            if (typeString.equals("ticking")){
                return HexalEntities.TICKING_WISP;
            } else if (typeString.equals("projectile")){
                return HexalEntities.PROJECTILE_WISP;
            }
        }
        return null;
    }

    private void deductMedia(ItemStack stack, long amount, Player player){
        if (!player.isCreative()){
            this.setMedia(stack, this.getMedia(stack) - amount);
        }
    }

    public boolean hasWisp(ItemStack stack, Level world){
        return this.getWispData(stack, world) != null;
    }

    @Override
    public boolean canProvideMedia(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canRecharge(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> pTooltipComponents, TooltipFlag context) {
        super.appendHoverText(stack, world, pTooltipComponents, context);
        if (this.hasWisp(stack, null)){
            String hashString = "???";
            CompoundTag wispData = this.getWispData(stack, null);
            assert wispData != null;
            long media = wispData.getLong("media");
            Tag hexData = wispData.get("hex");
            assert hexData != null;
            String nbtString = hexData.toString();
            /*if (world != null && world.getTime() % 100 == 0){
                Oneironautfinal.LOGGER.info(nbtString);
            }*/
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.update(nbtString.getBytes(StandardCharsets.UTF_8));
                hashString = new String(digest.digest());
            } catch (NoSuchAlgorithmException exception){
                //do nothing? idk
            }
            Component unstyled = Component.translatable("oneironaut.tooltip.wispcapturedevice.haswisp", (media / MediaConstants.DUST_UNIT), hashString);
            if (world != null){
                Style coloredStyle = unstyled.getStyle().withColor(
                        FrozenPigment.fromNBT(wispData.getCompound("colouriser")).getColorProvider().getColor(world.getGameTime(), Vec3.ZERO)
                );
                pTooltipComponents.add(unstyled.copy().setStyle(coloredStyle));
            } else {
                pTooltipComponents.add(unstyled);
            }
        } else {
            if (stack.getOrCreateTag().contains(TAG_MAX_MEDIA)){
                pTooltipComponents.add(Component.translatable("oneironaut.tooltip.wispcapturedevice.nowisp"));
            } else {
                pTooltipComponents.add(Component.translatable("oneironaut.tooltip.wispcapturedevice.uninitialized"));
            }
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
