package org.arcticquests.dev.oneironaut.oneironautt;


import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;

public class NoosphereDimensionEffects extends DimensionSpecialEffects {
    public static final Vec3 fogColor = new Vec3(96.7, 0.0, 103.6);
    public NoosphereDimensionEffects() {
        super(-96.0f, false, SkyType.END, true, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
        return color;
    }

    @Override
    public boolean isFoggyAt(int camX, int camY) {
        return true;
    }

    @Override
    public float[] getSunriseColor(float skyAngle, float tickDelta) {
        float divisor = 512f;
        return new float[]{(float)fogColor.x / divisor, (float)fogColor.y / divisor, (float)fogColor.z / divisor, 0.025f};
    }
}
