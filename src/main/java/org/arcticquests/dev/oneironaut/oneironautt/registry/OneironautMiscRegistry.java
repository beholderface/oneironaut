package org.arcticquests.dev.oneironaut.oneironautt.registry;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.block.ThoughtSlurry;
import org.arcticquests.dev.oneironaut.oneironautt.casting.OvercastDamageEnchant;
import org.arcticquests.dev.oneironaut.oneironautt.status.*;

public class OneironautMiscRegistry {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS,Oneironaut.MODID);
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create( ForgeRegistries.MOB_EFFECTS,Oneironaut.MODID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create( ForgeRegistries.ENCHANTMENTS,Oneironaut.MODID);


    public static final RegistryObject<DetectionResistEffect> DETECTION_RESISTANCE = EFFECTS.register("detection_resistance", DetectionResistEffect::new);
    public static final RegistryObject<GlowingAmbitEffect> NOT_MISSING = EFFECTS.register("not_missing", GlowingAmbitEffect::new);
    public static final RegistryObject<MonkfruitDelayEffect> RUMINATION = EFFECTS.register("rumination", MonkfruitDelayEffect::new);
    public static final RegistryObject<MediaDisintegrationEffect> DISINTEGRATION = EFFECTS.register("disintegration", MediaDisintegrationEffect::new);
    public static final RegistryObject<DisintegrationProtectionEffect> DISINTEGRATION_PROTECTION = EFFECTS.register("disintegration_protection", DisintegrationProtectionEffect::new);

    public static final RegistryObject<ThoughtSlurry.Still> THOUGHT_SLURRY = FLUIDS.register(
            "thought_slurry", ThoughtSlurry.Still::new);

    public static final RegistryObject<ThoughtSlurry.Flowing> THOUGHT_SLURRY_FLOWING = FLUIDS.register(
            "thought_slurry_flowing", ThoughtSlurry.Flowing::new);

    public static final RegistryObject<OvercastDamageEnchant> OVERCAST_DAMAGE_ENCHANT = ENCHANTMENTS.register("overcast_damage", OvercastDamageEnchant::new);



    public static void init(IEventBus bus) {
        FLUIDS.register(bus);
        EFFECTS.register(bus);
        ENCHANTMENTS.register(bus);
    }

}
