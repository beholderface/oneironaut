package org.arcticquests.dev.oneironaut.oneironautt.registry;


import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.feature.*;

public class OneironautFeatureRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES,Oneironaut.MODID);
    public static void init(IEventBus eventBus){
        FEATURES.register(eventBus);
    }
    //islands
    public static final RegistryObject<Feature<NoosphereSeaIslandConfig>> NOOSPHERE_SEA_ISLAND = FEATURES.register("noosphere_sea_island", () -> new NoosphereSeaIsland(NoosphereSeaIslandConfig.CODEC));
    public static final RegistryObject<Feature<NoosphereSeaVolcanoConfig>> NOOSPHERE_SEA_VOLCANO = FEATURES.register("noosphere_sea_volcano", () -> new NoosphereSeaVolcano(NoosphereSeaVolcanoConfig.CODEC));
    public static final RegistryObject<Feature<BlockVeinConfig>> BLOCK_VEIN = FEATURES.register("block_vein", () -> new BlockVein(BlockVeinConfig.CODEC));
    public static final RegistryObject<Feature<BlockBlobConfig>> BLOCK_BLOB = FEATURES.register("block_blob", () -> new BlockBlob(BlockBlobConfig.CODEC));
}
