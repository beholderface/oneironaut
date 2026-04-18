package org.arcticquests.dev.oneironaut.oneironautt.mixin.IotaKeyableMixins;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.casting.idea.IdeaKeyable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3Iota.class)
public abstract class Vec3IotaKeyableMixin implements IdeaKeyable {
    @Shadow public abstract Vec3 getVec3();

    @Override
    public String getKey() {
        return BlockPos.containing(this.getVec3()).toString();
    }

    @Override
    public boolean isValidKey(CastingEnvironment env) {
        BlockPos pos = BlockPos.containing(this.getVec3());
        WorldBorder border = Oneironaut.getCachedServer().overworld().getWorldBorder();
        return pos.getY() < -64 || pos.getY() > 320 || !(border.isWithinBounds(pos));
    }
}
