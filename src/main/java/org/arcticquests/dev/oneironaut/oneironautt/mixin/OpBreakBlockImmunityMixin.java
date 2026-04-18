package org.arcticquests.dev.oneironaut.oneironautt.mixin;


import at.petrak.hexcasting.forge.xplat.ForgeXplatImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeXplatImpl.class)
public abstract class OpBreakBlockImmunityMixin {
    @Inject(method = "isBreakingAllowed", at = @At(value = "HEAD", remap = false), remap = false, cancellable = true)
    public void dontBreakIfImmune(ServerLevel world, BlockPos pos, BlockState state, Player player, CallbackInfoReturnable<Boolean> cir){
        if (state.is(OneironautTags.Blocks.breakImmune)){
            cir.setReturnValue(false);
        }
    }
}