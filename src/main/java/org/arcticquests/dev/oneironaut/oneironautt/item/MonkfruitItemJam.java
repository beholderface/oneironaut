package org.arcticquests.dev.oneironaut.oneironautt.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class MonkfruitItemJam extends Item {
    public static final int DEFAULT_DURATION_JAM = 100;
    public static final int DEFAULT_AMPLIFIER_JAM = 2;
    public MonkfruitItemJam(Properties settings) {
        super(settings);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        super.finishUsingItem(stack, world, user);

        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (user instanceof Player player) {
                MonkfruitItem.applyRumination(player, DEFAULT_DURATION_JAM, DEFAULT_AMPLIFIER_JAM);
                if(!(player).getAbilities().instabuild){
                    ItemStack itemStack = new ItemStack(Items.GLASS_BOTTLE);
                    if (!player.getInventory().add(itemStack)) {
                        player.drop(itemStack, false);
                    }
                }
            }
            return stack;
        }
    }
}
