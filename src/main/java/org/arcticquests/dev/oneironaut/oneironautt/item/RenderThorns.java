package org.arcticquests.dev.oneironaut.oneironautt.item;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.arcticquests.dev.oneironaut.oneironautt.casting.OvercastDamageEnchant;

public class RenderThorns extends Item {
    public RenderThorns(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        OvercastDamageEnchant.applyMindDamage(null, user, 2, false);
        user.hurt(user.damageSources().sweetBerryBush(), user.isAlwaysTicking() ? 0.001f : 0f);
        user.getCooldowns().addCooldown(this, 10);
        if (user instanceof ServerPlayer serverPlayer){
            PlayerAdvancements tracker = serverPlayer.getAdvancements();
            Advancement ouchie = world.getServer().getAdvancements().getAdvancement(ResourceLocation.tryBuild("oneironaut","prick_self"));
            if (!tracker.getOrStartProgress(ouchie).isDone()) {
                tracker.award(ouchie, "grant");
            }
        }
        return InteractionResultHolder.success(user.getItemInHand(hand));
    }
}
