package org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

class MishapNoNoosphere() : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.PURPLE)

    override fun particleSpray(ctx: CastingEnvironment) =
        ParticleSpray.burst(ctx.mishapSprayPos(), 1.0)

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component {
        return error("oneironaut:nonoosphere")
    }

    override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        stack.add(GarbageIota())
        ctx.world.explode(null, ctx.mishapSprayPos().x, ctx.mishapSprayPos().y, ctx.mishapSprayPos().z, 0.25f, Level.ExplosionInteraction.NONE)
    }

    companion object {
        @JvmStatic
        fun of(pos: BlockPos): MishapNoNoosphere {
            return MishapNoNoosphere()
        }
    }

}