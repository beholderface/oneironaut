package org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level

class MishapBadCuboid(val stub : String) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.PURPLE)

    override fun particleSpray(ctx: CastingEnvironment) =
        ParticleSpray.burst(ctx.mishapSprayPos(), 1.0)

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component =
        error("oneironaut:badcuboid.$stub")

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        stack.add(GarbageIota())
        env.world.explode(null, env.mishapSprayPos().x, env.mishapSprayPos().y, env.mishapSprayPos().z, 0.25f, Level.ExplosionInteraction.NONE)
    }

    companion object {
        @JvmStatic
        fun of(stub : String): MishapBadCuboid {
            return MishapBadCuboid(stub)
        }
    }

}