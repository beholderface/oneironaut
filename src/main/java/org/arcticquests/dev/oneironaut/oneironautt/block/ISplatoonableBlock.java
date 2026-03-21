package org.arcticquests.dev.oneironaut.oneironautt.block;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Method;
//sam is cool
public interface ISplatoonableBlock {
    // soft implemented on blocks in other mods that can be splatted
    public void splatPigmentOntoBlock(Level world, BlockPos pos, FrozenPigment pigment);

    // use reflection to check if the block has the method
    public static boolean isSplatable(Block block){
        try {
            block.getClass().getMethod("splatPigmentOntoBlock", Level.class, BlockPos.class, FrozenPigment.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    // use reflection to actually call the method on the block
    public static void splatBlock(Level world, BlockPos pos, FrozenPigment pigment){
        Block block = world.getBlockState(pos).getBlock();
        try {
            Method splatMethod = block.getClass().getMethod("splatPigmentOntoBlock", Level.class, BlockPos.class, FrozenPigment.class);
            splatMethod.invoke(block, world, pos, pigment);
        } catch (Exception ignored) {

        }
    }
}