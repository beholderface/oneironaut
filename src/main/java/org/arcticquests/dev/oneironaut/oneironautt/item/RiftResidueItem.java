package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes.DimIota;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

public class RiftResidueItem extends ArbitaryDeltaPigmentItem implements IotaHolderItem {
    public RiftResidueItem(Properties settings, int[] colors, Supplier<Double> deltaGetter) {
        super(settings, colors, deltaGetter);
    }
    @Override
    public int getUseDuration(ItemStack stack) {
        return 128;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand){
        super.use(world, user, hand);
        ItemStack itemStack = user.getItemInHand(hand);
        user.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user){
        if (!world.isClientSide && world != Oneironaut.getDeepNoosphere()){
            Vec3 newPos = MiscAPIKt.coerceWithinBorder(
                    MiscAPIKt.scaleBetweenDimensions(user.position(), world, Oneironaut.getDeepNoosphere()),
                    Oneironaut.getDeepNoosphere().getWorldBorder());
            user.teleportTo(Oneironaut.getDeepNoosphere(), newPos.x, 64.0, newPos.z, EnumSet.noneOf(RelativeMovement.class), user.getYRot(), user.getXRot());
        }
        stack.shrink(1);
        return stack;
    }
    @Override
    public UseAnim getUseAnimation(ItemStack stack){
        return UseAnim.EAT;
    }

    private static CompoundTag deepNooTag = null;
    @Override
    public @Nullable CompoundTag readIotaTag(ItemStack stack) {
        try {
            ServerLevel deep = Oneironaut.getDeepNoosphere();
            if (deepNooTag == null) {
                deepNooTag = IotaType.serialize(new DimIota(deep));
            }
            return deepNooTag.copy();
        } catch (IllegalStateException e) {
            return null;
        }
    }
    @Override
    public boolean writeable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canWrite(ItemStack stack, @Nullable Iota iota) {
        return false;
    }

    @Override
    public void writeDatum(ItemStack stack, @Nullable Iota iota) {

    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                              TooltipFlag pIsAdvanced) {
        IotaHolderItem.appendHoverText(this, pStack, pTooltipComponents, pIsAdvanced);
    }
}
