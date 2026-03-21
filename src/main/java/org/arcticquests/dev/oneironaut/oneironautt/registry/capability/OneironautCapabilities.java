package org.arcticquests.dev.oneironaut.oneironautt.registry.capability;


import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ram.talia.hexal.common.entities.WanderingWisp;

@Mod.EventBusSubscriber
public final class OneironautCapabilities {
    private OneironautCapabilities() {}

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(IBoolComponent.class);
    }


    @SubscribeEvent
    public static void attachEntityCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof WanderingWisp) {
            event.addCapability(WispDecorativeProvider.ID, new WispDecorativeProvider());
        }
    }
}