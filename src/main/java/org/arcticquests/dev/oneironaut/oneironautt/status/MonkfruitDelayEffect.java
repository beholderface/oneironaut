package org.arcticquests.dev.oneironaut.oneironautt.status;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.misc.MediaConstants;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MonkfruitDelayEffect extends MobEffect {
    public MonkfruitDelayEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xa1b25e);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier){
        return duration % 20 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier){
        if (entity instanceof ServerPlayer player){
            ServerLevel world = (ServerLevel) entity.level();
            double maxGaussian = 2.0;
            double rawGaussian = world.random.nextGaussian();
            double gaussian = Math.min(Math.max((rawGaussian * (maxGaussian / 2)) + (maxGaussian / 2), 0), maxGaussian);
            double overallReleased = gaussian + 1;
            List<ItemStack> mediaHolders = new ArrayList<>();
            for (ItemStack checkedStack : player.getInventory().items){
                if (checkedStack.getItem() instanceof MediaHolderItem battery){
                    if (battery.canRecharge(checkedStack) && battery.getMaxMedia(checkedStack) != battery.getMedia(checkedStack)){
                        mediaHolders.add(checkedStack);
                    }
                }
            }
            for (ItemStack checkedStack : player.getInventory().armor){
                if (checkedStack.getItem() instanceof MediaHolderItem battery){
                    if (battery.canRecharge(checkedStack) && battery.getMaxMedia(checkedStack) != battery.getMedia(checkedStack)){
                        mediaHolders.add(checkedStack);
                    }
                }
            }
            if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof MediaHolderItem battery){
                if (battery.canRecharge(player.getItemInHand(InteractionHand.OFF_HAND))){
                    mediaHolders.add(player.getItemInHand(InteractionHand.OFF_HAND));
                }
            }
            int quantity = mediaHolders.size();
            double multiplier = 1.0 + (amplifier / 4.0);
            long inserted = (long) (((overallReleased / quantity) * (multiplier)) * MediaConstants.DUST_UNIT);
            for (ItemStack battery : mediaHolders){
                MediaHolderItem type = (MediaHolderItem) battery.getItem();
                type.insertMedia(battery, inserted, false);
            }
        }
    }
}
