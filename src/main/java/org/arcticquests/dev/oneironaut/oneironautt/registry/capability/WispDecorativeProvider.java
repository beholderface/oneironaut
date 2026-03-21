package org.arcticquests.dev.oneironaut.oneironautt.registry.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WispDecorativeProvider implements ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("oneironaut", "wisp_decorative");

    private final BoolComponentImpl impl = new BoolComponentImpl();
    private final LazyOptional<IBoolComponent> opt = LazyOptional.of(() -> impl);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return ICapabilitySerializable.super.getCapability(cap);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("value", impl.getValue());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        impl.setValue(nbt.getBoolean("value"));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == WispDecorativeCapability.INSTANCE ? opt.cast() : LazyOptional.empty();
    }
}