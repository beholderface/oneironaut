package org.arcticquests.dev.oneironaut.oneironautt.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry;

public class MonkfruitItem extends ItemNameBlockItem {
    public static final int DEFAULT_DURATION_RAW = 100;
    public MonkfruitItem(Block block, Properties settings) {
        super(block, settings);
    }

    public static void applyRumination(Player player, int duration, int amplifier){
        var effects = player.getActiveEffectsMap();
        MobEffect rumination = OneironautMiscRegistry.RUMINATION.get();
        if (effects.containsKey(rumination)){
            MobEffectInstance instance = effects.get(rumination);
            //adjust preexisting duration if changing effect level
            int adjustedDuration = instance.getAmplifier() != amplifier ? (int) ((double)instance.getDuration() * ((((double)instance.getAmplifier() / 4.0) + 1.0) / (((double) amplifier / 4.0) + 1.0))) : instance.getDuration();
            effects.put(rumination, new MobEffectInstance(rumination, adjustedDuration + duration, amplifier, false, false, true));
        } else {
            player.addEffect(new MobEffectInstance(rumination, duration, amplifier, false, false, true));
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (user instanceof Player player){
            applyRumination(player, DEFAULT_DURATION_RAW, 0);
        }
        return user.eat(world, stack);
    }
}
