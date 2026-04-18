package org.arcticquests.dev.oneironaut.oneironautt;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber
public final class OneironautConfigEvents {
    private OneironautConfigEvents() {}

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getModId().equals(Oneironaut.MODID)) {
            OneironautConfigForgeBridge.bind();
        }
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals(Oneironaut.MODID)) {
            OneironautConfigForgeBridge.bind();
        }
    }

        public static final ResourceLocation NOOSPHERE_DIM = ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "noosphere");

        @SubscribeEvent
        public static void onSetSpawn(PlayerSetSpawnEvent event) {
            if (event.getSpawnLevel().location().equals(NOOSPHERE_DIM) && event.getNewSpawn() != null) {
                Level level = event.getEntity().level();
                if (!level.isClientSide && level.dimension().location().equals(NOOSPHERE_DIM)) {
                    if (level.getBlockState(event.getNewSpawn()).getBlock().toString().contains("respawn_anchor")) {
                        event.setCanceled(false);
                    }
                }
            }
        }

}
