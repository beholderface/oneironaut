package org.arcticquests.dev.oneironaut.oneironautt.components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class BoolComponent   {
    private boolean value = false;
    private Entity entity;

    public BoolComponent(Entity e){
        this.entity = e;
    }
    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean newValue) {
        this.value = newValue;
        //DoubleComponent.VOLUME.sync(this.entity);
    }

    public void readFromNbt(CompoundTag tag) {
        this.value = tag.getBoolean("value");
    }

    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean("value", this.value);
    }

}
