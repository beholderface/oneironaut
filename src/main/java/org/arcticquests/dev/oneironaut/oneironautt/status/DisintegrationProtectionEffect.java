package org.arcticquests.dev.oneironaut.oneironautt.status;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry;

public class DisintegrationProtectionEffect extends MobEffect {
    public DisintegrationProtectionEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x8d6acc);
    }

    public void applyEffectTick(LivingEntity entity, int amplifier){
        entity.removeEffect(OneironautMiscRegistry.DISINTEGRATION.get());
    }

    public boolean isDurationEffectTick(int duration, int amplifier){
        return duration % 20 == 0;
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        super.addAttributeModifiers(entity, attributes, amplifier);
        entity.removeEffect(OneironautMiscRegistry.DISINTEGRATION.get());
    }
}
