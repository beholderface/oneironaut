package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.great

import org.arcticquests.dev.oneironaut.oneironautt.network.ItemUpdatePacket
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.EnchantmentInstance
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.EnchantedBookItem
import net.minecraft.world.item.ItemStack
import org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps.MishapMissingEnchant
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry
import ram.talia.hexal.api.getItemEntityOrItemFrame
//import ram.talia.hexal.api.getItemEntityOrItemFrame
import kotlin.math.pow

class OpApplyOvercastDamage : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val holder = args.getEntity(0, argc)
        val target = args.getItemEntityOrItemFrame(0, argc)
        env.assertEntityInRange(holder)
        val stack : ItemStack = if (!(target.left().isEmpty)){
            target.left().get().item
        } else {
            target.right().get().item
        }
        val level : Int
        if (stack.item is EnchantedBookItem){
            val stackEnchants = EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(stack))
            if (stackEnchants.contains(Enchantments.SHARPNESS)){
                level = stackEnchants[Enchantments.SHARPNESS]!!
            } else {
                throw MishapMissingEnchant(stack, Enchantments.SHARPNESS)
            }
            return SpellAction.Result(
                Spell(stack, level, true, stackEnchants, holder),
                (level.toDouble().pow(2) * MediaConstants.CRYSTAL_UNIT * 15).toLong(),
                listOf(ParticleSpray.cloud(holder.position(), 2.0))
            )

        } else {
            val stackEnchants = EnchantmentHelper.deserializeEnchantments(stack.enchantmentTags)
            if (stackEnchants.contains(Enchantments.SHARPNESS)){
                level = stackEnchants[Enchantments.SHARPNESS]!!
            } else {
                throw MishapMissingEnchant(stack, Enchantments.SHARPNESS)
            }
            return SpellAction.Result(
                Spell(stack, level, false, stackEnchants, holder),
                (level.toDouble().pow(2) * MediaConstants.CRYSTAL_UNIT * 10).toLong(),
                listOf(ParticleSpray.cloud(holder.position(), 2.0))
            )
        }
    }

    private data class Spell(val stack : ItemStack, val level : Int, val book : Boolean, val existingEnchantments : Map<Enchantment, Int>, val holder : Entity) : RenderedSpell{
        override fun cast(env: CastingEnvironment) {
            val overcastdamage = OneironautMiscRegistry.OVERCAST_DAMAGE_ENCHANT.get()
            val enchantsToKeep = HashMap<Enchantment, Int>()
            existingEnchantments.map {
                if(overcastdamage.checkCompatibility(it.key)){
                    enchantsToKeep.put(it.key, it.value)
                }
            }
            val nbt = stack.tag
            val enchantKey = if(book){
                EnchantedBookItem.TAG_STORED_ENCHANTMENTS
            } else {
                "Enchantments"
            }
            nbt!!.remove(enchantKey)
            if(book){
                EnchantedBookItem.addEnchantment(stack, EnchantmentInstance(overcastdamage, level))
                enchantsToKeep.map {
                    EnchantedBookItem.addEnchantment(stack, EnchantmentInstance(it.key, it.value))
                }
            } else {
                stack.enchant(overcastdamage, level)
                enchantsToKeep.map {
                    stack.enchant(it.key, it.value)
                }
            }
            IXplatAbstractions.INSTANCE.sendPacketNear(stack.entityRepresentation?.position(), 128.0, env.world, ItemUpdatePacket(stack, holder))
        }

    }
}