package org.arcticquests.dev.oneironaut.oneironautt.block.blockentity;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker;
import at.petrak.hexcasting.common.items.pigment.ItemDyePigment;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import com.mojang.datafixers.util.Pair;
import kotlin.collections.CollectionsKt;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.block.WispBattery;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.jetbrains.annotations.Nullable;
import ram.talia.hexal.api.FunUtilsKt;
import ram.talia.hexal.common.entities.WanderingWisp;

import java.util.ArrayList;
import java.util.List;

import static org.arcticquests.dev.oneironaut.oneironautt.item.BottomlessCastingItem.DUST_AMOUNT;


public class WispBatteryEntity extends BlockEntity implements WorldlyContainer {
    public static final long CAPACITY = MediaConstants.DUST_UNIT * 6400;
    private long media = 0;
    public WispBatteryEntity(BlockPos pos, BlockState state){
        super(OneironautBlockRegistry.WISP_BATTERY_ENTITY.get(), pos, state);
    }

    public static int[] getColors(RandomSource random){
        ArrayList<Integer> colors = new ArrayList<>();
        for (int i = 0; i < 32; i++){
            for (ItemDyePigment color : HexItems.DYE_PIGMENTS.values()){
                colors.add(FunUtilsKt.nextColour((new FrozenPigment(new ItemStack(color), Util.NIL_UUID)), random));
            }
        }
        return CollectionsKt.toIntArray(colors);
    }

    public void tick(Level world, BlockPos pos, BlockState state){
        //only do anything when powered
        if (state.getValue(WispBattery.REDSTONE_POWERED)){
            if (world.isClientSide){
                Vec3 doublePos = new Vec3(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                int[] colors = getColors(world.random);
                world.addParticle(
                        new ConjureParticleOptions(colors[world.random.nextInt(colors.length)]),
                        doublePos.x, doublePos.y, doublePos.z,
                        0.0125 * (world.random.nextDouble() - 0.5),
                        0.0125 * (world.random.nextDouble() - 0.5),
                        0.0125 * (world.random.nextDouble() - 0.5)
                );
            } else {
                if (world.getGameTime() % 80 == 0 && world.getEntitiesOfClass(WanderingWisp.class, AABB.ofSize(Vec3.atCenterOf(pos), 64.0, 64.0, 64.0), (idfk)-> true).size() < 20){
                    long wispSpawnCost = MediaConstants.CRYSTAL_UNIT * 2;
                    if (this.media >= wispSpawnCost || this.media < 0){
                        WanderingWisp wisp = new WanderingWisp(world, Vec3.upFromBottomCenterOf(pos, 1));
                        wisp.setPigment(new FrozenPigment(
                                new ItemStack(CollectionsKt.elementAt(HexItems.DYE_PIGMENTS.values(),
                                        world.random.nextInt(HexItems.DYE_PIGMENTS.size()))),
                                Util.NIL_UUID
                        ));
                        world.addFreshEntity(wisp);
                        if (this.media > 0){
                            this.media = this.media - wispSpawnCost;
                        }
                        this.sync();
                    }
                }
            }
        }
    }

    private static final int[] SLOTS = {0};

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    public long remainingMediaCapacity() {
        if (this.media < 0) {
            return 0;
        }
        return Math.max(0, CAPACITY - this.media);
    }

    public long extractMediaFromItem(ItemStack stack, boolean simulate) {
        if (this.media < 0) {
            return 0;
        }
        return MediaHelper.extractMedia(stack, remainingMediaCapacity(), true, simulate);
    }

    public void insertMedia(ItemStack stack) {
        if (getMedia() >= 0 && !stack.isEmpty() && stack.getItem() == HexItems.CREATIVE_UNLOCKER) {
            setInfiniteMedia();
            stack.shrink(1);
        } else {
            var mediamount = extractMediaFromItem(stack, false);
            if (mediamount > 0) {
                this.media = Math.min(mediamount + media, CAPACITY);
                this.sync();
            }
        }
    }

    public static void applyScryingLensOverlay(List<Pair<ItemStack, Component>> lines,
                                        BlockState state, BlockPos pos, Player observer, Level world, Direction hitFace) {
        if (world.getBlockEntity(pos) instanceof WispBatteryEntity battery) {
            if (battery.getMedia() < 0) {
                lines.add(new Pair<>(new ItemStack(HexItems.AMETHYST_DUST), ItemCreativeUnlocker.infiniteMedia(world)));
            } else {
                var dustCount = (float) battery.getMedia() / (float) MediaConstants.DUST_UNIT;
                var dustCmp = Component.translatable("hexcasting.tooltip.media",
                        DUST_AMOUNT.format(dustCount));
                lines.add(new Pair<>(new ItemStack(HexItems.AMETHYST_DUST), dustCmp));
            }
        }
    }

    public void setInfiniteMedia() {
        this.media = -1;
        this.sync();
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (remainingMediaCapacity() == 0) {
            return false;
        }

        if (stack.is(HexItems.CREATIVE_UNLOCKER)) {
            return true;
        }

        var mediamount = extractMediaFromItem(stack, true);
        return mediamount > 0;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return ItemStack.EMPTY.copy();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ItemStack.EMPTY.copy();
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY.copy();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        insertMedia(stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {

    }

    public long getMedia() {
        return this.media;
    }

    public void sync() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putLong("media", this.media);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.media = nbt.getLong("media");
    }
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

}
