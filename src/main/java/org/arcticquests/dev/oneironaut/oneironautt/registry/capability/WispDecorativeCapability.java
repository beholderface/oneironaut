package org.arcticquests.dev.oneironaut.oneironautt.registry.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class WispDecorativeCapability {
    private WispDecorativeCapability() {}

    public static final Capability<IBoolComponent> INSTANCE =
            CapabilityManager.get(new CapabilityToken<>() {});
}