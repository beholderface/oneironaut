package net.beholderface.oneironaut.fabric.mixin;

import at.petrak.hexcasting.common.loot.AddPerWorldPatternToScrollFunc;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.beholderface.oneironaut.registry.OneironautTags;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AddPerWorldPatternToScrollFunc.class)
public class BlockPatternFromLootScrollMixin {
    @WrapOperation(method = "doStatic", at = @At(value = "INVOKE", target =
            "Lat/petrak/hexcasting/api/utils/HexUtils;isOfTag(Lnet/minecraft/registry/Registry;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/tag/TagKey;)Z"
            ,remap = true), remap = false)
    private static boolean isAlsoOfThisTag(Registry<?> maybeHolder, RegistryKey<?> holder, TagKey<?> tag, Operation<Boolean> original){
        return original.call(maybeHolder, holder, tag) && !original.call(maybeHolder, holder, OneironautTags.Actions.noLootScrolls);
    }
}
