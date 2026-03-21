package org.arcticquests.dev.oneironaut.oneironautt.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.arcticquests.dev.oneironaut.oneironautt.OneironautClient;
import org.arcticquests.dev.oneironaut.oneironautt.network.OneironautNetwork;
import org.arcticquests.dev.oneironaut.oneironautt.network.SpoopyScreamPacket;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShiftingPseudoamethystItem extends Item {
    public ShiftingPseudoamethystItem(Properties settings) {
        super(settings);
    }

    @Override
    public void onDestroyed(ItemEntity entity) {
        Level world = entity.level();
        if (!world.isClientSide && world instanceof ServerLevel serverWorld){
            float pitch = 0.75f + (world.random.nextFloat() / 2);
            OneironautNetwork.CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(entity.getX(), entity.getY(), entity.getZ(),16.0, serverWorld.dimension())),new SpoopyScreamPacket(SoundEvents.FOX_SCREECH, pitch));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> pTooltipComponents, TooltipFlag context){
        super.appendHoverText(stack, world, pTooltipComponents, context);
        if (world != null && world.isClientSide){
            OneironautClient.lastShiftingHoverTick = world.getGameTime();
            if (OneironautClient.lastHoveredShifting != stack){
                OneironautClient.lastHoveredShifting = stack;
            }
        }
    }
}
