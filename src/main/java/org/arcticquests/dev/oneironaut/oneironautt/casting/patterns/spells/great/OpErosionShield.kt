package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.great


import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell
import at.petrak.hexcasting.api.casting.mishaps.circle.MishapNoSpellCircle
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut
import org.arcticquests.dev.oneironaut.oneironautt.casting.DisintegrationProtectionManager
import org.arcticquests.dev.oneironaut.oneironautt.corners
import org.arcticquests.dev.oneironaut.oneironautt.volume
import ram.talia.hexal.api.getStrictlyPositiveLong

class OpErosionShield : SpellAction {
    override val argc = 3
    fun isDevelopmentEnvironment(): Boolean {
        throw AssertionError()
    }


    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        if (!isDevelopmentEnvironment()){
            throw MishapDisallowedSpell()
        }
        if (env !is CircleCastEnv){
            throw MishapNoSpellCircle()
        }
        val cornerA = args.getBlockPos(0, argc)
        val cornerB = args.getBlockPos(1, argc)
        val costBox = AABB(Vec3.atLowerCornerOf(cornerA), Vec3.atLowerCornerOf(cornerB.offset(1,1,1)))
        val ambitCheckBox = AABB(Vec3.atLowerCornerOf(cornerA), Vec3.atLowerCornerOf(cornerB))
        val particlePositions : MutableList<ParticleSpray> = mutableListOf()
        for (corner in ambitCheckBox.corners()){
            env.assertVecInRange(corner)
        }
        for (corner in costBox.corners()){
            particlePositions.add(ParticleSpray.cloud(corner, 2.0))
        }
        val durability = args.getStrictlyPositiveLong(2, argc)
        val cost = (costBox.volume() * 0.1) * (durability.toDouble() * 0.01) * (MediaConstants.DUST_UNIT / 10)
        return SpellAction.Result(
            Spell(cornerA, cornerB, durability),
            cost.toLong(),
            particlePositions
        )
    }

    private data class Spell(val cornerA : BlockPos, val cornerB : BlockPos, val durability : Long) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            //it's not going to mishap if you use it in the overworld, but it's not going to do anything else either
            if (env.world != null && env.world == Oneironaut.getDeepNoosphere()){
                val entry = DisintegrationProtectionManager.DisintegrationProtectionEntry(cornerA, cornerB, durability)
                DisintegrationProtectionManager.getServerState(env.world.server).addEntry(entry)
            }
        }
    }
}