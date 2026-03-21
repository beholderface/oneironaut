package org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

class MishapNoSentinel : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context) : FrozenPigment = dyeColor(DyeColor.BLUE)

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component = error("oneironaut:nosentinel")

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        stack.add(GarbageIota())
    }
}