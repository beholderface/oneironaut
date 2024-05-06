package net.oneironaut.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.registry.Registry;
import net.oneironaut.Oneironaut;
import net.oneironaut.block.*;
import net.oneironaut.casting.DetectionResistEffect;
import net.oneironaut.casting.GlowingAmbitEffect;
import net.oneironaut.casting.MissingEffect;
import net.oneironaut.casting.OvercastDamageEnchant;

public class OneironautMiscRegistry {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.FLUID_KEY);
    public static final DeferredRegister<StatusEffect> EFFECTS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.MOB_EFFECT_KEY);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.ENCHANTMENT_KEY);

    //I will not scream at my computer over this

    public static void init() {
        FLUIDS.register();
        EFFECTS.register();
        ENCHANTMENTS.register();
    }

    public static final RegistrySupplier<DetectionResistEffect> DETECTION_RESISTANCE = EFFECTS.register("detection_resistance", DetectionResistEffect::new);
    //this effect should only be used to denote an invalid status effect iota, as it does absolutely nothing but remove itself
    public static final RegistrySupplier<MissingEffect> MISSING = EFFECTS.register("missing", MissingEffect::new);
    public static final RegistrySupplier<StatusEffect> NOT_MISSING = EFFECTS.register("not_missing", GlowingAmbitEffect::new);

    public static final RegistrySupplier<ThoughtSlurry> THOUGHT_SLURRY = FLUIDS.register("thought_slurry", () -> ThoughtSlurry.STILL_FLUID /*new ThoughtSlurry.Still(OneironautThingRegistry.THOUGHT_SLURRY_ATTRIBUTES)*/);
    public static final RegistrySupplier<ThoughtSlurry> THOUGHT_SLURRY_FLOWING = FLUIDS.register("thought_slurry_flowing", () -> ThoughtSlurry.FLOWING_FLUID /*new ThoughtSlurry.Flowing(OneironautThingRegistry.THOUGHT_SLURRY_ATTRIBUTES)*/);

    public static final RegistrySupplier<OvercastDamageEnchant> OVERCAST_DAMAGE_ENCHANT = ENCHANTMENTS.register("overcast_damage", OvercastDamageEnchant::new);
}
