package org.arcticquests.dev.oneironaut.oneironautt.casting;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DepartureEntry {
    private static final Map<CastingEnvironment, Map<ServerLevel, DepartureEntry>> departureMap = new HashMap<>();

    public final Vec3 originPos;
    public final ServerLevel originDim;
    public final long timestamp;


    public DepartureEntry(CastingEnvironment env, ServerLevel world){
        Entity caster = env.getCastingEntity();
        assert caster != null;
        this.originPos = caster.position();
        this.originDim = world;
        this.timestamp = world.getServer().overworld().getGameTime();
        Map<ServerLevel, DepartureEntry> list = departureMap.get(env);
        if (list == null){
            Map<ServerLevel, DepartureEntry> newList = new HashMap<>();
            newList.put(world, this);
            departureMap.put(env, newList);
        } else {
            list.put(world, this);
        }
    }

    @Nullable
    public static DepartureEntry getEntry(@NotNull CastingEnvironment env, ServerLevel queried, boolean allowExpired){
        var relevantMap = departureMap.get(env);
        if (relevantMap != null){
            DepartureEntry entry = relevantMap.get(queried);
            if (entry != null && (!entry.isExpired() || allowExpired)){
                return entry;
            }
        }
        return null;
    }
    @Nullable
    public static DepartureEntry getEntry(@NotNull CastingEnvironment env, ServerLevel queried){
        return getEntry(env, queried, false);
    }

    public static void clearMap(){
        //not sure if the loop is actually needed, considering garbage collection, but just in case
        for (CastingEnvironment env : departureMap.keySet()){
            departureMap.get(env).clear();
        }
        departureMap.clear();
    }

    public boolean isWithinCylinder(Vec3 pos){
        Vec3 yZeroPos = new Vec3(pos.x, 0.0, pos.z);
        Vec3 yZeroPos2 = new Vec3(originPos.x, 0.0, originPos.z);
        return yZeroPos.distanceTo(yZeroPos2) <= 8;
    }
    public boolean isExpired(){
        return originDim.getServer().overworld().getGameTime() > timestamp;
    }
}

