package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerBasedCastEnv.class)
public interface PlayerCastEnvInvoker {
    @Invoker("extractMediaFromInventory")
    long oneironaut$extractMediaFromInventory(long costLeft, boolean allowOvercast, boolean simulate);
    //naming it just canOvercast results in an infinite loop
    @Invoker("canOvercast")
    boolean oneironaut$canOvercast();
    @Invoker("sendMishapMsgToPlayer")
    void oneironaut$sendMishapMsgToPlayer(OperatorSideEffect.DoMishap mishap);
    @Invoker("isCreativeMode")
    boolean oneironaut$isCreativeMode();

}
