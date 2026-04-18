package org.arcticquests.dev.oneironaut.oneironautt

object OneironautConfigForgeBridge {
    @JvmStatic
    fun bind() {
        OneironautConfig.server = object : OneironautConfig.ServerConfigAccess {
            override val planeShiftOtherPlayers get() = OneironautForgeConfig.SERVER.planeShiftOtherPlayers.get()
            override val planeShiftNonliving get() = OneironautForgeConfig.SERVER.planeShiftNonliving.get()
            override val ideaLifetime get() = OneironautForgeConfig.SERVER.ideaLifetime.get()
            override val swapRequiresNoosphere get() = OneironautForgeConfig.SERVER.swapRequiresNoosphere.get()
            override val swapSwapsBEs get() = OneironautForgeConfig.SERVER.swapSwapsBEs.get()
            override val impulseRedirectsFireball get() = OneironautForgeConfig.SERVER.impulseRedirectsFireball.get()
            override val infusionEternalChorus get() = OneironautForgeConfig.SERVER.infusionEternalChorus.get()
            override val allowOverworldReflection get() = OneironautForgeConfig.SERVER.allowOverworldReflection.get()
            override val allowNetherReflection get() = OneironautForgeConfig.SERVER.allowNetherReflection.get()
            override val staleIPhialLenience get() = OneironautForgeConfig.SERVER.staleIPhialLenience.get().toFloat()
        }
    }
}