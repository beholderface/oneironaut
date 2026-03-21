package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.great


import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry
import kotlin.math.max

private val markerEffect: MobEffect = OneironautMiscRegistry.NOT_MISSING.get()

class OpMarkEntity() : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getLivingEntityButNotArmorStand(0, argc)
        env.assertEntityInRange(target)
        val existingLevel = if (target.hasEffect(markerEffect)) {
            max(target.getEffect(markerEffect)!!.amplifier, 128)
        } else {
            -1
        }
        //Oneironaut.boolLogger("Cost boost: $existingLevel", true)
        val cost = (existingLevel + 2) * MediaConstants.SHARD_UNIT
        return SpellAction.Result(Spell(target, existingLevel + 1),
            cost,
            listOf(ParticleSpray.cloud(target.position().add(0.0, target.eyeY / 2, 0.0), 1.0)))
    }

    private class Spell(val target : LivingEntity, val levelToApply : Int) : RenderedSpell{
        override fun cast(env: CastingEnvironment) {
            //ctx.caster.sendMessage(Text.literal("For the time being, this spell effectively just applies Glowing, due to mixin trouble. Sorry."), true)
            //val glowInstance = StatusEffectInstance(StatusEffects.GLOWING, 1200)
            val markInstance = MobEffectInstance(markerEffect, 1200, levelToApply)
            target.addEffect(markInstance)
            //target.addStatusEffect(glowInstance)
        }

    }

}