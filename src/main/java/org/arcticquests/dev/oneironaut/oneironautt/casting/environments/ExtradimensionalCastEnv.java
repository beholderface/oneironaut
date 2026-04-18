package org.arcticquests.dev.oneironaut.oneironautt.casting.environments;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.mixin.GeneralCastEnvInvoker;
import org.arcticquests.dev.oneironaut.oneironautt.mixin.PlayerCastEnvInvoker;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ExtradimensionalCastEnv extends PlayerBasedCastEnv {

    public final PlayerBasedCastEnv parentEnv;
    public final int depth;
    public final CastingVM vm;

    public ExtradimensionalCastEnv(ServerPlayer caster, PlayerBasedCastEnv parent, ServerLevel target, @Nullable CastingVM existingVM) {
        super(caster, parent.getCastingHand());
        this.parentEnv = parent;
        ((GeneralCastEnvInvoker)this).oneironaut$setWorld(target);
        if (parentEnv instanceof ExtradimensionalCastEnv extradimensionalCastEnv){
            this.depth = extradimensionalCastEnv.depth + 1;
        } else {
            this.depth = 1;
        }
        this.vm = existingVM != null ? existingVM : CastingVM.empty(this);
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
    protected long extractMediaEnvironment(long cost, boolean simulate) {
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
    public boolean isVecInRangeEnvironment(Vec3 vec) {
        if (this.world == parentEnv.getWorld()){
            return parentEnv.isVecInRangeEnvironment(vec);
        }
        var sentinel = HexAPI.instance().getSentinel(this.caster);
        double sentinelRadius = this.caster.getAttributeValue(HexAttributes.SENTINEL_RADIUS);
        return sentinel != null
                && sentinel.extendsRange()
                && this.world.dimension() == sentinel.dimension()
                // adding 0.00000000001 to avoid machine precision errors at specific angles
                && vec.distanceToSqr(sentinel.position()) <= sentinelRadius * sentinelRadius + 0.00000000001;
    }

    @Override
    protected boolean canOvercast() {
        return ((PlayerCastEnvInvoker)parentEnv).oneironaut$canOvercast();
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

    protected void sendMishapMsgToPlayer(OperatorSideEffect.DoMishap mishap) {
        ((PlayerCastEnvInvoker)parentEnv).oneironaut$sendMishapMsgToPlayer(mishap);
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
}
