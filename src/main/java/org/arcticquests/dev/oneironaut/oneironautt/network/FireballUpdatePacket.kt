package org.arcticquests.dev.oneironaut.oneironautt.network
import at.petrak.hexcasting.common.msgs.IMessage
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import net.minecraft.world.entity.Entity
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut
import java.util.ConcurrentModificationException


class FireballUpdatePacket(val targetVelocity : Vec3, val entity : AbstractHurtingProjectile?) : IMessage {
    override fun serialize(buf: FriendlyByteBuf) {
        buf.writeDouble(targetVelocity.x)
        buf.writeDouble(targetVelocity.y)
        buf.writeDouble(targetVelocity.z)
        entity?.x?.let { buf.writeDouble(it) }
        entity?.y?.let { buf.writeDouble(it) }
        entity?.z?.let { buf.writeDouble(it) }
        entity?.id?.let { buf.writeInt(it) }
    }

    override fun getFabricId() = ID

    companion object {
        @JvmField
        val ID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "fireballupdate")

        @JvmStatic
        fun deserialise(buffer: ByteBuf): FireballUpdatePacket? {
            val buf = FriendlyByteBuf(buffer)
            val targetVelocity = Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
            val entityPos = Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
            //val entityUUID = buf.readUuid()
            val entityID = buf.readInt()
            var foundEntity : Entity? = null
            val world = Minecraft.getInstance().level
            foundEntity = world?.getEntity(entityID)
            if (foundEntity == null){
                return null
            }
            return FireballUpdatePacket(targetVelocity, foundEntity as AbstractHurtingProjectile)
        }

        @JvmStatic
        fun handle(self: FireballUpdatePacket?) {
            if (self == null){
                return
            } else if (self.entity == null){
                return
            }
            Minecraft.getInstance().execute {
                val targetVelocity = self.targetVelocity
                val entityToUpdate = self.entity
                try {
                    entityToUpdate.xPower = targetVelocity.x
                    entityToUpdate.yPower = targetVelocity.y
                    entityToUpdate.zPower = targetVelocity.z
                } catch (e : ConcurrentModificationException){
                    Oneironaut.LOGGER.debug("oopsie, concurrent modification!\n$e")
                }
            }
        }
    }
}