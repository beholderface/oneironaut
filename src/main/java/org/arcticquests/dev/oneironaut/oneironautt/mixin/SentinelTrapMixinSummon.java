package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.SentinelTrapImpetusEntity;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
@Mixin(targets = "at.petrak.hexcasting.common.casting.actions.spells.sentinel.OpCreateSentinel$Spell")
public abstract class SentinelTrapMixinSummon {
    @Final
    @Shadow private Vec3 target;

    @Unique
    private final Map<ResourceKey<Level>, Map<BlockPos, Vec3>> oneironaut$trapMap = SentinelTrapImpetusEntity.trapLocationMap;

    @Inject(method = "cast(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)V", at = @At("HEAD"), remap = false)
    public void triggerTrap(CastingEnvironment env, CallbackInfo ci){
        if (env.getCastingEntity() == null){
            return;
        }
        Level world = env.getWorld();
        ResourceKey<Level> worldKey = world.dimension();
        if (oneironaut$trapMap.containsKey(worldKey)){
            Map<BlockPos, Vec3> trapPosMap = oneironaut$trapMap.get(worldKey);
            Iterator<Map.Entry<BlockPos, Vec3>> entryIterator = trapPosMap.entrySet().iterator();
            Map.Entry<BlockPos, Vec3> currentEntry;
            List<BlockPos> expiredKeys = new ArrayList<>();
            while(entryIterator.hasNext()){
                currentEntry = entryIterator.next();
                if (target.closerThan(currentEntry.getValue(), 8.0)){
                    BlockPos pos = currentEntry.getKey();
                    if (world.getBlockState(pos).getBlock() == OneironautBlockRegistry.SENTINEL_TRAP.get()){
                        SentinelTrapImpetusEntity be = (SentinelTrapImpetusEntity) world.getBlockEntity(pos);
                        ServerPlayer foundPlayer = null;
                        if (be.getStoredPlayer() != null){
                            foundPlayer = (ServerPlayer) world.getPlayerByUUID(be.getStoredPlayer().getUUID());
                        }
                        be.setTargetPlayer(env.getCastingEntity().getUUID());
                        be.startExecution(foundPlayer);
                    } else {
                        expiredKeys.add(currentEntry.getKey());
                    }
                }
            }
            for (BlockPos key : expiredKeys){
                trapPosMap.remove(key);
            }
        }
    }
}