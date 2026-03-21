package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.idea

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.Iota
import org.arcticquests.dev.oneironaut.oneironautt.casting.idea.IdeaInscriptionManager
import org.arcticquests.dev.oneironaut.oneironautt.getIdeaKey
import org.arcticquests.dev.oneironaut.oneironautt.getSoulprint
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautIotaTypeRegistry

class OpGetIdeaWriter : ConstMediaAction {
    override val argc = 2
    override val mediaCost = 0L
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val keyIota = args.getIdeaKey(0, argc, env)
        val suspect = if (args[1].type == OneironautIotaTypeRegistry.UUID){
            args.getSoulprint(1, argc)
        } else {
            args.getPlayer(1, argc).uuid
        }
        val uuid = IdeaInscriptionManager.getEntryWriter(keyIota, env.world)
        return suspect.equals(uuid).asActionResult
    }
}