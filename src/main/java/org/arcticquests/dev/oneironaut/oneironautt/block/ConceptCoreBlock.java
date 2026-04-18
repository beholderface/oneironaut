package org.arcticquests.dev.oneironaut.oneironautt.block;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.ConceptCoreBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.ConceptModifierBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification.ConceptModifier;
import org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification.ConceptModifierManager;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConceptCoreBlock extends BaseEntityBlock implements IConceptSocketed {
    public ConceptCoreBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.POWERED, false));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConceptCoreBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.AXIS);
        builder.add(BlockStateProperties.POWERED);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos,
                               boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);

        if (pLevel instanceof ServerLevel world) {
            boolean prevPowered = pState.getValue(BlockStateProperties.POWERED);
            boolean isPowered = pLevel.hasNeighborSignal(pPos);

            if (prevPowered != isPowered) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.POWERED, isPowered));

                if (isPowered && pLevel.getBlockEntity(pPos) instanceof ConceptCoreBlockEntity be) {
                    ServerPlayer player = be.getStoredPlayer();
                    if (player != null){
                        List<ConceptModifierBlockEntity> modifierBlocks = be.findConceptBlocks();
                        ConceptModifierManager manager = ConceptModifierManager.getServerState(player.server);
                        manager.clearPlayerModifiers(player.getUUID());
                        try {
                            EnumSet<ConceptModifier.ModifierType> encounteredTypes = EnumSet.noneOf(ConceptModifier.ModifierType.class);
                            Set<Attribute> attributes = new HashSet<>();
                            long positiveTotal = 0L;
                            long negativeTotal = 0L;
                            Set<ConceptModifier> modifiersToApply = new HashSet<>();
                            for (ConceptModifierBlockEntity entity : modifierBlocks){
                                ConceptModifier modifier = entity.getConceptModifier();
                                boolean shouldApply = false;
                                if (modifier.type == ConceptModifier.ModifierType.ATTRIBUTE){
                                    Attribute attribute = modifier.getAttributeType();
                                    if (!attributes.contains(attribute)){
                                        attributes.add(attribute);
                                        shouldApply = true;
                                    }
                                } else if (!encounteredTypes.contains(modifier.type)){
                                    encounteredTypes.add(modifier.type);
                                    shouldApply = true;
                                }
                                if (shouldApply){
                                    modifiersToApply.add(modifier);
                                    long cost = modifier.getMediaCost(world.getBlockState(entity.getBlockPos()).getBlock());
                                    if (cost > 0){
                                        positiveTotal += cost;
                                    } else if (cost < 0) {
                                        negativeTotal -= cost;
                                    }
                                }
                            }
                            long finalCost = (long) (Math.max(positiveTotal / 10, positiveTotal - negativeTotal) * ((((double) (modifiersToApply.size() - 1)) / 2.0) + 1));
                            if ((finalCost > 0 && be.getStoredMedia() >= finalCost) || player.isCreative()){
                                be.extractMedia(finalCost);
                                for (ConceptModifier modifier : modifiersToApply){
                                    manager.addModifier(player, modifier);
                                    modifier.onApply(player);
                                }
                            }
                        } catch (Exception e){
                            //nothing
                        }
                    }
                }
            }
        }
    }

    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player){
        super.playerWillDestroy(world,pos,state,player);
        if (world instanceof ServerLevel serverWorld){
            ConceptModifierManager manager = ConceptModifierManager.getServerState(serverWorld.getServer());
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ConceptCoreBlockEntity core){
                List<ConceptModifier> modifiers = manager.getAllModifiers(core.getStoredUUID());
                if (!modifiers.isEmpty() && modifiers.get(0).corePos.equals(pos)){
                    manager.clearPlayerModifiers(core.getStoredUUID());
                }
            }
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(BlockStateProperties.AXIS, ctx.getClickedFace().getAxis());
    }

    @Override
    public EnumSet<Direction> getSockets(BlockState state) {
        return switch (state.getValue(BlockStateProperties.AXIS)) {
            case X -> EnumSet.of(Direction.EAST, Direction.WEST);
            case Y -> EnumSet.of(Direction.UP, Direction.DOWN);
            case Z -> EnumSet.of(Direction.SOUTH, Direction.NORTH);
        };
    }

    @Override
    public @Nullable Direction getRootFace(BlockState state) {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
