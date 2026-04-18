package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.utils.getList
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import net.minecraft.nbt.Tag
import org.arcticquests.dev.oneironaut.oneironautt.block.ExtradimensionalBoundaryLocus
import org.arcticquests.dev.oneironaut.oneironautt.casting.environments.ExtradimensionalCastEnv
import org.arcticquests.dev.oneironaut.oneironautt.casting.environments.ExtradimensionalCircleCastEnv
import org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes.DimIota
import org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps.MishapExtradimensionalFail

class OpEvalExtradimensional : Action {
    override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation
    ): OperationResult {
        if (!(env is PlayerBasedCastEnv || env is CircleCastEnv)){
            throw MishapBadCaster()
        }
        val stack = image.stack.toMutableList()
        val toExecute = stack.removeLastOrNull() ?: throw MishapNotEnoughArgs(2, 0)
        val dimension = stack.removeLastOrNull() ?: throw MishapNotEnoughArgs(2, 1)
        val server = env.world.server
        if (dimension !is DimIota){
            throw MishapInvalidIota.ofType(dimension, 1, "oneironaut:imprint")
        } else if (dimension.toWorld(server) == env.world){
            throw MishapInvalidIota.ofType(dimension, 1, "oneironaut:differentimprint")
        }
        val copiedImage = image.copy(stack = stack)
        if (env is PlayerBasedCastEnv){
            var vm : CastingVM? = null
            if (env is ExtradimensionalCastEnv){
                vm = env.vm
            }
            val newEnv = ExtradimensionalCastEnv(env.caster, env, dimension.toWorld(server), vm)
            return execPlayer(newEnv, copiedImage, continuation, toExecute)
        } else {
            val circleEnv = env as CircleCastEnv
            var vm : CastingVM? = null
            if (circleEnv is ExtradimensionalCircleCastEnv){
                vm = circleEnv.vm
            }
            val visitedLoci = ExtradimensionalBoundaryLocus.toPositionSet(image.userData.getList(ExtradimensionalBoundaryLocus.TAG_BOUNDARY_LIST, Tag.TAG_COMPOUND))
            val newEnv = ExtradimensionalCircleCastEnv(env, dimension.toWorld(server), vm, env.circleState(), visitedLoci)
            return execCircle(newEnv, copiedImage, continuation, toExecute)
        }
    }

    fun execPlayer(env: ExtradimensionalCastEnv, image: CastingImage, continuation: SpellContinuation, instrs: Iota): OperationResult {
        val toExecute = if (instrs is ListIota){
            instrs.list.toList()
        } else {
            listOf(instrs)
        }
        val subHarness = env.vm
        subHarness.image = image
        val executionResult = subHarness.queueExecuteAndWrapIotas(toExecute, env.world)
        if (!executionResult.resolutionType.success){
            throw MishapExtradimensionalFail()
        }
        return OperationResult(subHarness.image.withUsedOp(), listOf(), continuation, HexEvalSounds.HERMES)
    }

    fun execCircle(env: ExtradimensionalCircleCastEnv, image: CastingImage, continuation: SpellContinuation, instrs: Iota): OperationResult {
        val toExecute = if (instrs is ListIota){
            instrs.list.toList()
        } else {
            listOf(instrs)
        }
        val subHarness = env.vm
        subHarness.image = image

        //Oneironaut.LOGGER.info(env.targetDimBounds)

        val executionResult = subHarness.queueExecuteAndWrapIotas(toExecute, env.world)
        if (!executionResult.resolutionType.success){
            throw MishapExtradimensionalFail()
        }

        //clear visited boundary loci
        image.userData.remove(ExtradimensionalBoundaryLocus.TAG_BOUNDARY_LIST)
        return OperationResult(subHarness.image.withUsedOp(), listOf(), continuation, HexEvalSounds.HERMES)
    }
}