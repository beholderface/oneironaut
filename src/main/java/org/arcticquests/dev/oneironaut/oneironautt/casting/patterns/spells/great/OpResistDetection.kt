package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.great


import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.getPositiveDouble
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.effect.MobEffectInstance
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry
import kotlin.math.floor

class OpResistDetection : SpellAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        env.assertEntityInRange(target)
        val duration = args.getPositiveDouble(1, argc)
        val cost = duration * MediaConstants.DUST_UNIT * 2

        return SpellAction.Result(
            Spell(target, floor(duration * 20).toInt()),
            (cost).toLong(),
            listOf(ParticleSpray.cloud(target.position(), 2.0))
        )
    }

    private data class Spell(val target: LivingEntity, val duration : Int) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            if (duration > 0){
                target.addEffect(MobEffectInstance(OneironautMiscRegistry.DETECTION_RESISTANCE.get(), duration), env.caster)
            }
        }
    }
}