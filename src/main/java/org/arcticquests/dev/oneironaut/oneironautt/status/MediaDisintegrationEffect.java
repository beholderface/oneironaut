package org.arcticquests.dev.oneironaut.oneironautt.status;


import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.casting.OvercastDamageEnchant;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MediaDisintegrationEffect extends MobEffect {
    public MediaDisintegrationEffect() {
        super(MobEffectCategory.HARMFUL, 0x8f6b94);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier){
        return duration % 30 == 0;
    }

    public final Map<LivingEntity, UUID> modifierData = new HashMap<>();

    public static final String TAG_MODIFIER_NAME = "oneironaut:disintegration";
    public static final String ATTRIBUTE_UUID_STRING = "99c9e34e-bb82-419c-82ce-7751cea942a0";
    public static final UUID ATTRIBUTE_UUID = UUID.fromString(ATTRIBUTE_UUID_STRING);
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier){
        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())){
            return;
        }
        entity.hurt(entity.damageSources().genericKill(), 0.00001f);
        entity.heal(0.00001f);
        AttributeInstance instance = entity.getAttribute(Attributes.MAX_HEALTH);
        //I stole most of this from Spectrum's life drain effect
        if (instance != null) {
            AttributeModifier currentMod = instance.getModifier(ATTRIBUTE_UUID);
            if (currentMod != null) {
                double oldValue = instance.getValue();
                instance.removeModifier(currentMod);
                double oldModValue = Math.abs(currentMod.getAmount());
                double newModValue = oldModValue + (amplifier + 1);
                AttributeModifier newModifier = new AttributeModifier(ATTRIBUTE_UUID,
                        this::getDescriptionId, -newModValue, AttributeModifier.Operation.ADDITION);
                instance.addPermanentModifier(newModifier);
                double newValue = instance.getValue();
                if (newValue != oldValue - (newModValue - oldModValue) /*if only max health could go negative, this check wouldn't be necessary*/){
                    OvercastDamageEnchant.applyMindDamage(null, entity, 9001, false);
                } else if (entity.getHealth() > entity.getMaxHealth()) {
                    entity.setHealth(entity.getMaxHealth());
                }
            }
        }
    }

    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier){
        super.removeAttributeModifiers(entity, attributes, amplifier);
        if (Oneironaut.isServerThread()){
            if (entity.level() == Oneironaut.getDeepNoosphere() && !entity.hasEffect(OneironautMiscRegistry.DISINTEGRATION_PROTECTION.get())){
                if (entity.getEffect(this) != null && entity.getEffect(this).getDuration() > 0){
                    Oneironaut.reapplicationSet.add(new Tuple<>(entity, new MobEffectInstance(this, 100, Math.min(amplifier + 1, Byte.MAX_VALUE)/*you've made it mad*/, true, true)));
                }
            }
        }
    }
}
