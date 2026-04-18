package org.arcticquests.dev.oneironaut.oneironautt.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry;

public class BlockBlob extends Feature<BlockBlobConfig> {
    public BlockBlob(Codec<BlockBlobConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockBlobConfig> context) {
        WorldGenLevel world = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();
        BlockBlobConfig config = context.config();
        ResourceLocation mainID = config.mainBlockID();
        BlockState mainState = BuiltInRegistries.BLOCK.get(mainID).defaultBlockState();
        int size = config.size();
        double squish = 1.0 / config.squish();
        int falloff = config.falloff();
        int immersion = config.immersion();

        Vec3i randOffset = new Vec3i((rand.nextInt(10) - 5), 0, (rand.nextInt(10) - 5));
        BlockPos scanPos = origin.offset(randOffset);
        for (int y = origin.getY(); y < 32; y++) {
            scanPos = scanPos.above();
            if (world.getFluidState(scanPos).getType() == OneironautMiscRegistry.THOUGHT_SLURRY.get()
                    && world.getBlockState(scanPos.above()).isAir()) {
                // immerse it a bit
                origin = scanPos.offset(0, -rand.nextInt(0, immersion), 0);
                Vec3i cuboidDimensions = new Vec3i((size * 2) + 1, (int) (((size * 2) + 1) * squish), (size * 2) + 1);
                BlockPos cuboidOrigin = origin.offset(-size, (int) -(size * squish), -size);
                for (int i = 0; i < cuboidDimensions.getX(); i++) {
                    for (int j = 0; j < cuboidDimensions.getY(); j++) {
                        for (int k = 0; k < cuboidDimensions.getZ(); k++) {
                            Vec3i offset = new Vec3i(i, j, k);
                            Vec3i deSquishedOffset = new Vec3i(i, (int) (j / squish), k);
                            BlockPos target = cuboidOrigin.offset(offset);
                            BlockPos deSquishedTarget = cuboidOrigin.offset(deSquishedOffset);
                            if (deSquishedTarget.distManhattan(origin) < rand.nextInt(0, (cuboidDimensions.getX() + cuboidDimensions.getZ() + cuboidDimensions.getZ()) / falloff)) {
                                boolean foundNonAir = false;
                                for (Direction d : Direction.values()) {
                                    if (!world.getBlockState(target.relative(d)).isAir()) {
                                        foundNonAir = true;
                                        break;
                                    }
                                }
                                if (foundNonAir) {
                                    world.setBlock(target, mainState, 0b10);
                                }
                            }
                        }
                    }
                }
                return true;
            }
        }
        return true;
    }
}