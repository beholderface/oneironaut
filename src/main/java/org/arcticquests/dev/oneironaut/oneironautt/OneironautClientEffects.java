package org.arcticquests.dev.oneironaut.oneironautt;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Oneironaut.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OneironautClientEffects {
    @SubscribeEvent
    public static void onRegisterDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath("oneironaut", "noosphere"), new NoosphereDimensionEffects());
        event.register(ResourceLocation.fromNamespaceAndPath("oneironaut", "deep_noosphere"), new DeepNoosphereDimensionEffects());
        Oneironaut.LOGGER.info("Registered Noosphere/DeepNoosphere dimension effects");
    }
}