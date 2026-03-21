package org.arcticquests.dev.oneironaut.oneironautt.block;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.items.pigment.ItemDyePigment;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class InactiveSlipwayBlock extends Block {
    public InactiveSlipwayBlock(Properties settings) {
        super(settings);
    }
    private static List<Integer> colors;
    public static void init(){
        RandomSource random = RandomSource.create();
        List<Integer> colorList = new ArrayList<>();
        for (int i = 0; i < 32; i++){
            for(ItemDyePigment pigment : HexItems.DYE_PIGMENTS.values()){
                FrozenPigment frozen = new FrozenPigment(new ItemStack(pigment), Util.NIL_UUID);
                colorList.add(ram.talia.hexal.api.FunUtilsKt.nextColour(frozen, random));
            }
        }
        colors = colorList;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context){
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.INVISIBLE;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (colors != null){
            Vec3 particleCenter = Vec3.atCenterOf(pos);
            for(ItemDyePigment pigment : HexItems.DYE_PIGMENTS.values()){
                int color = colors.get(random.nextInt(colors.size()));
                Vec3 particlePoint = new Vec3(
                        (particleCenter.x + 0.35 * random.nextGaussian()),
                        (particleCenter.y + 0.35 * random.nextGaussian()),
                        (particleCenter.z + 0.35 * random.nextGaussian()));
                world.addParticle(new ConjureParticleOptions(color),
                        particlePoint.x,
                        particlePoint.y,
                        particlePoint.z,
                        0.0125 * (random.nextDouble() - 0.5),
                        0.0125 * (random.nextDouble() - 0.5),
                        0.0125 * (random.nextDouble() - 0.5));
            }
        }
    }
}
