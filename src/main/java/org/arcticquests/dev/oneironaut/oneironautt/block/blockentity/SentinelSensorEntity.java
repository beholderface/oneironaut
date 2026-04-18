package org.arcticquests.dev.oneironaut.oneironautt.block.blockentity;

import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.block.SentinelSensor;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;

import java.util.List;

public class SentinelSensorEntity extends BlockEntity {
    //private static List<Direction> directions = new ArrayList<>();
    private static final Direction[] directions = {Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH};
    public SentinelSensorEntity(BlockPos pos, BlockState state){
        super(OneironautBlockRegistry.SENTINEL_SENSOR_ENTITY.get(), pos, state);
    }

    public void tick(Level world, BlockPos pos, BlockState state){
        if ((world.getGameTime() % 4) == 0 && !(world.isClientSide)){
            MinecraftServer server = world.getServer();
            Vec3 posCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            ResourceKey<Level> worldKey = world.dimension();
            assert server != null;
            List<ServerPlayer> players = server.getPlayerList().getPlayers();
            Sentinel currentSentinel = null;
            int output = 0;
            int prospectiveOutput = 0;
            boolean great = false;
            for (ServerPlayer player : players){
                currentSentinel = IXplatAbstractions.INSTANCE.getSentinel(player);
                if (currentSentinel != null && currentSentinel.dimension().equals(worldKey) && currentSentinel.position().closerThan(posCenter, 16.0)){
                    prospectiveOutput = (int) Math.abs(currentSentinel.position().subtract(posCenter).length() - 15);
                    if (prospectiveOutput > output){
                        output = prospectiveOutput;
                        great = currentSentinel.extendsRange();
                    }
                }
            }
            BlockState newState = state;
            output = Math.min(output, 15);
            if (state.getValue(BlockStateProperties.LEVEL) != output){
                newState = state.setValue(BlockStateProperties.LEVEL, output);
            }
            if (state.getValue(SentinelSensor.GREAT) != great){
                newState = state.setValue(SentinelSensor.GREAT, great);
            }
            if (!(state.equals(newState))){
                world.setBlockAndUpdate(pos, newState);
                world.updateNeighborsAt(pos, OneironautBlockRegistry.SENTINEL_SENSOR.get());
                for (Direction dir : directions){
                    world.updateNeighborsAt(pos.relative(dir), OneironautBlockRegistry.SENTINEL_SENSOR.get());
                }
                world.updateNeighbourForOutputSignal(pos, newState.getBlock());
            }
        }
    }
}
