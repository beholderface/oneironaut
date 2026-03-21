package org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

class MishapNoStaff(val spellName : Component) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.PURPLE)

    override fun particleSpray(ctx: CastingEnvironment) =
        ParticleSpray.burst(ctx.mishapSprayPos(), 1.0)

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component =
        error("oneironaut:nostaff", spellName)

    override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        stack.add(GarbageIota())
        ctx.mishapEnvironment.dropHeldItems()
        //ctx.world.createExplosion(null, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 0.25f, Explosion.DestructionType.NONE)
    }

    companion object {
        @JvmStatic
        fun of(spellName : Component): MishapNoStaff {
            return MishapNoStaff(spellName)
        }
    }

}