package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import ram.talia.hexal.common.blocks.BlockSlipway;
import ram.talia.hexal.common.blocks.entity.BlockEntitySlipway;
import ram.talia.hexal.common.lib.HexalBlockEntities;
import ram.talia.hexal.common.lib.HexalBlocks;

public class SlipwaySuppressorBlock extends Block {
    public static final BooleanProperty REDSTONE_POWERED = BlockStateProperties.POWERED;
    public SlipwaySuppressorBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(REDSTONE_POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(REDSTONE_POWERED);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return world.getBlockState(pos.above()).getBlock() instanceof BlockSlipway ? 15 : 0;
    }

    //it doesn't actually, I just want redstone to point at it
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(REDSTONE_POWERED, pContext.getLevel().hasNeighborSignal(pContext.getClickedPos()));
    }

    private boolean breaking = false;
    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos pFromPos,
                               boolean pIsMoving) {
        super.neighborChanged(state, world, pos, block, pFromPos, pIsMoving);
        try {
            boolean currentlyPowered = state.getValue(REDSTONE_POWERED);
            boolean detectedPower = world.hasNeighborSignal(pos);
            if (currentlyPowered != detectedPower) {
                world.setBlock(pos, state.setValue(REDSTONE_POWERED, detectedPower), 2);
                currentlyPowered = detectedPower;
            }
            BlockPos up = pos.above();
            boolean slipwayDisabled = world.getBlockState(up).getBlock() instanceof InactiveSlipwayBlock;
            if (currentlyPowered != slipwayDisabled && !breaking){
                if (slipwayDisabled){
                    reactivateSlipway(world, up);
                } else if (world.getBlockState(up).getBlock() instanceof BlockSlipway) {
                    world.setBlockAndUpdate(up, OneironautBlockRegistry.INACTIVE_SLIPWAY.get().defaultBlockState());
                }
            }
            world.updateNeighbourForOutputSignal(pos, this);
        } catch (Throwable t){
            //just making sure that some weird bug doesn't suppress the suppressors
        }
        breaking = false;
    }

    public static void reactivateSlipway(Level world, BlockPos target){
        world.setBlockAndUpdate(target, HexalBlocks.SLIPWAY.defaultBlockState());
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean(BlockEntitySlipway.TAG_IS_ACTIVE, true);
        nbt.putLong(BlockEntitySlipway.TAG_NEXT_SPAWN_TICK, world.getGameTime() + 100L);
        BlockEntitySlipway.addEntityType(nbt, HexalBlockEntities.SLIPWAY);
        BlockEntitySlipway slipwayEntity = (BlockEntitySlipway) BlockEntitySlipway.loadStatic(target, HexalBlocks.SLIPWAY.defaultBlockState(), nbt);
        if (slipwayEntity == null){
            return;
        }
        world.setBlockEntity(slipwayEntity);
        slipwayEntity.setChanged();
    }

    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(world, pos, state, player);
        breaking = true;
        if (world.getBlockState(pos.above()).getBlock() instanceof InactiveSlipwayBlock){
            reactivateSlipway(world, pos.above());
        }
    }
}
