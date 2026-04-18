package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions


class OpReadSentinel : ConstMediaAction {
    override val argc = 2
    override val mediaCost = MediaConstants.DUST_UNIT / 10
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val origin = args.getVec3(0, argc)
        val target = args.getPlayer(1, argc)
        val sentinel = IXplatAbstractions.INSTANCE.getSentinel(target)
        env.assertVecInRange(origin)
        if (sentinel != null){
            if (sentinel.dimension.equals(env.world.dimension())){
                return listOf(DoubleIota(origin.subtract(sentinel.position).length()))
            }
            return listOf(NullIota())
        }
        return listOf(NullIota())
    }
}