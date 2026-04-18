package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InsulatedTrinketItem extends ItemPackagedHex {
    public InsulatedTrinketItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean breakAfterDepletion() {
        return false;
    }

    @Override
    public int cooldown() {
        return 5;
    }

    @Override
    public boolean canDrawMediaFromInventory(ItemStack stack) {
        return false;
    }

    private static Tuple<Player, InteractionHand> currentCaster = null;
    public static Player getCurrentCaster(){
        return currentCaster == null ? null : currentCaster.getA();
    }
    public static InteractionHand getHand(){
        return currentCaster == null ? null :currentCaster.getB();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand usedHand){
        try {
            currentCaster = new Tuple<>(player, usedHand);
            InteractionResultHolder<ItemStack> output = super.use(world, player, usedHand);
            currentCaster = null;
            return output;
        } catch (Exception idk/*just in case something goes wrong for whatever reason.
        I don't want to accidentally turn off *all* GameEvent things from casting */){
            currentCaster = null;
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));
        }
    }
}
