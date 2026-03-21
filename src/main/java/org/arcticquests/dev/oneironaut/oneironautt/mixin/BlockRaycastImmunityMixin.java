package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.common.casting.actions.raycast.OpBlockAxisRaycast;
import at.petrak.hexcasting.common.casting.actions.raycast.OpBlockRaycast;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import kotlin.collections.CollectionsKt;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = {OpBlockRaycast.class, OpBlockAxisRaycast.class})
public abstract class BlockRaycastImmunityMixin {
    @ModifyReturnValue(method = "execute", at = @At(value = "RETURN", remap = false), remap = false)
    private List<Iota> nullIfImmune(List<Iota> original, @Local CastingEnvironment env, @Local BlockHitResult hit){
        if (original.get(0) instanceof Vec3Iota){
            BlockPos pos = hit.getBlockPos();
            //BlockPos pos = new BlockPos(vec3.getVec3());
            if (env.getWorld().getBlockState(pos).is(OneironautTags.Blocks.blocksRaycast)){
                return CollectionsKt.listOf(new NullIota());
            }
        }
        return original;
    }
}

