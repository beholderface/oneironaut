package org.arcticquests.dev.oneironaut.oneironautt.registry;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.casting.NoosphereAmbitExtensionComponent;

public class OneironautCastEnvComponents {
    public static void init(){
        CastingEnvironment.addCreateEventListener((env, nbt)->{
            if (env instanceof PlayerBasedCastEnv){
                if (Oneironaut.isWorldNoosphere(env.getWorld())){
                    env.addExtension(new NoosphereAmbitExtensionComponent(env));
                }
            }
        });
    }
}