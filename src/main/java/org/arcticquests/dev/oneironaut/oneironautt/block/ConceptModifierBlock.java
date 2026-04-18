package org.arcticquests.dev.oneironaut.oneironautt.block;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.ConceptCoreBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.ConceptModifierBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification.ConceptModifier;
import org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification.ConceptModifierManager;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Function;

public class ConceptModifierBlock extends BaseEntityBlock implements IConceptSocketed {

    private Attribute attribute = null;
    public final ConceptModifier.ModifierType type;
    @Nullable
    public final Function<CompoundTag, Double> costCalulator;

    public ConceptModifierBlock(Properties settings, ConceptModifier.ModifierType type, @Nullable Function<CompoundTag, Double> costCalulator) {
        super(settings);
        this.type = type;
        this.costCalulator = costCalulator;
    }
    public ConceptModifierBlock(Properties settings, ConceptModifier.ModifierType type, Attribute attribute, @Nullable Function<CompoundTag, Double> costCalulator){
        super(settings);
        this.type = type;
        this.attribute = attribute;
        this.costCalulator = costCalulator;
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConceptModifierBlockEntity(pos, state);
    }

    @Override
    public EnumSet<Direction> getSockets(BlockState state) {
        return EnumSet.noneOf(Direction.class);
    }

    @Override
    public @Nullable Direction getRootFace(BlockState state) {
        return state.getValue(BlockStateProperties.FACING).getOpposite();
    }

    public Attribute getAttribute(){
        return this.attribute;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, ctx.getClickedFace());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        try {
            ConceptModifierBlockEntity be = (ConceptModifierBlockEntity) world.getBlockEntity(pos);
            be.getConceptModifier();
        } catch (Exception e){
            //do nothing
        }
    }

    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player){
        super.playerWillDestroy(world,pos,state,player);
        if (world instanceof ServerLevel serverWorld){
            ConceptModifierManager manager = ConceptModifierManager.getServerState(serverWorld.getServer());
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ConceptModifierBlockEntity modifierBlock){
                ConceptCoreBlockEntity core = this.getCore(state, pos, serverWorld, null);
                if (core != null){
                    manager.removeModifier(core.getStoredUUID(), modifierBlock.getConceptModifier());
                }
            }
        }
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return (_world, _pos, _state, _be) -> ((ConceptModifierBlockEntity)_be).tick(_world, _pos, _state);
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context){
        return switch (state.getValue(BlockStateProperties.FACING)){
            case DOWN -> Shapes.box(3.0 / 16, 2.0 / 16, 3.0 / 16, 13.0 / 16, 16.0 / 16, 13.0 / 16);
            case UP -> Shapes.box(3.0 / 16, 0.0 / 16, 3.0 / 16, 13.0 / 16, 14.0 / 16, 13.0 / 16);
            case NORTH -> Shapes.box(3.0 / 16, 3.0 / 16, 2.0 / 16, 13.0 / 16, 13.0 / 16, 16.0 / 16);
            case SOUTH -> Shapes.box(3.0 / 16, 3.0 / 16, 0.0 / 16, 13.0 / 16, 13.0 / 16, 14.0 / 16);
            case WEST -> Shapes.box(2.0 / 16, 3.0 / 16, 3.0 / 16, 16.0 / 16, 13.0 / 16, 13.0 / 16);
            case EAST -> Shapes.box(0.0 / 16, 3.0 / 16, 3.0 / 16, 14.0 / 16, 13.0 / 16, 13.0 / 16);
        };
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context){
        return switch (state.getValue(BlockStateProperties.FACING)){
            case DOWN -> Shapes.box(3.0 / 16, 2.0 / 16, 3.0 / 16, 13.0 / 16, 16.0 / 16, 13.0 / 16);
            case UP -> Shapes.box(3.0 / 16, 0.0 / 16, 3.0 / 16, 13.0 / 16, 14.0 / 16, 13.0 / 16);
            case NORTH -> Shapes.box(3.0 / 16, 3.0 / 16, 2.0 / 16, 13.0 / 16, 13.0 / 16, 16.0 / 16);
            case SOUTH -> Shapes.box(3.0 / 16, 3.0 / 16, 0.0 / 16, 13.0 / 16, 13.0 / 16, 14.0 / 16);
            case WEST -> Shapes.box(2.0 / 16, 3.0 / 16, 3.0 / 16, 16.0 / 16, 13.0 / 16, 13.0 / 16);
            case EAST -> Shapes.box(0.0 / 16, 3.0 / 16, 3.0 / 16, 14.0 / 16, 13.0 / 16, 13.0 / 16);
        };
    }
}
