package org.arcticquests.dev.oneironaut.oneironautt.block.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;

public class TransformingSkullBlockEntity extends BlockEntity {
    private static final IntegerProperty ROTATION = SkullBlock.ROTATION;
    public TransformingSkullBlockEntity(BlockPos pos, BlockState state) {
        super(OneironautBlockRegistry.TRANFORMING_SKULL_ENTITY.get(), pos, state);
    }

    public static <T extends BlockEntity> void tick(Level world, BlockPos pos, BlockState state, boolean isWall) {
        if (world instanceof ServerLevel serverWorld){
            ServerPlayer nearestPlayer = null;
            double distance = Double.MAX_VALUE;
            Vec3 posCenter = pos.getCenter();
            for (ServerPlayer checked : serverWorld.players()){
                Vec3 eyePos = checked.getEyePosition();
                if (eyePos.distanceTo(posCenter) < distance){
                    nearestPlayer = checked;
                    distance = eyePos.distanceTo(posCenter);
                    if (distance == 0){
                        break;
                    }
                }
            }
            BlockState newState = null;
            boolean foundPlayer = false;
            BlockState playerHeadState;
            BlockState zombieHeadState;
            if (!isWall){
                playerHeadState = Blocks.PLAYER_HEAD.defaultBlockState().setValue(ROTATION, state.getValue(ROTATION));
                zombieHeadState = Blocks.ZOMBIE_HEAD.defaultBlockState().setValue(ROTATION, state.getValue(ROTATION));
            } else {
                playerHeadState = Blocks.PLAYER_WALL_HEAD.defaultBlockState().setValue(WallSkullBlock.FACING, state.getValue(WallSkullBlock.FACING));
                zombieHeadState = Blocks.ZOMBIE_WALL_HEAD.defaultBlockState().setValue(WallSkullBlock.FACING, state.getValue(WallSkullBlock.FACING));
            }
            if (nearestPlayer != null && distance <= 32){
                foundPlayer = true;
                newState = playerHeadState;
            } else {
                newState = zombieHeadState;
            }
            world.setBlockAndUpdate(pos, newState);
            if (foundPlayer){
                SkullBlockEntity entity = new SkullBlockEntity(pos, newState);
                entity.setOwner(nearestPlayer.getGameProfile());
                world.setBlockEntity(entity);
            }
        }
    }
}
