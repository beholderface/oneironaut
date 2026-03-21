package org.arcticquests.dev.oneironaut.oneironautt.components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class DoubleComponent  {
    private double value = 1f;
    private Entity entity;

    //public static final ComponentKey<DoubleComponent> VOLUME = ComponentRegistry.getOrCreate(new Identifier("oneironaut", "wisp_volume"), DoubleComponent.class);


    public DoubleComponent(Entity e){
        this.entity = e;
    }
    public double getValue() {
        return this.value;
    }

    public void setValue(double newValue) {
        this.value = newValue;
        //DoubleComponent.VOLUME.sync(this.entity);
    }

    public void readFromNbt(CompoundTag tag) {
        this.value = tag.getDouble("value");
    }

    public void writeToNbt(CompoundTag tag) {
        tag.putDouble("value", this.value);
    }

    //public static final ComponentKey<DoubleComponent> WISP_VOLUME = ComponentRegistry.getOrCreate(new Identifier("oneironaut", "wisp_volume"), DoubleComponent.class);

}
