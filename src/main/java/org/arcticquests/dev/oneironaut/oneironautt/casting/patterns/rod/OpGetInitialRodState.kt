package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.rod

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import org.arcticquests.dev.oneironaut.oneironautt.casting.environments.ReverbRodCastEnv
import org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps.MishapNoRod

class OpGetInitialRodState(val mode: Int) : ConstMediaAction {
    override val argc = 0

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env is ReverbRodCastEnv){
            //I don't remember why I included these lines but idc
            val rodStack  = env.castingEntity!!.useItem
            val rodNbt = rodStack.tag
            if (rodNbt != null){
                when(mode){
                    1 -> return listOf(Vec3Iota(env.initialLook))
                    2 -> return listOf(Vec3Iota(env.initialPos))
                    3 -> return listOf(DoubleIota(env.timestamp.toDouble()))
                }
            } else {
                return listOf(NullIota())
            }

        } else {
            //throw mishap
            throw MishapNoRod(false)
        }
        return listOf(NullIota())
    }
}