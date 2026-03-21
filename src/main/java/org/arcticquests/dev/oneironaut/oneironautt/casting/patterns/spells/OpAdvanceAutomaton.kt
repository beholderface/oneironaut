package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import org.arcticquests.dev.oneironaut.oneironautt.corners
import org.arcticquests.dev.oneironaut.oneironautt.getPositionsInCuboid
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry
import org.arcticquests.dev.oneironaut.oneironautt.toVec3i

class OpAdvanceAutomaton : SpellAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val box = AABB(BlockPos(args.getVec3(0, argc).toVec3i()), BlockPos(args.getVec3(1, argc).toVec3i()))
        val corners = box.corners()
        for(c in corners){
            env.assertVecInRange(c)
        }
        val cost = (box.xsize * box.ysize * box.zsize * (MediaConstants.DUST_UNIT * 0.1)).toLong()
        return SpellAction.Result(
            Spell(box, null, args, false),
            cost,
            listOf(ParticleSpray.cloud(box.center, 2.0))
        )
    }
    private data class Spell(val box : AABB, val corner : BlockPos?,
                             val args : List<Iota>?, val execute : Boolean) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            advanceAutomaton(env, box)
        }
    }
}
fun advanceAutomaton(ctx: CastingEnvironment, box : AABB){
    val world = ctx.world
    val corner1 = BlockPos(box.minX.toInt(), box.minY.toInt(), box.minZ.toInt())
    val corner2 = BlockPos(box.maxX.toInt(), box.maxY.toInt(), box.maxZ.toInt())
    val positions = getPositionsInCuboid(corner1, corner2)
    val cellsToKill : MutableList<BlockPos> = mutableListOf()
    val cellsToSpawn : MutableList<BlockPos> = mutableListOf()
    val cellsToVerify : MutableList<BlockPos> = mutableListOf()
    for(pos in positions){
        val isCell = world.getBlockState(pos).block.equals(OneironautBlockRegistry.CELL.get())
        val isAir = world.getBlockState(pos).isAir
        val neighbors = getPositionsInCuboid(pos.offset(1, 1, 1), pos.offset(-1, -1, -1), pos)
        var foundCells = 0
        for (neighbor in neighbors){
            if (world.getBlockState(neighbor).block.equals(OneironautBlockRegistry.CELL.get())){
                foundCells++
            }
        }
        if (isCell){
            if (foundCells in 5..7){
                //cell survives, mark it as having done so
                cellsToVerify.add(pos)
            } else {
                //mark cell as submissive and killable
                cellsToKill.add(pos)
            }
        } else if (isAir && foundCells == 6){
            //mark position as free real estate
            cellsToSpawn.add(pos)
            cellsToVerify.add(pos)
        }
    }
    for (pos in cellsToKill){
        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
    }
    for (pos in cellsToSpawn){
        world.setBlockAndUpdate(pos, OneironautBlockRegistry.CELL.get().defaultBlockState())
    }
    for (pos in cellsToVerify){
        val mayBE = world.getBlockEntity(pos, OneironautBlockRegistry.CELL_ENTITY.get())
        if (mayBE.isPresent){
            if (!mayBE.get().verified){
                mayBE.get().verified = true
            }
        }
    }
}