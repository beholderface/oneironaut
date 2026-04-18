package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.storage.ItemFocus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes.DimIota;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemLibraryCard extends Item implements IotaHolderItem {
    public ItemLibraryCard(Properties settings) {
        super(settings);
    }

    @Nullable
    public ResourceKey<Level> getDimension(ItemStack stack){
        CompoundTag nbt = stack.getTag();
        //Oneironautfinal.LOGGER.info(nbt);
        if(nbt == null || !nbt.contains(ItemFocus.TAG_DATA, Tag.TAG_COMPOUND))
            return null;
        return new DimIota(nbt.getCompound(ItemFocus.TAG_DATA).getCompound("hexcasting:data").getString(DimIota.DIM_KEY)).getWorldKey();
    }

    @Override
    public @Nullable CompoundTag readIotaTag(ItemStack stack) {
        ResourceKey<Level> regKey = this.getDimension(stack);
        if (regKey != null){
            return IotaType.serialize(new DimIota(regKey));
        }
        return null;
    }

    @Override
    public boolean writeable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canWrite(ItemStack stack, @Nullable Iota iota) {
        return iota instanceof DimIota || iota == null;
    }

    @Override
    public void writeDatum(ItemStack stack, @Nullable Iota iota) {
        if (iota == null){
            stack.removeTagKey(ItemFocus.TAG_DATA);
        } else {
            NBTHelper.put(stack, ItemFocus.TAG_DATA, IotaType.serialize(iota));
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                              TooltipFlag pIsAdvanced) {
        IotaHolderItem.appendHoverText(this, pStack, pTooltipComponents, pIsAdvanced);
    }
}
