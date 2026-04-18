package org.arcticquests.dev.oneironaut.oneironautt.network



import at.petrak.hexcasting.common.msgs.IMessage
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Mob
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut
import org.arcticquests.dev.oneironaut.oneironautt.unbrainsweep

class UnBrainsweepPacket(val patientID : Int) : IMessage {
    override fun serialize(buf: FriendlyByteBuf?) {
        buf!!.writeInt(patientID)
    }

    override fun getFabricId(): ResourceLocation {
        return ID
    }

    companion object {
        @JvmField
        val ID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "unbrainsweep")

        @JvmStatic
        fun deserialise(buffer: ByteBuf): UnBrainsweepPacket {
            val buf = FriendlyByteBuf(buffer)

            return UnBrainsweepPacket(buf.readInt())
        }

        @JvmStatic
        fun handle(self: UnBrainsweepPacket) {
            Minecraft.getInstance().execute {
                val world = Minecraft.getInstance().level ?: return@execute
                val patient = world.getEntity(self.patientID) as Mob
                patient.unbrainsweep()
            }
        }
    }
}