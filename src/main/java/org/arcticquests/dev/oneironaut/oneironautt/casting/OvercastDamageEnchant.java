package org.arcticquests.dev.oneironaut.oneironautt.casting;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexDamageTypes;
import at.petrak.hexcasting.ktxt.AccessorWrappers;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.network.ParticleBurstPacket;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class OvercastDamageEnchant extends Enchantment {
    private static final Map<LivingEntity, Long> cooldownMap = new HashMap<>();

    private static FrozenPigment playerlessColor = null;
    private static FrozenPigment getPlayerlessColor() {
        if (playerlessColor == null) {
            playerlessColor = FrozenPigment.DEFAULT.get();
        }
        return playerlessColor;
    }

    public OvercastDamageEnchant() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof AxeItem || super.canEnchant(stack);
    }

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level) {
        applyMindDamage(user, target, level, target.getType().is(OneironautTags.Entities.mindRenderAutospare));
    }

    public boolean checkCompatibility(Enchantment other) {
        return !(other instanceof DamageEnchantment) && this != other;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    public static void applyMindDamage(@Nullable LivingEntity user, @NotNull Entity target, int level, boolean autospare) {
        Level world = target.level();
        long currentTime = world.getGameTime();
        long lastTime = cooldownMap.getOrDefault(user, 0L);
        if (target instanceof LivingEntity livingTarget && (lastTime + 12) < currentTime && !world.isClientSide) {
            boolean brainswept = false;
            if (target instanceof Mob mob) {
                brainswept = IXplatAbstractions.INSTANCE.isBrainswept(mob);
            }
            boolean creative = target instanceof Player player && (player.isSpectator() || player.isCreative());
            DamageSource overcastSource = livingTarget.damageSources().source(HexDamageTypes.OVERCAST);
            if (!livingTarget.isInvulnerableTo(overcastSource) && !livingTarget.isDeadOrDying() && !brainswept && !creative) {
                float oldHealth = livingTarget.getHealth();
                float newHealth = oldHealth - (level / 2f);
                if (newHealth > 0) {
                    livingTarget.setHealth(newHealth);
                } else if (newHealth <= 0 && autospare && newHealth < oldHealth) {
                    livingTarget.setHealth(0.1f);
                } else {
                    livingTarget.hurt(overcastSource, Float.MAX_VALUE);
                    livingTarget.kill();
                }
                AccessorWrappers.markHurt(livingTarget);
                if (livingTarget.isAlive() && livingTarget.getHealth() <= 1f && target instanceof Mob mob) {
                    boolean whitelisted = mob.getType().is(OneironautTags.Entities.mindRenderFlayWhitelist);
                    boolean blacklisted = mob.getType().is(OneironautTags.Entities.mindRenderFlayBlacklist);
                    if ((mob.getMaxHealth() <= 100.0f || whitelisted) && !blacklisted) {
                        IXplatAbstractions.INSTANCE.setBrainsweepAddlData(mob);
                        if (user instanceof ServerPlayer player) {
                            IXplatAbstractions.INSTANCE.sendPacketNear(target.position(), 128.0, (ServerLevel) mob.level(), new ParticleBurstPacket(
                                    target.position(), new Vec3(0.0, 0.1, 0.0), 0.1, 0.025,
                                    IXplatAbstractions.INSTANCE.getPigment(player), 64, false));
                            world.playSound(null, mob, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS, 1.0f, 1.0f);
                        } else {
                            IXplatAbstractions.INSTANCE.sendPacketNear(target.position(), 128.0, (ServerLevel) mob.level(), new ParticleBurstPacket(
                                    target.position(), new Vec3(0.0, 0.1, 0.0), 0.1, 0.025,
                                    getPlayerlessColor(), 64, false));
                            world.playSound(null, mob, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.BLOCKS, 0.5f, 1.0f);
                        }
                    }
                }
            }
            if (user != null) {
                cooldownMap.put(user, currentTime);
            }
        }
    }
}