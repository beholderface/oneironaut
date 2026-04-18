package org.arcticquests.dev.oneironaut.oneironautt.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record NoosphereSeaVolcanoConfig(ResourceLocation mainBlockID, ResourceLocation secondaryBlockID) implements FeatureConfiguration {
    public static Codec<NoosphereSeaVolcanoConfig> CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            ResourceLocation.CODEC.fieldOf("mainblockid").forGetter(NoosphereSeaVolcanoConfig::mainBlockID),
                            ResourceLocation.CODEC.fieldOf("secondaryblockid").forGetter(NoosphereSeaVolcanoConfig::secondaryBlockID))
                    .apply(instance, NoosphereSeaVolcanoConfig::new));
}
