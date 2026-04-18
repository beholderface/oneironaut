package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.network.chat.Component
import net.minecraft.util.Tuple
import net.minecraft.world.entity.LivingEntity
import org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes.SoulprintIota
import org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps.MishapNoStaff

class OpGetSoulprint : ConstMediaAction {
    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT / 10
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env !is StaffCastEnv){
            throw MishapNoStaff(Component.translatable("hexcasting.spell.oneironaut:getsoulprint"))
        }
        if (env.castingEntity !is LivingEntity){
            throw MishapBadCaster()
        }
        return listOf(
            SoulprintIota(
                Tuple(
                    env.castingEntity!!.uuid,
                    env.castingEntity!!.name.string
                )
            )
        )
    }
}