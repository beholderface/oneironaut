package org.arcticquests.dev.oneironaut.oneironautt

import net.minecraft.resources.ResourceLocation


object OneironautConfig {

        interface CommonConfigAccess { }

        interface ClientConfigAccess { }

        interface ServerConfigAccess {
            //allow Noetic Gateway to teleport other players
            val planeShiftOtherPlayers : Boolean
            val planeShiftNonliving : Boolean
            //Idea Inscription expiration time, in ticks
            val ideaLifetime : Int
            val swapRequiresNoosphere : Boolean
            val swapSwapsBEs : Boolean
            val impulseRedirectsFireball : Boolean
            val infusionEternalChorus : Boolean
            val allowOverworldReflection : Boolean
            val allowNetherReflection : Boolean
            val staleIPhialLenience : Float

            companion object {
                const val DEFAULT_ALLOW_PLANESHIFT_OTHERS = false
                const val DEFAULT_ALLOW_PLANESHIT_NONLIVING = true
                const val DEFAULT_IDEA_LIFETIME = 20 * 60 * 60 //one hour
                const val DEFAULT_SWAP_NOOSPHERE = true
                const val DEFAULT_SWAP_BES = true
                const val DEFAULT_REDIRECT_FIREBALL = true
                const val DEFAULT_INFUSE_CHORUS = true
                const val DEFAULT_OVERWORLD_REFLECTION = true
                const val DEFAULT_NETHER_REFLECTION = true
                const val DEFAULT_STALE_IPHIAL_LENIENCE = 0.1f
            }
        }

        @JvmStatic
        fun anyMatch(keys: MutableList<out String>, key: ResourceLocation): Boolean {
            for (s in keys) {
                if (ResourceLocation.isValidResourceLocation(s)) {
                    val rl = ResourceLocation(s)
                    if (rl == key) {
                        return true
                    }
                }
            }
            return false
        }

        @JvmStatic
        fun noneMatch(keys: MutableList<out String>, key: ResourceLocation): Boolean {
            return !anyMatch(keys, key)
        }

        private const val throwMessage = "Attempted to access property of Dummy Config Object"

        private object DummyCommon : CommonConfigAccess {  }
        private object DummyClient : ClientConfigAccess {  }
        private object DummyServer : ServerConfigAccess {
            override val planeShiftOtherPlayers: Boolean
                get() = throw IllegalStateException(throwMessage)
            override val planeShiftNonliving: Boolean
                get() = throw IllegalStateException(throwMessage)
            override val ideaLifetime: Int
                get() = throw IllegalStateException(throwMessage)
            override val swapRequiresNoosphere: Boolean
                get() = throw IllegalStateException(throwMessage)
            override val swapSwapsBEs: Boolean
                get() = throw IllegalStateException(throwMessage)
            override val impulseRedirectsFireball: Boolean
                get() = throw IllegalStateException(throwMessage)
            override val infusionEternalChorus: Boolean
                get() = throw IllegalStateException(throwMessage)
            override val allowOverworldReflection: Boolean
                get() = throw IllegalStateException(throwMessage)
            override val allowNetherReflection: Boolean
                get() = throw IllegalStateException(throwMessage)
            override val staleIPhialLenience: Float
                get() = throw IllegalStateException(throwMessage)
        }

        @JvmStatic
        var common: CommonConfigAccess = DummyCommon
            set(access) {
                if (field != DummyCommon) {
                    Oneironaut.LOGGER.warn("CommonConfigAccess was replaced! Old {} New {}",
                        field.javaClass.name, access.javaClass.name)
                }
                field = access
            }

        @JvmStatic
        var client: ClientConfigAccess = DummyClient
            set(access) {
                if (field != DummyClient) {
                    Oneironaut.LOGGER.warn("ClientConfigAccess was replaced! Old {} New {}",
                        field.javaClass.name, access.javaClass.name)
                }
                field = access
            }

        @JvmStatic
        var server: ServerConfigAccess = DummyServer
            set(access) {
                if (field != DummyServer) {
                    Oneironaut.LOGGER.warn("ServerConfigAccess was replaced! Old {} New {}",
                        field.javaClass.name, access.javaClass.name)
                }
                field = access
            }

}