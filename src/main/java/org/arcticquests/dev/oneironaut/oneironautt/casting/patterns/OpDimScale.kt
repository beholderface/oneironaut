package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import org.arcticquests.dev.oneironaut.oneironautt.getDimension

class OpDimScale : ConstMediaAction {
    override val argc = 1
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val dim = args.getDimension(0, argc, env.world.server)
        return listOf(DoubleIota(dim.dimensionType().coordinateScale))
    }

}