package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.utils.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class WriteableBlockItem extends BlockItem implements IotaHolderItem {

    public final Function<Iota, Boolean> acceptsIota;

    public WriteableBlockItem(Block block, Properties settings, Function<Iota, Boolean> acceptsIota) {
        super(block, settings);
        this.acceptsIota = acceptsIota;
    }

    public static final String TAG_IOTA = "iota";

    @Override
    public @Nullable CompoundTag readIotaTag(ItemStack stack) {
        CompoundTag blockEntityNBT = stack.getOrCreateTag().getCompound(BlockItem.BLOCK_ENTITY_TAG);
        if (blockEntityNBT != null){
            CompoundTag iotaNBT = blockEntityNBT.getCompound(TAG_IOTA);
            if (iotaNBT != null){
                return iotaNBT;
            }
        }
        return IotaType.serialize(new NullIota());
    }

    @Override
    public boolean writeable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canWrite(ItemStack stack, @Nullable Iota iota) {
        return this.acceptsIota.apply(iota);
    }

    @Override
    public void writeDatum(ItemStack stack, @Nullable Iota iota) {
        CompoundTag nbt = stack.getOrCreateTag();
        CompoundTag blockEntityNBT = nbt.getCompound(BlockItem.BLOCK_ENTITY_TAG);
        NBTHelper.putCompound(blockEntityNBT, TAG_IOTA, IotaType.serialize(iota != null ? iota : new NullIota()));
        NBTHelper.putCompound(nbt, BlockItem.BLOCK_ENTITY_TAG, blockEntityNBT);
    }
}
