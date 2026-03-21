package org.arcticquests.dev.oneironaut.oneironautt.network

import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.effect.MobEffects
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class HoverliftAntiDesyncPacket {
    companion object {
        @JvmStatic
        fun encode(msg: HoverliftAntiDesyncPacket, buf: FriendlyByteBuf) {
        }

        @JvmStatic
        fun decode(buf: FriendlyByteBuf): HoverliftAntiDesyncPacket {
            return HoverliftAntiDesyncPacket()
        }

        @JvmStatic
        fun handle(msg: HoverliftAntiDesyncPacket, ctxSupplier: Supplier<NetworkEvent.Context>) {
            val ctx = ctxSupplier.get()
            ctx.enqueueWork {
                val mc = Minecraft.getInstance()
                val player = mc.player
                if (player != null && player.hasEffect(MobEffects.SLOW_FALLING)) {
                    player.removeEffect(MobEffects.SLOW_FALLING)
                }
            }
            ctx.packetHandled = true
        }
    }
}