package org.arcticquests.dev.oneironaut.oneironautt.casting.environments;

import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public class ForcedMediaCostEnv extends PlayerBasedCastEnv {

    public ForcedMediaCostEnv(ServerPlayer caster, InteractionHand castingHand) {
        super(caster, castingHand);
    }

    @Override
    public long extractMediaEnvironment(long cost, boolean simulate) {
        if (this.caster.isCreative())
            return 0;

        var canOvercast = this.canOvercast();
        return this.extractMediaFromInventory(cost, canOvercast, simulate);
    }

    @Override
    public InteractionHand getCastingHand() {
        return InteractionHand.MAIN_HAND;
    }

    @Override
    public FrozenPigment getPigment() {
        return null;
    }
    @Override
    protected boolean canOvercast() {
        return true;
    }
}
