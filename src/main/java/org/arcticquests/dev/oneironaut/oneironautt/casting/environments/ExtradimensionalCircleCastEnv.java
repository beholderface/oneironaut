package org.arcticquests.dev.oneironaut.oneironautt.casting.environments;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.casting.circles.CircleExecutionState;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt;
import org.arcticquests.dev.oneironaut.oneironautt.block.ExtradimensionalBoundaryLocus;
import org.arcticquests.dev.oneironaut.oneironautt.mixin.GeneralCastEnvInvoker;
import org.arcticquests.dev.oneironaut.oneironautt.mixin.PlayerCastEnvInvoker;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ExtradimensionalCircleCastEnv extends CircleCastEnv {

    public final CircleCastEnv parentEnv;
    public final int depth;
    public final CastingVM vm;
    public final AABB targetDimBounds;
    public final ServerLevel originalWorld;

    public ExtradimensionalCircleCastEnv(CircleCastEnv parent, ServerLevel target, @Nullable CastingVM existingVM,
                                         CircleExecutionState existingState, Set<BlockPos> visitedLoci) {
        super(parent.getWorld(), existingState);
        this.parentEnv = parent;
        ((GeneralCastEnvInvoker)this).oneironaut$setWorld(target);
        if (parentEnv instanceof ExtradimensionalCircleCastEnv extradimensionalCastEnv){
            this.depth = extradimensionalCastEnv.depth + 1;
            this.originalWorld = extradimensionalCastEnv.originalWorld;
        } else {
            this.depth = 1;
            this.originalWorld = parent.getWorld();
        }
        this.vm = existingVM != null ? existingVM : CastingVM.empty(this);
        AABB reallyFuckingTinyBox = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        if (visitedLoci.size() != 8){
            this.targetDimBounds = reallyFuckingTinyBox;
        } else {
            List<BlockPos> lociList = visitedLoci.stream().toList();
            BlockPos corner1 = lociList.get(0);
            double volumeRecord = -1.0;
            AABB biggestBoxFound = reallyFuckingTinyBox;
            for (BlockPos pos : lociList){
                AABB testBox = new AABB(corner1, pos);
                if (MiscAPIKt.volume(testBox) > volumeRecord){
                    biggestBoxFound = testBox;
                    volumeRecord = MiscAPIKt.volume(testBox);
                }
            }
            boolean validBox = true;
            for (Vec3 vec : MiscAPIKt.corners(biggestBoxFound)){
                BlockPos pos = new BlockPos(MiscAPIKt.toVec3i(vec));
                if (!(visitedLoci.contains(pos) && originalWorld.getBlockState(pos).getBlock() instanceof ExtradimensionalBoundaryLocus)){
                    validBox = false;
                    break;
                }
            }
            if (validBox){
                double scaleMultiplier = this.parentEnv.getWorld().dimensionType().coordinateScale() / this.world.dimensionType().coordinateScale();
                Vec3 targetMinCoord = new Vec3(biggestBoxFound.minX * scaleMultiplier, biggestBoxFound.minY, biggestBoxFound.minZ * scaleMultiplier);
                Vec3 targetMaxCoord = new Vec3(biggestBoxFound.maxX * scaleMultiplier, biggestBoxFound.maxY, biggestBoxFound.maxZ * scaleMultiplier);
                this.targetDimBounds = new AABB(targetMinCoord, targetMaxCoord);
            } else {
                this.targetDimBounds = reallyFuckingTinyBox;
            }
        }
        /*if (this.targetDimBounds == reallyFuckingTinyBox){
            Oneironautfinal.LOGGER.info("tiny-ass box");
        } else {
            Oneironautfinal.LOGGER.info("successful box");
        }*/
    }

    @Override
    public void postExecution(CastResult result) {
        parentEnv.postExecution(result);
    }

    @Override
    public boolean replaceItem(Predicate<ItemStack> stackOk, ItemStack replaceWith, @Nullable InteractionHand hand) {
        return parentEnv.replaceItem(stackOk, replaceWith, hand);
    }

    @Override
    public long extractMediaEnvironment(long cost, boolean simulate) {
        double multiplier = 1.25;
        return ((GeneralCastEnvInvoker)parentEnv).oneironaut$extractMediaEnvironment((long) (cost * multiplier), simulate);
    }

    @Override
    public InteractionHand getCastingHand() {
        return parentEnv.getCastingHand();
    }

    @Override
    public FrozenPigment getPigment() {
        return parentEnv.getPigment();
    }

    @Override
    public LivingEntity getCastingEntity() {
        return parentEnv.getCastingEntity();
    }

    @Override
    public ServerPlayer getCaster() {
        return parentEnv.getCaster();
    }

    @Override
    public @Nullable BlockEntityAbstractImpetus getImpetus() {
        var entity = this.originalWorld.getBlockEntity(execState.impetusPos);
        if (entity instanceof BlockEntityAbstractImpetus)
            return (BlockEntityAbstractImpetus) entity;
        return null;
    }

    @Override
    public boolean isVecInRangeEnvironment(Vec3 vec) {
        ServerPlayer caster = this.execState.getCaster(this.world);
        if (this.world == parentEnv.getWorld()){
            return parentEnv.isVecInRangeEnvironment(vec);
        }
        boolean withinSentinel = false;
        if (caster != null){
            double sentinelRadius = caster.getAttributeValue(HexAttributes.SENTINEL_RADIUS);
            var sentinel = HexAPI.instance().getSentinel(caster);
            withinSentinel = sentinel != null
                    && sentinel.extendsRange()
                    && this.world.dimension() == sentinel.dimension()
                    // adding 0.00000000001 to avoid machine precision errors at specific angles
                    && vec.distanceToSqr(sentinel.position()) <= sentinelRadius * sentinelRadius + 0.00000000001;
        }
        return withinSentinel || this.targetDimBounds.contains(vec);
    }

    @Override
    public @Nullable FrozenPigment setPigment(@Nullable FrozenPigment pigment) {
        return parentEnv.setPigment(pigment);
    }

    @Override
    public void produceParticles(ParticleSpray particles, FrozenPigment pigment) {
        parentEnv.produceParticles(particles, pigment);
    }

    @Override
    public Vec3 mishapSprayPos() {
        return parentEnv.mishapSprayPos();
    }

    @Override
    public MishapEnvironment getMishapEnvironment() {
        return parentEnv.getMishapEnvironment();
    }

    @Override
    protected boolean isCreativeMode() {
        // not sure what the diff between this and isCreative() is
        return ((PlayerCastEnvInvoker)parentEnv).oneironaut$isCreativeMode();
    }

    @Override
    public void printMessage(Component message) {
        parentEnv.printMessage(message);
    }

    @Override
    public boolean hasEditPermissionsAtEnvironment(BlockPos pos){
        if (this.getCaster() != null){
            return this.getCaster().gameMode.getGameModeForPlayer() != GameType.ADVENTURE && this.world.mayInteract(this.getCaster(), pos);
        } else {
            return true;
        }
    }
}
