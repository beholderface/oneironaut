package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.misc.MediaConstants;
import net.minecraftforge.common.util.LazyOptional;
import org.arcticquests.dev.oneironaut.oneironautt.components.BoolComponent;
import org.arcticquests.dev.oneironaut.oneironautt.registry.capability.IBoolComponent;
import org.arcticquests.dev.oneironaut.oneironautt.registry.capability.WispDecorativeCapability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ram.talia.hexal.common.entities.WanderingWisp;

@Mixin(WanderingWisp.class)
public class DecorativeWispMixin {
    @Unique
    private final WanderingWisp wisp = (WanderingWisp)(Object)this;

    @Inject(method = "getMedia()J", at = @At("HEAD"), cancellable = true, remap = false)
    public void oneironaut$decorativeNoMedia(CallbackInfoReturnable<Long> cir) {
        LazyOptional<IBoolComponent> cap = wisp.getCapability(WispDecorativeCapability.INSTANCE);
        if (cap.isPresent() && cap.orElseThrow(null).getValue()) {
            cir.setReturnValue(MediaConstants.SHARD_UNIT);
        }
    }
}