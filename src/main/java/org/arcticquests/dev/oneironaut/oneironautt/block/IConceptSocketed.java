package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.ConceptCoreBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.ConceptModifierBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface IConceptSocketed {
    public EnumSet<Direction> getSockets(BlockState state);

    public default List<ConceptModifierBlockEntity> getConnectedModifiers(BlockState state, BlockPos pos, Level world, @Nullable Set<BlockPos> alreadyChecked){
        List<ConceptModifierBlockEntity> modifiers = new ArrayList<>();
        if (alreadyChecked == null){
            alreadyChecked = new HashSet<>();
        }
        for (Direction dir : this.getSockets(state)){
            BlockPos checkedPos = pos.relative(dir);
            if (!alreadyChecked.contains(checkedPos)){
                alreadyChecked.add(checkedPos);
                BlockState checkedState = world.getBlockState(checkedPos);
                if (checkedState.getBlock() instanceof IConceptSocketed socketed){
                    modifiers.addAll(socketed.getConnectedModifiers(checkedState, checkedPos, world, alreadyChecked));
                }
                if (checkedState.getBlock() instanceof ConceptModifierBlock modifierBlock){
                    modifiers.add((ConceptModifierBlockEntity) world.getBlockEntity(checkedPos));
                }
            }
        }
        return modifiers;
    }

    @Nullable
    public Direction getRootFace(BlockState state);

    @Nullable
    public default ConceptCoreBlockEntity getCore(BlockState state, BlockPos pos, Level world, @Nullable Set<BlockPos> alreadyVisited){
        if (alreadyVisited == null){
            alreadyVisited = new HashSet<>();
        }
        Block blockType = state.getBlock();
        while ((!(blockType instanceof ConceptCoreBlock)) && !alreadyVisited.contains(pos)){
            if (blockType instanceof IConceptSocketed socketed){
                Direction dir = socketed.getRootFace(state);
                if (dir != null){
                    pos = pos.relative(dir);
                    state = world.getBlockState(pos);
                    blockType = state.getBlock();
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        if (blockType instanceof ConceptCoreBlock){
            return (ConceptCoreBlockEntity) world.getBlockEntity(pos);
        }
        return null;
    }
}
