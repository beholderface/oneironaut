package org.arcticquests.dev.oneironaut.oneironautt.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt;

public class BlockVein extends Feature<BlockVeinConfig> {

    public BlockVein(Codec<BlockVeinConfig> configCodec){
        super (configCodec);
    }
    @Override
    public boolean place(FeaturePlaceContext<BlockVeinConfig> context) {
        WorldGenLevel world = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();
        BlockVeinConfig config = context.config();

        ResourceLocation veinID = config.mainBlockID();
        BlockState veinState = BuiltInRegistries.BLOCK.get(veinID).defaultBlockState();
        ResourceLocation carvedID = config.carvedBlockID();
        Block carvedBlock = BuiltInRegistries.BLOCK.get(carvedID);

        if (/*roll == 8*/ true){
            //Vec3i randOffset = new Vec3i((rand.nextInt(10) - 5), 0, (rand.nextInt(10) - 5));

            Vec3 direction = new Vec3(rand.nextDouble() -0.5, rand.nextDouble() -0.5, rand.nextDouble() -0.5).normalize();
            BlockPos scanPos = new BlockPos(origin.getX(), world.getMinBuildHeight(), origin.getZ());
            //determine depth of vein start
            while (world.getBlockState(scanPos).getBlock() != carvedBlock){
                scanPos = scanPos.above();
                if (scanPos.getY() > world.getMaxBuildHeight()){
                    //Oneironautfinal.LOGGER.info("Failed to find a carvable block to spawn vein in.");
                    return false;
                }
            }
            int lowestY = scanPos.getY();
            while (world.getBlockState(scanPos).getBlock() == carvedBlock){
                scanPos = scanPos.above();
                if (scanPos.getY() > world.getMaxBuildHeight()){
                    //Oneironautfinal.LOGGER.info("Failed to find the top of the carvable blocks.");
                    break;
                }
            }
            scanPos = scanPos.below();
            int highestY = scanPos.getY();
            int columnHeight = highestY - lowestY;
            int height = (int)(rand.nextGaussian() * columnHeight) + (lowestY);
            int length = (int)(rand.nextGaussian() * 15);
            Vec3 carvePoint = new Vec3((Math.floor(origin.getX() / 16.0) * 16) + 8, height, (Math.floor(origin.getZ() / 16.0) * 16) + 8);
            Vec3i carve3i = MiscAPIKt.toVec3i(carvePoint);
            BlockPos carveOrigin = new BlockPos(carve3i);
            for (int i = 0; i < length; i++){
                if ((world.getChunk(carveOrigin).getPos() == world.getChunk(new BlockPos(carve3i)).getPos()) && world.getBlockState(new BlockPos(carve3i)).getBlock() == carvedBlock && carvePoint.y > world.getMinBuildHeight()){
                    //Oneironautfinal.LOGGER.info("Origin position: " + origin + ", Origin chunk: " + world.getChunk(origin).getPos());
                    //Oneironautfinal.LOGGER.info("Target position: " + new BlockPos(carvePoint) + ", Target chunk: " + world.getChunk(new BlockPos(carvePoint)).getPos());
                    world.setBlock(new BlockPos(carve3i), veinState, 0b10);
                }
                carvePoint = carvePoint.add(direction);
                carve3i = MiscAPIKt.toVec3i(carvePoint);
            }
            //Oneironautfinal.LOGGER.info("Allegedly generated a vein at " + origin.getX() +", "+ origin.getZ());
            return true;
        }
        //Oneironautfinal.LOGGER.info("Failed to generate a vein. " + roll);
        return false;
    }
}
