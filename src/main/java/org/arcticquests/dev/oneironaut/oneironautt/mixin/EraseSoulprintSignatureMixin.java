package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "at.petrak.hexcasting.common.casting.actions.spells.OpErase$Spell")
public class EraseSoulprintSignatureMixin {
    @Final
    @Shadow
    private ItemStack stack;

    @Inject(method = "cast(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)V", at = @At(value = "TAIL", remap = false), remap = false)
    public void eraseSignature(CastingEnvironment env, CallbackInfo ci){
        CompoundTag nbt = stack.getTag();
        if (nbt != null && nbt.hasUUID("soulprint_signature")){
            nbt.remove("soulprint_signature");
            stack.setTag(nbt);
        }
    }
}
