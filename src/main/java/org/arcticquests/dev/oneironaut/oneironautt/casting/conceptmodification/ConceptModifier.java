package org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.utils.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.block.Block;
import org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt;
import org.arcticquests.dev.oneironaut.oneironautt.block.ConceptModifierBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ConceptModifier {

    public static final String TAG_COREPOS = "corePos";
    public static final String TAG_HOSTPOS = "hostPos";
    public static final String TAG_PARAMETERS = "parameters";
    public static final String TAG_MODIFIER_TYPE = "type";
    public static final String TAG_ATTRIBUTE_DATA = "attribute";
    public static final String TAG_ATTRIBUTE_MODIFIER = "modifier";
    public static final String TAG_COMPARISON_OVERRIDE = "comparison";
    public static final String TAG_POTENCY = "potency";

    public final BlockPos corePos;
    public final BlockPos hostPos;
    public final UUID id;
    public final CompoundTag parameters;
    public final ModifierType type;

    public ConceptModifier(@Nullable BlockPos corePos, @NotNull BlockPos hostPos, @Nullable CompoundTag parameters, ModifierType type){
        this.corePos = corePos;
        this.hostPos = hostPos;
        this.id = MiscAPIKt.toUUID(hostPos);
        this.parameters = parameters != null ? parameters : new CompoundTag();
        this.type = type;
    }

    public void onApply(ServerPlayer player){
        if (this.type == ModifierType.ATTRIBUTE){
            AttributeModifier modifier = this.getAttributeModifier();
            Attribute attribute = this.getAttributeType();
            AttributeInstance instance = player.getAttribute(attribute);
            assert instance != null;
            //Oneironautfinal.LOGGER.info("Attempting to apply {} modifer to player", attribute);
            instance.addTransientModifier(modifier);
        }
    }

    public void onRemove(ServerPlayer player){
        if (this.type == ModifierType.ATTRIBUTE){
            Attribute attribute = this.getAttributeType();
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance != null){
                instance.removeModifier(this.id);
            }
        }
    }

    public CompoundTag serialize(){
        CompoundTag nbt = new CompoundTag();
        if (this.corePos != null){
            NBTHelper.putCompound(nbt, TAG_COREPOS, NbtUtils.writeBlockPos(this.corePos));
        }
        NBTHelper.putCompound(nbt, TAG_HOSTPOS, NbtUtils.writeBlockPos(this.hostPos));
        NBTHelper.putCompound(nbt, TAG_PARAMETERS, parameters != null ? parameters : new CompoundTag());
        nbt.putString(TAG_MODIFIER_TYPE, this.type.toString());
        return nbt;
    }

    @Nullable
    public static ConceptModifier deserialize(CompoundTag nbt){
        try {
            BlockPos corePos = null;
            if (nbt.contains(TAG_COREPOS)){
                corePos = NbtUtils.readBlockPos(nbt.getCompound(TAG_COREPOS));
            }
            BlockPos hostPos = NbtUtils.readBlockPos(nbt.getCompound(TAG_HOSTPOS));
            CompoundTag parameters = nbt.getCompound(TAG_PARAMETERS);
            ModifierType type = ModifierType.valueOf(nbt.getString(TAG_MODIFIER_TYPE));
            return new ConceptModifier(corePos, hostPos, parameters, type);
        } catch (Exception e){
            return null;
        }
    }

    @Nullable
    public AttributeModifier getAttributeModifier(){
        CompoundTag attributeNBT = this.parameters.getCompound(TAG_ATTRIBUTE_DATA);
        if (attributeNBT != null){
            CompoundTag modifierNBT = attributeNBT.getCompound(TAG_ATTRIBUTE_MODIFIER);
            if (modifierNBT != null){
                return AttributeModifier.load(modifierNBT);
            }
        }
        return null;
    }

    @Nullable
    public Attribute getAttributeType(){
        CompoundTag attributeNBT = this.parameters.getCompound(TAG_ATTRIBUTE_DATA);
        if (attributeNBT != null){
            String modifierID = attributeNBT.getString(TAG_MODIFIER_TYPE);
            if (modifierID != null){
                return BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(modifierID));
            }
        }
        return null;
    }

    public void setAttributeData(Attribute attribute, AttributeModifier modifier){
        ResourceLocation attributeID = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        if (attributeID == null){
            throw new IllegalStateException("Attribute "+ attribute.toString() +" is not registered.");
        }
        CompoundTag attributeNBT = new CompoundTag();
        NBTHelper.putCompound(attributeNBT, TAG_ATTRIBUTE_MODIFIER, modifier.save());
        NBTHelper.putString(attributeNBT, TAG_MODIFIER_TYPE, attributeID.toString());
        NBTHelper.putCompound(this.parameters, TAG_ATTRIBUTE_DATA, attributeNBT);
    }

    public long getMediaCost(Block block){
        if (block instanceof ConceptModifierBlock conceptModifierBlock && conceptModifierBlock.costCalulator != null){
            return (long) (conceptModifierBlock.costCalulator.apply(this.parameters) * MediaConstants.DUST_UNIT);
        }
        return 0;
    }

    public enum ModifierType {
        ANTIEROSION(false, "antierosion"), //implemented
        ATTRIBUTE(true, "attribute"), //implemented
        FALSY_REFERENCE(false, "falsy"), //implemented
        GTP_DROPREDUCTION(true, "gtp_splat"), //implemented
        KEEPINVENTORY(false, "keepinv"),
        LITTERBUG_REFERENCE(false, "litterbug"),
        NO_OVERCAST(false, "nobloodcast"),
        NONE(false, "none"),
        REFERENCE_COMPARISON(true, "ref_comparison"), //implemented
        TOTEM(false, "totem"),
        XL_REFERENCE(true, "ref_size"),
        STACK_LIMIT(false, "stack_limit");

        public final boolean requiresIota;
        public final String translationKey;
        ModifierType(boolean requiresIota, String translation){
            this.requiresIota = requiresIota;
            this.translationKey = "oneironaut.conceptmodifier." + translation;
        }
    }
}
