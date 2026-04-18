package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns
import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveDouble
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry

class OpDetectShrouded : ConstMediaAction {
    override val argc = 2
    override val mediaCost = MediaConstants.DUST_UNIT / 10;
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getVec3(0, argc)
        env.assertVecInRange(target)
        val radius = args.getPositiveDouble(1, argc)
        val box = AABB(target.add(Vec3(-radius, -radius, -radius)), target.add(Vec3(radius, radius, radius)))
        val entities = env.world.getEntities(null, box) {
            isShroudedAndReachable(it, env) && it.distanceToSqr(target) <= (radius * radius)}.sortedBy { it.distanceToSqr(target) }
        val directions = mutableSetOf<Vec3>()
        for (element in entities){
            directions.add(element.position().subtract(target).normalize())
        }
        return directions.map(::Vec3Iota).asActionResult
    }
    companion object{
        fun isShroudedAndReachable(e: Entity, ctx : CastingEnvironment) : Boolean{
            if (e.showVehicleHealth() && ctx.isEntityInRange(e)){
                val le = e as LivingEntity
                return le.hasEffect(OneironautMiscRegistry.DETECTION_RESISTANCE.get())
            } else {
                return false
            }
        }
    }
}