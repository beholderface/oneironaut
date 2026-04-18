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
import org.arcticquests.dev.oneironaut.oneironautt.block.ThoughtSlurry;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry;

import java.util.Arrays;
import java.util.Iterator;

import static org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt.genCircle;


public class NoosphereSeaVolcano extends Feature<NoosphereSeaVolcanoConfig> {
    public NoosphereSeaVolcano(Codec<NoosphereSeaVolcanoConfig> configCodec){
        super (configCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoosphereSeaVolcanoConfig> context) {
        WorldGenLevel world = context.level();
        //ServerWorld sworld = context.getWorld().toServerWorld();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();
        NoosphereSeaVolcanoConfig config = context.config();

        ResourceLocation mainID = config.mainBlockID();
        ResourceLocation coreID = config.secondaryBlockID();

        BlockState mainstate = BuiltInRegistries.BLOCK.get(mainID).defaultBlockState();
        if (mainstate == null){
            throw new IllegalStateException(mainID + " could not be parsed to a valid block identifier!");
        }
        BlockState corestate = BuiltInRegistries.BLOCK.get(coreID).defaultBlockState();
        if (corestate == null){
            throw new IllegalStateException(mainID + " could not be parsed to a valid block identifier!");
        }

        //Vec3i randOffset = new Vec3i((rand.nextInt(10) - 5), 0, (rand.nextInt(10) - 5));
        BlockPos scanPos = new BlockPos((int) ((Math.floor(origin.getX() / 16.0) * 16) + 8), origin.getY(), (int) ((Math.floor(origin.getZ() / 16.0) * 16) + 8));
        //int roll = rand.nextInt(1000);
        if (true){
            for (int y = origin.getY(); y < 32; y++){
                scanPos = scanPos.above();
                if ((world.getFluidState(scanPos).getType() == OneironautMiscRegistry.THOUGHT_SLURRY.get() && world.getBlockState(scanPos.above()).isAir())){
                    //make a basalt volcano with pseudoamethyst in the middle
                    //BlockPos currentPos = scanPos;
                    //Vec3i offset;
                    //int area = (int) Math.pow(num, 2);
                    Block[] replaceable = new Block[]{
                            OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get(),
                            OneironautBlockRegistry.PSUEDOAMETHYST_BLOCK.get(),
                            OneironautBlockRegistry.NOOSPHERE_BASALT.get(),
                            Blocks.AIR
                    };
                    int y2 = -63;
                    double r = 23;
                    int placedMainBlock = 0;
                    int placedCoreBlock = 0;
                    for (; y2 < 14; y2++, r-=0.25){
                        if (r /*not diameter*/ > 3.75){
                            placedMainBlock += genCircle(world, scanPos.offset(new Vec3i(0, y2, 0)), ((int)r*2 + 1), mainstate, replaceable, 1.0);
                            if (r >= 4.5){
                                //neat fact: with a fortune III pick, this whole structure is likely to yield almost 110,000 charged amethyst worth of pseudoamethyst shards
                                placedCoreBlock += genCircle(world, scanPos.offset(new Vec3i(0, y2, 0)), ((int)r*2 + 1) - 4, corestate, replaceable, 1.0);
                            } else if (r == 4.25) {
                                genCircle(world, scanPos.offset(new Vec3i(0, y2, 0)), 5, Blocks.AIR.defaultBlockState(), replaceable, 1.0);
                                genCircle(world, scanPos.offset(new Vec3i(0, y2, 0)), 5, OneironautBlockRegistry.PSEUDOAMETHYST_CLUSTER.get().defaultBlockState(), replaceable, 1.0/8.0);
                            } else {
                                genCircle(world, scanPos.offset(new Vec3i(0, y2, 0)), 7, Blocks.AIR.defaultBlockState(), replaceable, 1.0);
                            }
                        }
                    }
                    world.setBlock(new BlockPos(scanPos.getX(), 9, scanPos.getZ()), OneironautBlockRegistry.NOOSPHERE_GATE.get().defaultBlockState(), 0b10);
                    placedCoreBlock--;
                    Iterator<Vec3i> jaggedOffsets = Arrays.stream(new Vec3i[]{
                            new Vec3i(-4, 0, 1), new Vec3i(-4, 0, -2), new Vec3i(-4, 1, -2),
                            new Vec3i(-4, 0, -2), new Vec3i(-3, 0, -3), new Vec3i(-2, 0, -3),
                            new Vec3i(-2, 1, -3), new Vec3i(-2, 0, -4), new Vec3i(1, 0, -4),
                            new Vec3i(3, 0, -3), new Vec3i(4, 0, -1), new Vec3i(4, 0, 1),
                            new Vec3i(2, 0, 3), new Vec3i(0, 0, 4), new Vec3i(-2, 0, 4),
                    }).iterator();
                    placedMainBlock += 15;
                    while (jaggedOffsets.hasNext()){
                        world.setBlock(new BlockPos(scanPos.getX(), y2-1, scanPos.getZ()).offset(jaggedOffsets.next()), mainstate, 0b10);
                    }
                    return true;
                }
            }
        }
        return false;
    }

}

