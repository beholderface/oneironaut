package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.great

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.portal.PortalInfo
import net.minecraftforge.common.util.ITeleporter
import java.util.function.Function


object ForgeTeleportUtil {
    fun teleport(entity: Entity, dest: ServerLevel, info: PortalInfo) {
        if (entity.level() === dest) {
            entity.teleportTo(info.pos.x, info.pos.y, info.pos.z)
            entity.deltaMovement = info.speed
            entity.yRot = info.yRot
            entity.xRot = info.xRot
            entity.yHeadRot = info.yRot
            entity.setYBodyRot(info.yRot)
            entity.hasImpulse = true
        } else {
            entity.changeDimension(dest, object : ITeleporter {
                override fun getPortalInfo(
                    entity: Entity,
                    destWorld: ServerLevel,
                    defaultPortalInfo: Function<ServerLevel, PortalInfo>
                ): PortalInfo = info

                override fun placeEntity(
                    entity: Entity,
                    currentWorld: ServerLevel,
                    destWorld: ServerLevel,
                    yaw: Float,
                    repositionEntity: Function<Boolean, Entity>
                ): Entity {
                    val placed = repositionEntity.apply(false)
                    placed.deltaMovement = info.speed
                    placed.yRot = info.yRot
                    placed.xRot = info.xRot
                    placed.yHeadRot = info.yRot
                    placed.setYBodyRot(info.yRot)
                    placed.hasImpulse = true
                    return placed
                }
            })
        }
    }
}