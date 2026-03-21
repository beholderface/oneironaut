package org.arcticquests.dev.oneironaut.oneironautt.block;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.arcticquests.dev.oneironaut.oneironautt.network.OneironautNetwork;
import org.arcticquests.dev.oneironaut.oneironautt.network.ParticleBurstPacket;

import java.util.UUID;

public class MediaGelBlock extends HalfTransparentBlock {
    public MediaGelBlock(Properties settings) {
        super(settings);
    }
    protected static final VoxelShape COLLISION_SHAPE = Block.box(2.0, 2.0, 2.0, 14.0, 12.0, 14.0);
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context){
        return Shapes.block();
    }

    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) {
        return Shapes.block();
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 0.2F;
    }

    private static FrozenPigment getPurpleColorizer() {
        return new FrozenPigment(HexItems.DYE_PIGMENTS.get(DyeColor.PURPLE).getDefaultInstance(), new UUID(0, 0));
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if(!entity.isSteppingCarefully() && !world.isClientSide && (world.getGameTime() % 10) == 0 && entity.showVehicleHealth()){
            Vec3 targetPos = entity.position().add(0, 0.2, 0);
            OneironautNetwork.CHANNEL.send(
                    net.minecraftforge.network.PacketDistributor.NEAR.with(
                            () -> new net.minecraftforge.network.PacketDistributor.TargetPoint(
                                    targetPos.x, targetPos.y, targetPos.z,
                                    32.0, world.dimension()
                            )
                    ),
                    new ParticleBurstPacket(
                            targetPos,
                            new Vec3(0, -0.02, 0),
                            0.2, 0,
                            getPurpleColorizer(),
                            20, false
                    )
            );
        }
        super.stepOn(world, pos, state, entity);
    }
}