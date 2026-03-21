package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CastingVM.class)
public class MaxStackSizeMixinVM {

    @Unique
    private final CastingVM oneironaut$vm = (CastingVM) (Object) this;

    @WrapOperation(method = "queueExecuteAndWrapIotas", at = @At(value = "INVOKE",
            target = "Lat/petrak/hexcasting/api/casting/iota/IotaType;isTooLargeToSerialize(Ljava/lang/Iterable;)Z", remap = false), remap = false)
    public boolean isTooLarge(Iterable<Iota> examinee, Operation<Boolean> original){
        return MiscAPIKt.handleIncreasedStackLimit(oneironaut$vm.getEnv(), oneironaut$vm.getImage(), examinee, original);
    }
}
