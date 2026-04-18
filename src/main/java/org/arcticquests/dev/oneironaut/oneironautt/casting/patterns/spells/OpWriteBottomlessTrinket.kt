package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.network.chat.Component
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautItemRegistry

class OpWriteBottomlessTrinket  : SpellAction {
    override val argc = 1
    private val itemType: Item = OneironautItemRegistry.BOTTOMLESS_CASTING_ITEM.get()
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val patterns = args.getList(0, argc).toList()

        val handStackInfo = env.getHeldItemToOperateOn() {
            val hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(it)
            it.item == itemType && hexHolder != null && !hexHolder.hasHex()
        } ?: throw MishapBadOffhandItem(null, Component.nullToEmpty("no_item.offhand"))
        val handStack = handStackInfo.stack
        val hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(handStack)
        if (handStack.item != itemType || (hexHolder == null || hexHolder.hasHex())) {
            throw MishapBadOffhandItem(handStack, Component.nullToEmpty("bad_item.offhand"))
        }

        val trueName = MishapOthersName.getTrueNameFromArgs(patterns, env.caster)
        if (trueName != null)
            throw MishapOthersName(trueName)

        return SpellAction.Result(Spell(patterns, handStack), MediaConstants.CRYSTAL_UNIT * 10, listOf(ParticleSpray.burst(env.mishapSprayPos(), 0.5)))
    }

    private inner class Spell(val patterns: List<Iota>, val stack: ItemStack) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(stack)
            val pigment = if (env.castingEntity != null && env.castingEntity is ServerPlayer){
                IXplatAbstractions.INSTANCE.getPigment(env.castingEntity as ServerPlayer)
            } else {
                null
            }
            if (hexHolder != null
                && !hexHolder.hasHex()
            ) {
                hexHolder.writeHex(patterns, pigment,1000)
            }
        }
    }
}