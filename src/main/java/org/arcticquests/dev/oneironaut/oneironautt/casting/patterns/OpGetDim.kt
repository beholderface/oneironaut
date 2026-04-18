package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerPlayer
import org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes.DimIota

class OpGetDim (val sent: Boolean) : ConstMediaAction {
    override val argc = 0
    override val mediaCost = if (sent) { MediaConstants.DUST_UNIT / 10 } else { 0 }
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        var output : Iota = NullIota()
        if (!sent){
            val casterWorld = env.world
            output = DimIota(casterWorld)
        } else {
            if (env.castingEntity is ServerPlayer){
                val sentinel = IXplatAbstractions.INSTANCE.getSentinel(env.castingEntity as ServerPlayer)
                if (sentinel != null){
                    val dimString = sentinel.dimension.location().toString()
                    output = DimIota(dimString)
                }
            }
        }
        return listOf(output)


        /*if (!HexConfig.server().canTeleportInThisDimension(ctx.world.registryKey))
            throw MishapLocationTooFarAway(ctx.caster.pos, "bad_dimension")*/


    }
}