package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.rod

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.network.chat.Component
import org.arcticquests.dev.oneironaut.oneironautt.casting.environments.ReverbRodCastEnv
import org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps.MishapNoRod

class OpAccessRAMRemote(val store : Boolean) : ConstMediaAction {
    override val argc = if (store) { 1 } else { 0 }
    override val mediaCost = MediaConstants.DUST_UNIT / 100
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env is ReverbRodCastEnv){
            return if (store){
                val iotaToStore = args[0]
                if (iotaToStore.type.equals(ListIota.TYPE) || iotaToStore.type.toString().contains("DictionaryIota", true)){
                    throw MishapInvalidIota(iotaToStore, 0, Component.translatable("oneironaut.mishap.invalid_value.class.nolistsallowed"))
                }
                env.setStoredIota(iotaToStore)
                listOf()
            } else {
                listOf(env.storedIota)
            }
        } else {
            throw MishapNoRod(true)
        }
    }
}