package org.arcticquests.dev.oneironaut.oneironautt.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry;

import static org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt.genCircle;


public class NoosphereSeaIsland extends Feature<NoosphereSeaIslandConfig> {
    public NoosphereSeaIsland(Codec<NoosphereSeaIslandConfig> configCodec){
        super (configCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoosphereSeaIslandConfig> context) {
        WorldGenLevel world = context.level();
        //ServerWorld sworld = context.getWorld().toServerWorld();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();
        NoosphereSeaIslandConfig config = context.config();

        int num = config.size();
        ResourceLocation blockID = config.blockID();

        BlockState state = BuiltInRegistries.BLOCK.get(blockID).defaultBlockState();
        Vec3i randOffset = new Vec3i((rand.nextInt(10) - 5), 0, (rand.nextInt(10) - 5));
        BlockPos scanPos = origin.offset(randOffset);
        for (int y = origin.getY(); y < 32; y++) {
            scanPos = scanPos.above();
            if ((world.getFluidState(scanPos).getType() == OneironautMiscRegistry.THOUGHT_SLURRY.get()
                    && world.getBlockState(scanPos.above()).isAir())) {
                BlockPos surfaceCenter = scanPos.above();
                //make a small basalt island
                Block[] replaceable = new Block[]{
                        OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get(),
                        Blocks.AIR
                };
                genCircle(world, scanPos, num, state, replaceable, 1.0);
                boolean generatedTrees = false;
                if (num >= 19) {
                    scanPos = scanPos.below();
                    genCircle(world, scanPos, 11, state, replaceable, 1.0);
                    if (rand.nextIntBetweenInclusive(1, 5) == 5) {
                        generatedTrees = true;
                        int treeCount = rand.nextIntBetweenInclusive(2, 6);
                        for (int i = 0; i < treeCount; i++) {
                            BlockPos treeSpot = surfaceCenter.offset(rand.nextIntBetweenInclusive(-7, 7), 0, rand.nextIntBetweenInclusive(-7, 7));
                            world.setBlock(treeSpot, OneironautBlockRegistry.EDIFIED_TREE_SPAWNER.get().defaultBlockState(), 3);
                        }
                    }
                }
                if (num >= 11) {
                    scanPos = scanPos.below();
                    genCircle(world, scanPos, 7, state, replaceable, 1.0);
                    if (rand.nextIntBetweenInclusive(1, 5) == 5 && num == 11) {
                        //generatedTrees = true;
                        int treeCount = rand.nextIntBetweenInclusive(1, 3);
                        for (int i = 0; i < treeCount; i++) {
                            BlockPos treeSpot = surfaceCenter.offset(rand.nextIntBetweenInclusive(-4, 4), 0, rand.nextIntBetweenInclusive(-4, 4));
                            world.setBlock(treeSpot, OneironautBlockRegistry.EDIFIED_TREE_SPAWNER.get().defaultBlockState(), 3);
                        }
                    }
                    /*if (rand.nextBetween(1, generatedTrees ? 3 : 25 - num) == 1){
                        BoatEntity boat = new BoatEntity(EntityType.BOAT, world.toServerWorld());
                        boat.setBoatType(BoatEntity.Type.getType(rand.nextBetween(0, 6)));
                        Vec3d surfaceCenterDouble = new Vec3d(surfaceCenter.getX(), surfaceCenter.getY(), surfaceCenter.getZ());
                        Vec3d boatPos = surfaceCenterDouble.add(new Vec3d(1.0, 0.0, 0.0)
                                .rotateY((float) Math.toRadians(rand.nextBetween(0, 360))).multiply(rand.nextBetween(num - 3, (int) (num * 1.5))));
                        boat.setPos(boatPos.x, boatPos.y, boatPos.z);
                        boat.setYaw(rand.nextBetween(-180, 180));
                        world.spawnEntity(boat);
                    }*/
                }
                //Oneironautfinal.LOGGER.info("Successfully placed an island at " + scanPos);
                return true;
            }
        }
        //Oneironautfinal.LOGGER.info("Unsuccessfully placed an island at " + scanPos);
        return false;
    }
    //public static ConfiguredFeature<NoosphereSeaIslandConfig, NoosphereSeaIsland> NOOSPHERE_SEA_ISLAND_SMALL = new ConfiguredFeature<>(
    //        (NoosphereSeaIsland) OneironautThingRegistry.NOOSPHERE_SEA_ISLAND,
    //        new NoosphereSeaIslandConfig(11, new Identifier(Oneironautfinal.MOD_ID, "noosphere_basalt"))
    //        );

}

