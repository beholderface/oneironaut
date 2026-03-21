package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker;
import net.minecraft.resources.ResourceLocation;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;

@Mixin(ItemCreativeUnlocker.class)
public abstract class CubeLoreGrantMixin {
    @ModifyVariable(method = "finishUsingItem", at = @At(value = "STORE", ordinal = 0), name = "names")
    private ArrayList<ResourceLocation> addMemoryFragmentNames(ArrayList<ResourceLocation> array){
        //array.addAll(MemoryFragmentItem.NAMES);
        array.add(Oneironaut.id("lore/root"));
        return array;
    }
}
