package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.great


import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapImmuneEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.api.player.FlightAbility
import at.petrak.hexcasting.common.blocks.BlockConjured
import at.petrak.hexcasting.common.lib.HexBlocks
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.network.chat.Component
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import net.minecraft.core.Vec3i
import net.minecraft.world.level.portal.PortalInfo
import org.arcticquests.dev.oneironaut.oneironautt.casting.DepartureEntry
import org.arcticquests.dev.oneironaut.oneironautt.*
import kotlin.math.floor


class OpDimTeleport : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        if (env.castingEntity == null){
            throw MishapBadCaster()
        }
        val target = args.getNonlivingIfAllowed(0, argc)
        env.assertEntityInRange(target)
        val origin = env.world
        val coords = target.position()
        var noosphere = false
        val destination = if (args[1] is NullIota){
            noosphere = true
            Oneironaut.getNoosphere()
        } else {
            args.getDimension(1, argc, env.world.server)
        }
        //do not do the bad thing
        destination.assertTeleportationAllowed()
        if (!target.canChangeDimensions() || target.type.`is`(HexTags.Entities.CANNOT_TELEPORT))
            throw MishapImmuneEntity(target)
        if (target.isAlwaysTicking && target != env.caster as LivingEntity && !OneironautConfig.server.planeShiftOtherPlayers){
            throw MishapImmuneEntity(target)
        }

        var departure = false
        if (target == env.caster){
            val entry = DepartureEntry.getEntry(env, destination)
            if (entry != null){
                //if (entry.isWithinCylinder(target.pos)){
                    departure = true
                //}
            }
        }

        val cost = if(departure){
            5 * MediaConstants.DUST_UNIT
        } else {
            20 * MediaConstants.CRYSTAL_UNIT
        }

        return if (origin == destination && !noosphere){
            SpellAction.Result(
                Spell(target, origin, destination, coords, false),
                //don't consume amethyst if trying to teleport to the same dimension you're already in
                0,
                listOf(ParticleSpray.cloud(target.position(), 2.0))
            )
        } else {
            SpellAction.Result(
                Spell(target, origin, destination, coords, noosphere),
                cost,
                listOf(ParticleSpray.cloud(target.position(), 2.0))
            )
        }
    }

    private data class Spell(var target: Entity, val origin: ServerLevel, val destination: ServerLevel, val coords: Vec3, val noosphere: Boolean) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            var x = coords.x
            var y = floor(coords.y)
            var z = coords.z
            val border = destination.worldBorder
            val compressionFactor = origin.dimensionType().coordinateScale / destination.dimensionType().coordinateScale
            x *= compressionFactor
            z *= compressionFactor
            var isFlying = false
            var flightSpell : FlightAbility? = null
            if (target is ServerPlayer){
                val playerTarget = target as ServerPlayer
                isFlying = playerTarget.abilities.flying
                flightSpell = IXplatAbstractions.INSTANCE.getFlight(playerTarget)
                if (target == env.caster){
                    DepartureEntry(env, origin)
                    /*val entry = DepartureEntry.getEntry(env, destination)
                    if (entry != null){
                        if (entry.isWithinCylinder(Vec3d(x, 0.0, z))){
                            //Oneironaut.LOGGER.info("Found an existing departure, teleporting there.")
                            val entryOrigin = entry.originPos
                            playerTarget.teleport(destination, entryOrigin.x, entryOrigin.y, entryOrigin.z, target.yaw, target.pitch)
                            playerTarget.abilities.flying = isFlying
                            playerTarget.sendAbilitiesUpdate()
                            return
                        }
                    }*/
                    //Oneironaut.LOGGER.info("No existing departure found, behaving as normal.")
                }
                playerTarget.onUpdateAbilities()
            }
            var floorSpot : BlockPos = BlockPos(Vec3i.ZERO)
            var floorNeeded = false
            //make sure you don't end up under the nether or something
            if (destination.minBuildHeight > coords.y - 5.0){
                y = ((destination.minBuildHeight + 5).toDouble())
            }
            //make sure you don't end up outside the world border
            if (x > border.maxX){
                x = border.maxX - 2
            } else if (x < border.minX){
                x = border.minX + 2
            }
            if (z > border.maxZ){
                z = border.maxZ - 2
            } else if (z < border.minZ){
                z = border.minZ + 2
            }
            //actually put you on the floor if possible
            var scanPoint = BlockPos.MutableBlockPos(x, y+1, z)
            if (!isFlying){
                while(!isSolid(destination, scanPoint)){
                    scanPoint.setY(scanPoint.y - 1)
                    //check for void
                    if (scanPoint.y < destination.minBuildHeight || isUnsafe(destination, scanPoint, false)){
                        scanPoint.set(x, y+1, z)
                        break
                    }
                }
            }
            //try to avoid putting your head in solid rock or something
            while(isUnsafe(destination, scanPoint, true) || isSolid(destination, scanPoint)){
                scanPoint.setY(scanPoint.y + 1)
                //check for ceiling
                if (destination.getBlockState(scanPoint).block.equals(Blocks.BEDROCK)){
                    break
                }
            }
            if (!(destination.getBlockState(scanPoint).block.equals(Blocks.BEDROCK))){
                if (isUnsafe(destination, BlockPos(Vec3(x, (scanPoint.y - 1).toDouble(), z).toVec3i()), true) || !isSolid(destination, BlockPos(
                        Vec3(x, (scanPoint.y - 1).toDouble(), z).toVec3i()))){
                    y = (scanPoint.y + 1).toDouble()
                    if (!isSolid(destination, BlockPos(Vec3(x, (scanPoint.y - 1).toDouble(), z).toVec3i()))){
                        floorNeeded = true
                        floorSpot = BlockPos(Vec3(x, (scanPoint.y - 1).toDouble(), z).toVec3i())
                    }
                }
                y = (scanPoint.y).toDouble()
            }
            val colorizer = env.pigment
            if (origin == destination){
                env.caster!!.sendSystemMessage(Component.translatable("hexcasting.spell.oneironaut:dimteleport.samedim"));
            } else {
                if (target is ServerPlayer){
                    val playerTarget = target as ServerPlayer
                    playerTarget.teleportTo(destination, x, y, z, target.yRot, target.xRot)
                    if (flightSpell != null){
                        val compressedOrigin = Vec3(flightSpell.origin.x * compressionFactor, flightSpell.origin.y, flightSpell.origin.z * compressionFactor)
                        val newFlight = FlightAbility(flightSpell.timeLeft, destination.dimension(), compressedOrigin, flightSpell.radius)
                        IXplatAbstractions.INSTANCE.setFlight(playerTarget, newFlight)
                    }
                    playerTarget.abilities.flying = isFlying
                    playerTarget.onUpdateAbilities()

                    //FabricDimensions.teleport(target, destination, TeleportTarget(Vec3d(x, y, z), Vec3d.ZERO, target.yaw, target.pitch))
                    if (noosphere){
                        playerTarget.addEffect(MobEffectInstance(MobEffects.CONFUSION, 200))
                        playerTarget.addEffect(MobEffectInstance(MobEffects.BLINDNESS, 100))
                    }
                    if (floorNeeded && !isFlying){
                        destination.setBlockAndUpdate((floorSpot), HexBlocks.CONJURED_BLOCK.defaultBlockState())
                        BlockConjured.setColor(destination, floorSpot, colorizer)
                    }
                } else {
                    ForgeTeleportUtil.teleport(target, destination, PortalInfo(Vec3(x, y, z), target.deltaMovement, target.yRot, target.xRot))
                    if (floorNeeded && !isFlying){
                        destination.setBlockAndUpdate((floorSpot), HexBlocks.CONJURED_BLOCK.defaultBlockState())
                        BlockConjured.setColor(destination, floorSpot, colorizer)
                    }
                }
                if (!isFlying && target is LivingEntity){
                    (target as LivingEntity).addEffect(MobEffectInstance(MobEffects.SLOW_FALLING, 1200))
                }
            }
        }
    }
}