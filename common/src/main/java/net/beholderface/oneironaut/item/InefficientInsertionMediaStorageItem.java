package net.beholderface.oneironaut.item;

import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import net.minecraft.item.ItemStack;

public class InefficientInsertionMediaStorageItem extends ItemMediaHolder {
    public InefficientInsertionMediaStorageItem(Settings pProperties) {
        super(pProperties);
    }

    @Override
    public long getMaxMedia(ItemStack stack){
        return Long.MAX_VALUE;
    }

    @Override
    public long insertMedia(ItemStack stack, long amount, boolean simulate) {
        long mediaHere = getMedia(stack);
        long emptySpace = getMaxMedia(stack) - mediaHere;
        if (emptySpace <= 0) {
            return 0;
        }
        if (amount < 0) {
            amount = emptySpace;
        }

        double penalty = Math.pow(mediaHere / 50000000d, 1.1) + 1;

        long inserting = (long) Math.min(amount / penalty, emptySpace);

        if (!simulate) {
            var newMedia = mediaHere + inserting;
            setMedia(stack, newMedia);
        }
        return inserting;
    }

    @Override
    public boolean canProvideMedia(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canRecharge(ItemStack stack) {
        return true;
    }

    public static final double displayLogBase = 1.54756499d; //desmos says using this as a base makes maxlong return 100.000000524, good for media bar stuff
    private double getDisplayLogProgress(ItemStack stack){
        return Math.floor(BottomlessMediaItem.arbitraryLog(displayLogBase, Math.max(this.getMedia(stack), 1))) / 100d;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return MediaHelper.mediaBarColor((long)(this.getDisplayLogProgress(stack) * 1000000), 1000000);
    }
    @Override
    public int getItemBarStep(ItemStack stack){
        return (int) Math.min(13, Math.floor(BottomlessMediaItem.arbitraryLog(2d, (getMedia(stack) / 100000d) + 1)));
    }
    @Override
    public boolean isItemBarVisible(ItemStack pStack) {
        return getMedia(pStack) > 0;
    }
}
