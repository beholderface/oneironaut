package org.arcticquests.dev.oneironaut.oneironautt;

import net.minecraftforge.common.ForgeConfigSpec

object OneironautForgeConfig {
    val SERVER_SPEC: ForgeConfigSpec
    val SERVER: Server

    class Server(builder: ForgeConfigSpec.Builder) {
        val planeShiftOtherPlayers = builder.define("planeShiftOtherPlayers", false)
        val planeShiftNonliving = builder.define("planeShiftNonliving", true)
        val ideaLifetime = builder.defineInRange("ideaLifetime", 20 * 60 * 60, 1, Int.MAX_VALUE)
        val swapRequiresNoosphere = builder.define("swapRequiresNoosphere", true)
        val swapSwapsBEs = builder.define("swapSwapsBEs", true)
        val impulseRedirectsFireball = builder.define("impulseRedirectsFireball", true)
        val infusionEternalChorus = builder.define("infusionEternalChorus", true)
        val allowOverworldReflection = builder.define("allowOverworldReflection", true)
        val allowNetherReflection = builder.define("allowNetherReflection", true)
        val staleIPhialLenience = builder.defineInRange("staleIPhialLenience", 0.1, 0.0, 1.0)
    }

    init {
        val b = ForgeConfigSpec.Builder()
        SERVER = Server(b)
        SERVER_SPEC = b.build()
    }
}