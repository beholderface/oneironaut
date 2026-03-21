package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.player.Sentinel
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.world.phys.Vec3
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut
import org.arcticquests.dev.oneironaut.oneironautt.assertTeleportationAllowed
import org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps.MishapNoSentinel
import org.arcticquests.dev.oneironaut.oneironautt.getDimension


class OpShiftSentinel : ConstMediaAction {
    override val argc = 1
    override val mediaCost = MediaConstants.SHARD_UNIT
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env.caster == null){
            throw MishapBadCaster()
        }
        val destDim = if (args[0] is NullIota){
            Oneironaut.getNoosphere()
        } else {
            args.getDimension(0, argc, env.world.server)
        }
        destDim.assertTeleportationAllowed()
        val sentinel = IXplatAbstractions.INSTANCE.getSentinel(env.caster) ?: throw MishapNoSentinel() //placeholder
        val originDim = env.world.server.getLevel(sentinel.dimension)
        if (originDim == destDim){
            return listOf()
        }
        val compressionFactor = originDim!!.dimensionType().coordinateScale / destDim.dimensionType().coordinateScale
        val originalPos = sentinel.position
        val x = (originalPos.x * compressionFactor).coerceIn(destDim.worldBorder.minX, destDim.worldBorder.maxX)
        val z = (originalPos.z * compressionFactor).coerceIn(destDim.worldBorder.minZ, destDim.worldBorder.maxZ)
        val y = originalPos.y.coerceIn(destDim.dimensionType().minY.toDouble(), (destDim.dimensionType().minY + destDim.dimensionType().height).toDouble())
        val newPos = Vec3(x, y, z)
        IXplatAbstractions.INSTANCE.setSentinel(env.caster, Sentinel(sentinel.extendsRange, newPos, destDim.dimension()))
        return listOf()
    }
}