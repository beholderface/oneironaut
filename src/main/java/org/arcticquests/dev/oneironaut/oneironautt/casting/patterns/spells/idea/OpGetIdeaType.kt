package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.idea

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import org.arcticquests.dev.oneironaut.oneironautt.casting.idea.IdeaInscriptionManager
import org.arcticquests.dev.oneironaut.oneironautt.getIdeaKey
import ram.talia.moreiotas.api.asActionResult

class OpGetIdeaType : ConstMediaAction {
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val key = args.getIdeaKey(0, argc, env)
        val entry = IdeaInscriptionManager.getEntry(key, env.world) ?: return listOf(NullIota())
        return entry.type.toString().asActionResult
    }
    override val argc = 1
}