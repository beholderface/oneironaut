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
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level

class MishapUninfusable(val pos: BlockPos/*, val expected: Text*/) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.PURPLE)

    override fun particleSpray(ctx: CastingEnvironment) =
        ParticleSpray.burst(Vec3.atCenterOf(pos), 1.0)

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component =
        error("oneironaut:uninfusable", this.pos.toShortString(), blockAtPos(ctx, this.pos))

    override fun execute(ctx: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        stack.add(GarbageIota())
        ctx.world.explode(null, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 0.25f, Level.ExplosionInteraction.NONE)
    }

    companion object {
        @JvmStatic
        fun of(pos: BlockPos/*, stub: String*/): MishapUninfusable {
            return MishapUninfusable(pos/*, Text.translatable("oneironaut.mishap.uninfusable")*/)
        }
    }

}