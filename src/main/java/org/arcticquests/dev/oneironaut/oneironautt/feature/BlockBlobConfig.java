package org.arcticquests.dev.oneironaut.oneironautt.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record BlockBlobConfig(ResourceLocation mainBlockID, int size, int squish, int falloff, int immersion) implements FeatureConfiguration {
    public static Codec<BlockBlobConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("mainblockid").forGetter(BlockBlobConfig::mainBlockID),
                    ExtraCodecs.POSITIVE_INT.fieldOf("size").forGetter(BlockBlobConfig::size),
                    ExtraCodecs.POSITIVE_INT.fieldOf("squish").forGetter(BlockBlobConfig::squish),
                    ExtraCodecs.POSITIVE_INT.fieldOf("falloff").forGetter(BlockBlobConfig::falloff),
                    ExtraCodecs.POSITIVE_INT.fieldOf("immersion").forGetter(BlockBlobConfig::immersion)
            ).apply(instance, BlockBlobConfig::new));
}
