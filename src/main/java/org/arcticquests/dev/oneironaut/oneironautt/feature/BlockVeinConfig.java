package org.arcticquests.dev.oneironaut.oneironautt.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record BlockVeinConfig(ResourceLocation mainBlockID, ResourceLocation carvedBlockID) implements FeatureConfiguration {
    public static Codec<BlockVeinConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("mainblockid").forGetter(BlockVeinConfig::mainBlockID),
                    ResourceLocation.CODEC.fieldOf("carvedblockid").forGetter(BlockVeinConfig::carvedBlockID))
                    .apply(instance, BlockVeinConfig::new));
}
