package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.idea

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.DyeColor
import net.minecraft.Util
import org.arcticquests.dev.oneironaut.oneironautt.casting.idea.IdeaEntry
import org.arcticquests.dev.oneironaut.oneironautt.casting.idea.IdeaInscriptionManager
import org.arcticquests.dev.oneironaut.oneironautt.colorToClosestPigment
import org.arcticquests.dev.oneironaut.oneironautt.getIdeaKey
import org.arcticquests.dev.oneironaut.oneironautt.isDecorative
import ram.talia.hexal.common.entities.WanderingWisp

class OpReleaseEntity : ConstMediaAction {
    override val argc = 2
    override val mediaCost = MediaConstants.CRYSTAL_UNIT

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val key = args.getIdeaKey(0, argc, env)
        val position = args.getVec3(1, argc)
        env.assertVecInRange(position)
        val entry = IdeaInscriptionManager.getEntry(key, env.world)
        val wisp = if (entry == null || entry.type == IdeaEntry.EntryType.IOTA) {
            val out = WanderingWisp(env.world, position)
            if (entry?.type == IdeaEntry.EntryType.IOTA) {
                val iota = entry.payload as Iota
                out.setPigment(colorToClosestPigment(iota.type.color()))
            } else {
                out.setPigment(
                    FrozenPigment(
                        HexItems.DYE_PIGMENTS[DyeColor.values().random()]!!.defaultInstance,
                        Util.NIL_UUID
                    )
                )
            }
            out.isDecorative = true
            env.world.addFreshEntity(out)
        } else {
            null
        }
        if (entry?.type == IdeaEntry.EntryType.ENTITY) {
            val entity = entry.payload as Entity
            //entity.setRemoved(null)
            entity.setPos(position)
            env.world.addFreshEntity(entity)
            IdeaInscriptionManager.eraseEntry(key)
            IdeaInscriptionManager.getServerState(env.world.server).setDirty()
            return entity.asActionResult
        } else if (wisp != null) {
            return wisp.asActionResult
        } else {
            return listOf(GarbageIota())
        }
    }
}