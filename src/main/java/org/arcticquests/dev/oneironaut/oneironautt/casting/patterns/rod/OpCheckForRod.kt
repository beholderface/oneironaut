package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.rod

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import org.arcticquests.dev.oneironaut.oneironautt.isUsingRod

class OpCheckForRod : ConstMediaAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        return isUsingRod(env).asActionResult
    }
}