package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.NoosphereGateEntity;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import ram.talia.hexal.common.entities.BaseCastingWisp;

import java.util.Map;


@SuppressWarnings("ConstantConditions")
@Mixin(value = BaseCastingWisp.class)
public abstract class MiscWispMixin
{

    @Unique
    private final BaseCastingWisp oneironaut$wisp = (BaseCastingWisp) (Object) this;

    //the code for negating wisp upkeep
    @Unique
    private static final Map<ResourceKey<Level>, Map<BlockPos, Vec3>> oneironaut$gateMap = NoosphereGateEntity.gateLocationMap;

    @WrapOperation(method = "deductMedia",
            at = @At(value = "INVOKE",
                    target="Lram/talia/hexal/common/entities/BaseCastingWisp;getNormalCostPerTick()J",
                    remap = false),
            remap = false)
    public long freeIfNoosphereNormal(BaseCastingWisp wisp, Operation<Long> original){
        if (oneironaut$free(wisp)){
            return 0;
        }
        return original.call(wisp);
    }

    @WrapOperation(method = "deductMedia",
            at = @At(value = "INVOKE",
                    target="Lram/talia/hexal/common/entities/BaseCastingWisp;getUntriggeredCostPerTick()J",
                    remap = false),
            remap = false)
    public long freeIfNoosphereSleepy(BaseCastingWisp wisp, Operation<Long> original){
        if (oneironaut$free(wisp)){
            return 0;
        }
        return original.call(wisp);
    }

    @Unique
    private static boolean oneironaut$free(BaseCastingWisp wisp){
        boolean foundGate = false;
        //Contrary to what Big IDE wants you to think, casting wisp to Entity is not redundant.
        //This is because outside of dev environments, the desired methods do not seem to exist in BaseCastingWisp.
        //I have no idea why it thinks they do exist when in a dev environment.
        Level world = ((Entity)wisp).getCommandSenderWorld();
        ResourceKey<Level> worldKey = world.dimension();
        String worldName = worldKey.location().toString();
        if(oneironaut$gateMap.containsKey(worldKey) && !(worldName.equals("oneironaut:noosphere"))){
            Map<BlockPos, Vec3> gatePosMap = oneironaut$gateMap.get(worldKey);
            for (Map.Entry<BlockPos, Vec3> map : gatePosMap.entrySet()){
                if (((Entity)wisp).position().closerThan(map.getValue(), 8.0)){
                    if(world.getBlockState(map.getKey()).getBlock().equals(OneironautBlockRegistry.NOOSPHERE_GATE.get().defaultBlockState().getBlock())){
                        foundGate = true;
                    } else {
                        gatePosMap.remove(map.getKey());
                    }
                }
            }
        }
        if (worldName.equals("oneironaut:noosphere")){
            foundGate = true;
        }
        if (wisp.wispNumContainedPlayers() > 0){
            foundGate = false;
        }
        return foundGate;
    }
}