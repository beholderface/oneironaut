package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.rod

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveInt
import at.petrak.hexcasting.api.casting.iota.Iota
import org.arcticquests.dev.oneironaut.oneironautt.casting.environments.ReverbRodCastEnv
import org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps.MishapNoRod

class OpHaltRod(val reset : Int) : ConstMediaAction {
    override val argc = reset
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env is ReverbRodCastEnv){
            if(reset == 1){
                val delay = args.getPositiveInt(0, argc)
                env.setResetCooldown(delay.coerceAtLeast(1).coerceAtMost(100))
            }
            env.stopCasting()
            //ctx.caster.stopUsingItem()
        } else {
            throw MishapNoRod(false)
        }
        return listOf()
    }
}