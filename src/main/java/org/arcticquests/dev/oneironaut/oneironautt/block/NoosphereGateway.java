package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.NoosphereGateEntity;

import javax.annotation.Nullable;

//import static net.oneironaut.MiscAPIKt.stringToWorld;
//import static net.oneironaut.MiscAPIKt.clientPlayertoServerPlayer;

public class NoosphereGateway extends BaseEntityBlock{
    public NoosphereGateway(Properties settings){
        super(settings);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        //Oneironautfinal.LOGGER.info("Creating blockentity.");
        return new NoosphereGateEntity(pos, state);
    }
    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.MODEL;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        //if (type != OneironautThingRegistry.NOOSPHERE_GATE_ENTITY.get()) return null;
        return (_world, _pos, _state, _be) -> ((NoosphereGateEntity)_be).tick(_world, _pos, _state);
    }

}

