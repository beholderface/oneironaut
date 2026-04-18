package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CastingEnvironment.class)
public interface GeneralCastEnvInvoker {
    @Invoker("extractMediaEnvironment")
    long oneironaut$extractMediaEnvironment(long cost, boolean simulate);

    @Final
    @Mutable
    @Accessor("world")
    void oneironaut$setWorld(ServerLevel world);
}
