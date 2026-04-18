package org.arcticquests.dev.oneironaut.oneironautt.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static org.arcticquests.dev.oneironaut.oneironautt.item.MonkfruitItem.applyRumination;


public class MonkfruitItemCooked extends Item {
    public static final int DEFAULT_DURATION_COOKED = 80;
    public MonkfruitItemCooked(Properties settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (user instanceof Player player){
            applyRumination(player, DEFAULT_DURATION_COOKED, 0);
        }
        return user.eat(world, stack);
    }
}
