package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.great

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.animal.allay.Allay
import net.minecraft.world.entity.npc.Villager
import net.minecraft.server.level.ServerPlayer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.npc.VillagerDataHolder
import org.arcticquests.dev.oneironaut.oneironautt.item.MemoryFragmentItem
import org.arcticquests.dev.oneironaut.oneironautt.unbrainsweep

class OpReviveFlayed : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val patient = args.getLivingEntityButNotArmorStand(0, argc)
        env.assertEntityInRange(patient)
        if (patient is Mob && IXplatAbstractions.INSTANCE.isBrainswept(patient)){
            val cost = if (patient is Villager || patient is Allay) {
                MediaConstants.CRYSTAL_UNIT * 16
            } else {
                MediaConstants.SHARD_UNIT * 10
            }
            return SpellAction.Result(Spell(patient), cost, listOf(ParticleSpray.cloud(patient.position(), 1.0)))
        } else {
            throw MishapBadEntity(patient, Component.translatable("oneironaut.mishap.requiresflayedmob"))
        }
    }

    private data class Spell(val patient : Mob) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            patient.unbrainsweep()
            if (patient is VillagerDataHolder && env.castingEntity != null && env.castingEntity is ServerPlayer){
                val tracker = (env.castingEntity as ServerPlayer).advancements
                val loader = env.world.server.advancements
                val recyclingAdvancement = loader.getAdvancement(ResourceLocation.tryBuild("oneironaut", "unflay"))
                if (!tracker.getOrStartProgress(recyclingAdvancement).isDone){
                    tracker.award(recyclingAdvancement, MemoryFragmentItem.CRITEREON_KEY)
                }
            }
        }
    }
}