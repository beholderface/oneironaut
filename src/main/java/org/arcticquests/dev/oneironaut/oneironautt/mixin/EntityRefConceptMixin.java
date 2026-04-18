package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification.ConceptModifier;
import org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification.ConceptModifierManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityIota.class)
public abstract class EntityRefConceptMixin {
    @Shadow public abstract Entity getEntity();

    @WrapMethod(method = "isTruthy", remap = false)
    public boolean isntTruthy(Operation<Boolean> original){
        if (this.getEntity() instanceof ServerPlayer player){
            ConceptModifierManager manager = ConceptModifierManager.getServerState(Oneironaut.getCachedServer());
            if (manager != null){
                if (manager.hasModifierType(player, ConceptModifier.ModifierType.FALSY_REFERENCE)){
                    return false;
                }
            }
        }
        return original.call();
    }

    @WrapMethod(method = "toleratesOther", remap = false)
    public boolean toleranceOverride(Iota that, Operation<Boolean> original){
        ConceptModifier ent1override = null;
        ConceptModifier ent2override = null;
        ConceptModifierManager manager = null;
        if (this.getEntity() instanceof ServerPlayer player){
            manager = ConceptModifierManager.getServerState(Oneironaut.getCachedServer());
            if (manager != null){
                ConceptModifier modifier = manager.getModifierByType(player, ConceptModifier.ModifierType.REFERENCE_COMPARISON);
                if (modifier != null){
                    ent1override = modifier;
                }
            }
        }
        if (that instanceof EntityIota entityIota && entityIota.getEntity() instanceof ServerPlayer player){
            if (manager == null){
                manager = ConceptModifierManager.getServerState(Oneironaut.getCachedServer());
            }
            if (manager != null){
                ConceptModifier modifier = manager.getModifierByType(player, ConceptModifier.ModifierType.REFERENCE_COMPARISON);
                if (modifier != null){
                    ent2override = modifier;
                }
            }
        }
        if (ent1override != null && ent2override == null){
            return ent1override.parameters.getBoolean(ConceptModifier.TAG_COMPARISON_OVERRIDE);
        } else if (ent1override == null && ent2override != null){
            return ent2override.parameters.getBoolean(ConceptModifier.TAG_COMPARISON_OVERRIDE);
        } else if (ent1override != null){ //intellij says that explicitly stating ent2override != null always evaluates to true here
            boolean ent1bool = ent1override.parameters.getBoolean(ConceptModifier.TAG_COMPARISON_OVERRIDE);
            boolean ent2bool = ent2override.parameters.getBoolean(ConceptModifier.TAG_COMPARISON_OVERRIDE);
            if (ent1bool == ent2bool){
                return ent1bool;
            } else {
                return false;
            }
        }
        return original.call(that);
    }
}
