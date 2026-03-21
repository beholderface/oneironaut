package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.casting.actions.selectors.OpGetEntitiesBy;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ram.talia.moreiotas.common.casting.actions.types.OpGetEntitiesByDyn;

import java.util.Collection;

//TIL that I can target multiple classes with one mixin class
@Mixin(value = {OpGetEntitiesBy.class, OpGetEntitiesByDyn.class})
public abstract class ZoneDistImmunityMixin {
    @WrapOperation(method = "execute", at = @At(value = "INVOKE", target = "Ljava/util/Collection;add(Ljava/lang/Object;)Z", remap = false), remap = false)
    private boolean ignoreImmune(Collection<Iota> instance, Object o, Operation<Boolean> original){
        EntityIota e = (EntityIota) o;
        if (e.getEntity() instanceof LivingEntity le){
            if (le.hasEffect(OneironautMiscRegistry.DETECTION_RESISTANCE.get())){
                return false;
            }
        }
        return original.call(instance, o);
    }
}