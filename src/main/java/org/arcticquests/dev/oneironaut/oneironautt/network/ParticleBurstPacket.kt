package org.arcticquests.dev.oneironaut.oneironautt.network



import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexSounds
import at.petrak.hexcasting.common.msgs.IMessage
import at.petrak.hexcasting.common.particles.ConjureParticleOptions
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.sounds.SoundSource
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut
import ram.talia.hexal.api.nextColour


//mostly stolen from Hexal
class ParticleBurstPacket(val origin : Vec3, val direction : Vec3, val posRandom : Double, val speedRandom : Double, val color : FrozenPigment, val quantity : Int, val isActuallySound : Boolean) :
    IMessage {
    override fun serialize(buf: FriendlyByteBuf) {
        buf.writeDouble(origin.x)
        buf.writeDouble(origin.y)
        buf.writeDouble(origin.z)
        buf.writeDouble(direction.x)
        buf.writeDouble(direction.y)
        buf.writeDouble(direction.z)
        //buf.writeDouble(speed)
        buf.writeDouble(posRandom)
        buf.writeDouble(speedRandom)
        buf.writeNbt(color.serializeToNBT())
        buf.writeInt(quantity)
        buf.writeBoolean(isActuallySound)
    }

    override fun getFabricId() = ID

    companion object {
        @JvmField
        val ID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "particleburst")

        @JvmStatic
        fun deserialise(buffer: ByteBuf): ParticleBurstPacket {
            val buf = FriendlyByteBuf(buffer)
            val origin = Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
            val direction = Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
            //val speed = buf.readDouble()
            val posRandom = buf.readDouble()
            val speedRandom = buf.readDouble()

            /*for (i in 1 .. numLocs) {
                locs.add(Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()))
            }*/

            return ParticleBurstPacket(origin, direction/*, speed*/, posRandom, speedRandom, FrozenPigment.fromNBT(buf.readNbt()!!), buf.readInt() ,buf.readBoolean())
        }

        @JvmStatic
        fun handle(self: ParticleBurstPacket) {
            Minecraft.getInstance().execute {
                val world = Minecraft.getInstance().level ?: return@execute
                val rand = world.random
                val origin = self.origin
                val direction = self.direction
                //val speed = self.speed
                val posRandom = self.posRandom
                val speedRandom = self.speedRandom
                val color = self.color.nextColour(rand)
                val quantity = self.quantity.coerceAtLeast(1)
                if (!self.isActuallySound){
                    for (i in 1 .. quantity){
                        val adjustedPos = Vec3(origin.x + (rand.nextGaussian() * posRandom), origin.y + (rand.nextGaussian() * posRandom), origin.z + (rand.nextGaussian() * posRandom))
                        val adjustedSpeed = Vec3(direction.x + (rand.nextGaussian() * speedRandom), direction.y + (rand.nextGaussian() * speedRandom), direction.z + (rand.nextGaussian() * speedRandom))
                        world.addParticle(ConjureParticleOptions(color),
                            adjustedPos.x, adjustedPos.y, adjustedPos.z,
                            adjustedSpeed.x, adjustedSpeed.y, adjustedSpeed.z)
                    }
                } else {
                    val source = world.getNearestPlayer(origin.x, origin.y, origin.z, 128.0, false)
                    world.playSound(
                        null, origin.x, origin.y, origin.z, HexSounds.CASTING_AMBIANCE,
                        SoundSource.MASTER, 1f, 1f
                    )
                }

                /*self.locs.zipWithNext { start, end ->
                    val steps = ((end - start).length() * 10).toInt()
                    for (i in 0 .. steps) {
                        val pos = start + (i.toDouble() / steps) * (end - start)
                        val colour = self.colouriser.nextColour(level.random)
                        level.addParticle(
                            ConjureParticleOptions(colour, false),
                            pos.x, pos.y, pos.z, 0.0, 0.0, 0.0)
                    }
                }*/
            }
        }
    }
}