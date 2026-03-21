package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.OneironautConfig;
import org.arcticquests.dev.oneironaut.oneironautt.network.FireballUpdatePacket;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(targets = "at.petrak.hexcasting.common.casting.actions.spells.OpAddMotion$Spell")
public abstract class ImpulseRedirectFireballMixin {

    @Final
    @Shadow
    private Vec3 motion;
    @Final
    @Shadow
    private Entity target;

    @Unique
    private static final boolean oneironaut$redirectionEnabled = OneironautConfig.getServer().getImpulseRedirectsFireball();
    @Inject(method = "cast(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)V", at = @At(value = "RETURN", remap = false), remap = false)
    public void redirectFireball(CastingEnvironment env, CallbackInfo ci/*, @Local(ordinal = 0) Entity target*/){
        if (target instanceof AbstractHurtingProjectile explosive && oneironaut$redirectionEnabled){
            EntityDataAccessor<Float> POWER_X = SynchedEntityData.defineId(AbstractHurtingProjectile.class, EntityDataSerializers.FLOAT);
            EntityDataAccessor<Float> POWER_Y = SynchedEntityData.defineId(AbstractHurtingProjectile.class, EntityDataSerializers.FLOAT);
            EntityDataAccessor<Float> POWER_Z = SynchedEntityData.defineId(AbstractHurtingProjectile.class, EntityDataSerializers.FLOAT);
            boolean immune = false;
            if (explosive instanceof WitherSkull skull){
                //blue skulls are immune to the redirection
                if (skull.isDangerous()){
                    immune = true;
                }
            }
            if (target.getType().is(OneironautTags.Entities.impulseRedirectBlacklist)){
                immune = true;
            }
            double deltaDelta = immune ? 0 : 1;
            double deltaX = motion.x() * deltaDelta;
            double deltaY = motion.y() * deltaDelta;
            double deltaZ = motion.z() * deltaDelta;
            SynchedEntityData tracker = explosive.getEntityData();
            tracker.define(POWER_X, (float)explosive.xPower);
            tracker.define(POWER_Y, (float)explosive.yPower);
            tracker.define(POWER_Z, (float)explosive.zPower);
            Vec3 oldPower = new Vec3(explosive.xPower, explosive.yPower, explosive.zPower);
            explosive.xPower = explosive.xPower + deltaX;
            explosive.yPower = explosive.yPower + deltaY;
            explosive.zPower = explosive.zPower + deltaZ;
            Vec3 newPower = new Vec3(explosive.xPower, explosive.yPower, explosive.zPower);
            if (!immune){
                explosive.setOwner(env.getCastingEntity());
            }
            tracker.set(POWER_X, (float) (explosive.xPower + deltaX));
            tracker.set(POWER_Y, (float) (explosive.xPower + deltaY));
            tracker.set(POWER_Z, (float) (explosive.xPower + deltaZ));
            if (!newPower.equals(oldPower)){
                IXplatAbstractions.INSTANCE.sendPacketNear(explosive.position(), 128, env.getWorld(), new FireballUpdatePacket(newPower, explosive));
            }
        }
    }
}
