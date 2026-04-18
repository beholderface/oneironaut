package org.arcticquests.dev.oneironaut.oneironautt.block;

import at.petrak.hexcasting.api.block.circle.BlockAbstractImpetus;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.common.lib.HexSounds;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.SentinelTrapImpetusEntity;
import org.jetbrains.annotations.Nullable;

public class SentinelTrapImpetus extends BlockAbstractImpetus {

    public SentinelTrapImpetus(Properties settings){
        super(settings);
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SentinelTrapImpetusEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide ? null : (_world, _pos, _state, _be) -> ((SentinelTrapImpetusEntity)_be).tick(_world, _pos, _state);
    }

    @Override
    public InteractionResult use(BlockState pState, Level world, BlockPos pPos, Player pPlayer, InteractionHand pHand,
                            BlockHitResult pHit) {
        if (world.getBlockEntity(pPos) instanceof SentinelTrapImpetusEntity tile) {
            var usedStack = pPlayer.getItemInHand(pHand);
            var datumContainer = IXplatAbstractions.INSTANCE.findDataHolder(usedStack);
            if (datumContainer != null) {
                if (!world.isClientSide && world instanceof ServerLevel level)
                    /*if (!world.isClient) */{
                        var stored = datumContainer.readIota(level);
                        if (stored instanceof EntityIota eieio) {
                            var entity = eieio.getEntity();
                            if (entity instanceof Player player) {
                                // phew, we got something
                                tile.setPlayer(player.getGameProfile(), entity.getUUID());
                                level.sendBlockUpdated(pPos, pState, pState, Block.UPDATE_CLIENTS);

                                world.playSound(pPlayer, pPos, HexSounds.IMPETUS_REDSTONE_DING,
                                        SoundSource.BLOCKS, 1f, 1f);
                            }
                        }
                    }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }
}
