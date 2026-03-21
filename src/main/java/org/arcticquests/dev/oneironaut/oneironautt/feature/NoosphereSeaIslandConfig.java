package org.arcticquests.dev.oneironaut.oneironautt.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record NoosphereSeaIslandConfig(int size, ResourceLocation blockID) implements FeatureConfiguration {
    public static Codec<NoosphereSeaIslandConfig> CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            ExtraCodecs.POSITIVE_INT.fieldOf("size").forGetter(NoosphereSeaIslandConfig::size),
                            ResourceLocation.CODEC.fieldOf("blockid").forGetter(NoosphereSeaIslandConfig::blockID))
                    .apply(instance, NoosphereSeaIslandConfig::new));
}
