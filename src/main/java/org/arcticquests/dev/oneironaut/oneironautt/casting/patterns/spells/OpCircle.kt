package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getInt
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautItemRegistry

class OpCircle : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, ctx: CastingEnvironment): SpellAction.Result {
        val quantity = args.getInt(0, argc).coerceIn(1, 64)
        val cost = quantity * MediaConstants.DUST_UNIT
        return SpellAction.Result(Spell(quantity), cost, listOf(ParticleSpray.cloud(ctx.mishapSprayPos(), 2.0)))
    }
    private data class Spell(val quantity : Int) : RenderedSpell {
        override fun cast(env: CastingEnvironment){
            if (env.castingEntity != null && env.castingEntity is ServerPlayer){
                val caster = env.castingEntity as ServerPlayer
                caster.addItem(ItemStack(OneironautItemRegistry.CIRCLE_ITEM.get(), quantity))
            }
        }
    }
}