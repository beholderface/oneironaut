package org.arcticquests.dev.oneironaut.oneironautt.network



import at.petrak.hexcasting.common.msgs.IMessage
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvent
import net.minecraft.resources.ResourceLocation
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut

class SpoopyScreamPacket(val sound : SoundEvent, val pitch : Float) : IMessage {
    override fun serialize(buf: FriendlyByteBuf) {
        buf.writeResourceLocation(sound.location)
        buf.writeFloat(pitch)
    }

    override fun getFabricId(): ResourceLocation {
        return ID
    }

    companion object {
        @JvmField
        val ID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "scream")

        @JvmStatic
        fun deserialise(buffer: ByteBuf): SpoopyScreamPacket {
            val buf = FriendlyByteBuf(buffer)
            val sound = BuiltInRegistries.SOUND_EVENT.get(buf.readResourceLocation())

            return SpoopyScreamPacket(sound!!, buf.readFloat())
        }

        @JvmStatic
        fun handle(self: SpoopyScreamPacket) {
            Minecraft.getInstance().execute {
                val world = Minecraft.getInstance().level ?: return@execute
                val you = Minecraft.getInstance().player!!
                val pos = you.eyePosition.subtract(you.lookAngle)
                world.playLocalSound(pos.x, pos.y, pos.z, self.sound, SoundSource.HOSTILE, 3f, self.pitch, true)
            }
        }
    }
}