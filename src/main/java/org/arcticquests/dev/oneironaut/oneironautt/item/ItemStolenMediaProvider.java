package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;


//stolen because I copied this whole thing from gloop
public class ItemStolenMediaProvider extends Item {
    protected long mediaAmt;
    private boolean grabFromInventory;
    private int priority;

    public static final Set<ItemStolenMediaProvider> allStolenMediaItems = new HashSet<>();

    public ItemStolenMediaProvider(Properties settings, long mediaAmt, boolean grabFromInventory, int priority){
        super(settings);
        this.mediaAmt = mediaAmt;
        this.grabFromInventory = grabFromInventory;
        this.priority = priority;
        allStolenMediaItems.add(this);
    }

    public ItemStolenMediaProvider(Properties settings, int mediaAmt){
        this(settings, mediaAmt, true, ADMediaHolder.CHARGED_AMETHYST_PRIORITY);
    }

    public ItemStolenMediaProvider(Properties settings, long mediaAmt, int priority){
        this(settings, mediaAmt, true, priority);
    }

    public boolean shouldGrabFromInventory(ItemStack stack){
        return grabFromInventory;
    }

    public long getMediaAmount(){
        return mediaAmt;
    }

    public int getPriority(){
        return priority;
    }

    public long getMedia(ItemStack stack){
        return mediaAmt * stack.getCount();
    }

    public long getMaxMedia(ItemStack stack){
        return mediaAmt * stack.getMaxStackSize();
    }

    public void setMedia(ItemStack stack, long media){
        // no
    }

    public boolean canProvideMedia(ItemStack stack){
        return true;
    }

    public boolean canRecharge(ItemStack stack){
        return false;
    }

    public boolean shouldUseOwnWithdrawLogic(ItemStack stack){
        return false;
    }

    public long withdrawMedia(ItemStack stack, long cost, boolean simulate) {
        long mediaHere = getMedia(stack);
        if (cost < 0) {
            cost = mediaHere;
        }
        long realCost = Math.min(cost, mediaHere);
        if (!simulate) {
            stack.shrink((int) Math.ceil(realCost / (double)mediaAmt));
        }
        return realCost;
    }

    public long insertMedia(ItemStack stack, int amount, boolean simulate) {
        return 0; // no don't do that
    }

    public InstancedProvider getProvider(ItemStack stack){
        return new InstancedProvider(stack, this);
    }

    public class InstancedProvider implements ADMediaHolder{
        protected ItemStack innerStack;
        protected ItemStolenMediaProvider providerItem;

        public InstancedProvider(ItemStack stack, ItemStolenMediaProvider providerItem){
            this.innerStack = stack;
            this.providerItem = providerItem;
        }

        @Override
        public long getMedia() {
            return providerItem.getMedia(innerStack);
        }

        @Override
        public long getMaxMedia() {
            return getMedia();
        }

        @Override
        public void setMedia(long media) {
            providerItem.setMedia(innerStack, media);
        }

        @Override
        public boolean canRecharge() {
            return providerItem.canRecharge(innerStack);
        }

        @Override
        public boolean canProvide() {
            return providerItem.canProvideMedia(innerStack);
        }

        @Override
        public int getConsumptionPriority() {
            return providerItem.getPriority();
        }

        @Override
        public boolean canConstructBattery() {
            return true;
        }

        @Override
        public long withdrawMedia(long cost, boolean simulate) {
            return providerItem.withdrawMedia(innerStack, cost, simulate);
        }
    }
}

