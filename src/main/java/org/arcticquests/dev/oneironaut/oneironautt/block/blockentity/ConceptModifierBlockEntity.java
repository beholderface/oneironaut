package org.arcticquests.dev.oneironaut.oneironautt.block.blockentity;

import at.petrak.hexcasting.api.block.HexBlockEntity;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.HexItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.arcticquests.dev.oneironaut.oneironautt.block.ConceptModifierBlock;
import org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification.ConceptModifier;
import org.arcticquests.dev.oneironaut.oneironautt.item.WriteableBlockItem;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautItemRegistry;

import java.util.List;

public class ConceptModifierBlockEntity extends HexBlockEntity {

    public static final String TAG_MODIFIER = "modifier";

    private final boolean typeNeedsIota;

    public final ConceptModifier.ModifierType modifierType;

    private ConceptModifier conceptModifier = null;
    public ConceptModifierBlockEntity(BlockPos pos, BlockState state) {
        super(OneironautBlockRegistry.CONCEPT_MODIFIER_ENTITY.get(), pos, state);
        if (state.getBlock() instanceof ConceptModifierBlock block){
            typeNeedsIota = block.type.requiresIota;
            this.modifierType = block.type;
        } else {
            typeNeedsIota = false;
            this.modifierType = null;
        }
    }

    @Override
    protected void saveModData(CompoundTag tag) {
        if (this.getConceptModifier() != null){
            NBTHelper.putCompound(tag, TAG_MODIFIER, this.getConceptModifier().serialize());
        }
    }

    @Override
    protected void loadModData(CompoundTag tag) {
        if (level != null){
            BlockState state = this.level.getBlockState(this.worldPosition);
            Block block = state.getBlock();
            if (block instanceof ConceptModifierBlock conceptBlock){
                ConceptModifier.ModifierType type = conceptBlock.type;
                boolean requiresIota = type.requiresIota;
                ConceptModifier modifierToSet = null;
                if ((!requiresIota || tag.contains(WriteableBlockItem.TAG_IOTA))){
                    ConceptCoreBlockEntity core = conceptBlock.getCore(state, this.worldPosition, this.level, null);
                    BlockPos corePos = core != null ? core.getBlockPos() : null;
                    if (requiresIota){
                        Iota iota = IotaType.deserialize(tag.getCompound(WriteableBlockItem.TAG_IOTA), null);
                        if (type == ConceptModifier.ModifierType.ATTRIBUTE){
                            double attributeValue = ((DoubleIota)iota).getDouble();
                            ConceptModifier modifier = new ConceptModifier(corePos, this.worldPosition, null, type);
                            modifier.setAttributeData(conceptBlock.getAttribute(), new AttributeModifier(modifier.id, modifier.id.toString(),
                                    attributeValue, AttributeModifier.Operation.MULTIPLY_BASE));
                            modifierToSet = modifier;
                        } else if (type == ConceptModifier.ModifierType.REFERENCE_COMPARISON){
                            boolean overrideValue = ((BooleanIota)iota).getBool();
                            CompoundTag parameter = new CompoundTag();
                            parameter.putBoolean(ConceptModifier.TAG_COMPARISON_OVERRIDE, overrideValue);
                            modifierToSet = new ConceptModifier(corePos, this.worldPosition, parameter, type);
                        } else if (type == ConceptModifier.ModifierType.GTP_DROPREDUCTION){
                            double reductionValue = ((DoubleIota)iota).getDouble();
                            CompoundTag parameter = new CompoundTag();
                            parameter.putDouble(ConceptModifier.TAG_POTENCY, reductionValue);
                            modifierToSet = (new ConceptModifier(corePos, this.worldPosition, parameter, type));
                        }
                    } else {
                        modifierToSet = new ConceptModifier(corePos, this.worldPosition, null, type);
                    }
                }
                this.setConceptModifier(modifierToSet);
            }
        }
        if (this.conceptModifier == null){
            this.setConceptModifier(ConceptModifier.deserialize(tag.getCompound(TAG_MODIFIER)));
        }
    }

    public void tick(Level world, BlockPos pos, BlockState state){
        if (this.conceptModifier == null){
            this.getConceptModifier();
            if (this.conceptModifier != null){
                this.setChanged();
            }
        }
    }

    public void setConceptModifier(ConceptModifier newModifier){
        this.conceptModifier = newModifier;
        this.setChanged();
    }
    public ConceptModifier getConceptModifier(){
        if (this.conceptModifier == null && !this.typeNeedsIota && this.modifierType != ConceptModifier.ModifierType.NONE && this.level != null){
            BlockState state = level.getBlockState(this.worldPosition);
            if (state.getBlock() instanceof ConceptModifierBlock block){
                ConceptCoreBlockEntity core = block.getCore(state, this.worldPosition, level, null);
                BlockPos corePos = core != null ? core.getBlockPos() : null;
                ConceptModifier newModifier = new ConceptModifier(corePos, this.worldPosition, null, block.type);
                this.setConceptModifier(newModifier);
            }
        }
        return this.conceptModifier;
    }
    public boolean hasConceptModifier(){
        return this.getConceptModifier() != null;
    }

    public static void applyScryingLensOverlay(List<Pair<ItemStack, Component>> lines,
                                               BlockState state, BlockPos pos, Player observer, Level world, Direction hitFace){
        ConceptModifierBlockEntity be = (ConceptModifierBlockEntity) world.getBlockEntity(pos);
        if (be != null){
            ConceptModifier modifier = be.getConceptModifier();
            if (modifier != null){
                String translation = modifier.type.translationKey;
                lines.add(Pair.of(state.getBlock().asItem().getDefaultInstance(), Component.translatable(translation)));
                ConceptModifier.ModifierType type = modifier.type;
                if (type == ConceptModifier.ModifierType.ATTRIBUTE){
                    assert modifier.getAttributeType() != null;
                    double modifierValue = modifier.getAttributeModifier().getAmount();
                    lines.add(Pair.of(HexItems.ABACUS.getDefaultInstance(), Component.translatable(("oneironaut.conceptmodifier.attribute.overlay." + (modifierValue > 0 ? "positive" : "negative")),
                                    Component.translatable(modifier.getAttributeType().getDescriptionId()), Math.abs(modifierValue) * 100)));
                } else if (type.requiresIota){
                    CompoundTag parameters = modifier.parameters;
                    if (parameters.contains(ConceptModifier.TAG_POTENCY)){
                        lines.add(Pair.of(HexItems.ABACUS.getDefaultInstance(), Component.translatable("oneironaut.conceptmodifier.lens.potency",
                                String.valueOf(parameters.getDouble(ConceptModifier.TAG_POTENCY)))));
                    }
                    if (parameters.contains(ConceptModifier.TAG_COMPARISON_OVERRIDE)){
                        lines.add(Pair.of(Items.LEVER.getDefaultInstance(), Component.translatable("oneironaut.conceptmodifier.lens.comparison",
                                String.valueOf(parameters.getBoolean(ConceptModifier.TAG_COMPARISON_OVERRIDE)))));
                    }
                }
                if (modifier.corePos == null){
                    lines.add(Pair.of(OneironautItemRegistry.CONCEPT_CORE.get().getDefaultInstance(), Component.translatable("oneironaut.conceptmodifier.nocore")));
                }
            } else {
                lines.add(Pair.of(Items.BARRIER.getDefaultInstance(), Component.translatable("oneironaut.conceptmodifier.nomodifier")));
            }
        }
    }
}
