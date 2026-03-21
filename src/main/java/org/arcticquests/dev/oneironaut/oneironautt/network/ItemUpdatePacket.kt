package org.arcticquests.dev.oneironaut.oneironautt.network
import at.petrak.hexcasting.common.msgs.IMessage
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.decoration.ItemFrame
import net.minecraft.world.item.ItemStack
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut


class ItemUpdatePacket(val stack : ItemStack, val entity : Entity?) : IMessage {
    override fun serialize(buf: FriendlyByteBuf) {
        buf.writeItem(stack)
        entity?.id?.let { buf.writeInt(it) }
    }

    override fun getFabricId() = ID

    companion object {
        @JvmField
        val ID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "itemupdate")

        @JvmStatic
        fun deserialise(buffer: ByteBuf): ItemUpdatePacket {
            val buf = FriendlyByteBuf(buffer)
            val newStack = buf.readItem()
            val entityID = buf.readInt()
            val world = Minecraft.getInstance().level
            return ItemUpdatePacket(newStack, world?.getEntity(entityID))
        }

        @JvmStatic
        fun handle(self: ItemUpdatePacket) {
            Minecraft.getInstance().execute {
                val stack = self.stack
                val entity = self.entity
                if (entity != null){
                    if (entity is ItemFrame){
                        val frame = entity as ItemFrame
                        frame.item = stack
                    } else if (entity is ItemEntity){
                        val item = entity as ItemEntity
                        item.item = stack
                    }
                }
            }
        }
    }
}