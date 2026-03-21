package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(IotaType.class)
public interface IotaTypeInvoker {
    @Invoker("isTooLargeToSerialize")
    static boolean oneironaut$isTooLarge(Iterable<Iota> examinee, int startingCount) {
        throw new AssertionError();
    }
}
