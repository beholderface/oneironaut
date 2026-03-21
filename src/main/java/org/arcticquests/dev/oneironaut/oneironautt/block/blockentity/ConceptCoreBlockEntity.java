package org.arcticquests.dev.oneironaut.oneironautt.block.blockentity;

import at.petrak.hexcasting.api.block.HexBlockEntity;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker;
import at.petrak.hexcasting.common.lib.HexItems;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.block.ConceptCoreBlock;
import org.arcticquests.dev.oneironaut.oneironautt.item.WriteableBlockItem;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus.TAG_MEDIA;
import static at.petrak.hexcasting.common.blocks.circles.impetuses.BlockEntityRedstoneImpetus.TAG_STORED_PLAYER;
import static at.petrak.hexcasting.common.blocks.circles.impetuses.BlockEntityRedstoneImpetus.TAG_STORED_PLAYER_PROFILE;
import static org.arcticquests.dev.oneironaut.oneironautt.item.BottomlessCastingItem.DUST_AMOUNT;

public class ConceptCoreBlockEntity extends HexBlockEntity implements WorldlyContainer {

    public static final long MAX_MEDIA_CAPACITY = MediaConstants.DUST_UNIT * 1000000;

    private GameProfile storedPlayerProfile = null;
    private ItemStack storedPlayerHeadStack = null;
    private UUID storedPlayer = null;
    private long storedMedia = 0L;

    public ConceptCoreBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(OneironautBlockRegistry.CONCEPT_CORE_ENTITY.get(), pWorldPosition, pBlockState);
    }

    public void setPlayer(GameProfile profile, UUID player) {
        this.storedPlayerProfile = profile;
        this.storedPlayer = player;
        this.setChanged();
    }

    public void clearPlayer() {
        this.storedPlayerProfile = null;
        this.storedPlayer = null;
    }

    public void updatePlayerProfile() {
        ServerPlayer player = getStoredPlayer();
        if (player != null) {
            GameProfile newProfile = player.getGameProfile();
            if (!newProfile.equals(this.storedPlayerProfile)) {
                this.storedPlayerProfile = newProfile;
                this.setChanged();
            }
        } else {
            this.storedPlayerProfile = null;
        }
    }

    @Nullable
    public ServerPlayer getStoredPlayer() {
        if (this.storedPlayer == null) {
            return null;
        }
        if (!(this.level instanceof ServerLevel slevel)) {
            Oneironaut.LOGGER.error("Called getStoredPlayer on the client");
            return null;
        }
        var e = slevel.getEntity(this.storedPlayer);
        if (e instanceof ServerPlayer player) {
            return player;
        } else {
            return null;
        }
    }
    @Nullable
    public UUID getStoredUUID(){
        return this.storedPlayer;
    }

    public List<ConceptModifierBlockEntity> findConceptBlocks(){
        List<ConceptModifierBlockEntity> output = new ArrayList<>();
        if (this.level != null){
            BlockState state = this.level.getBlockState(this.worldPosition);
            output = ((ConceptCoreBlock)state.getBlock()).getConnectedModifiers(state, this.worldPosition, this.level, null);
        }
        return output;
    }

    @Override
    protected void saveModData(CompoundTag tag) {
        if (this.storedPlayer != null) {
            tag.putUUID(TAG_STORED_PLAYER, this.storedPlayer);
        }
        if (this.storedPlayerProfile != null) {
            tag.put(TAG_STORED_PLAYER_PROFILE, NbtUtils.writeGameProfile(new CompoundTag(), storedPlayerProfile));
        }
        tag.putLong(TAG_MEDIA, this.storedMedia);
    }

    @Override
    protected void loadModData(CompoundTag tag) {
        if (tag.contains(WriteableBlockItem.TAG_IOTA) && this.level != null && this.level instanceof ServerLevel){
            EntityIota iota = (EntityIota) IotaType.deserialize(tag.getCompound(WriteableBlockItem.TAG_IOTA), (ServerLevel) level);
            if (iota.getEntity() instanceof Player player){
                this.storedPlayer = player.getUUID();
                this.storedPlayerProfile = player.getGameProfile();
            }
            return;
        }
        if (tag.contains(TAG_STORED_PLAYER, Tag.TAG_INT_ARRAY)) {
            this.storedPlayer = tag.getUUID(TAG_STORED_PLAYER);
        } else {
            this.storedPlayer = null;
        }
        if (tag.contains(TAG_STORED_PLAYER_PROFILE, Tag.TAG_COMPOUND)) {
            this.storedPlayerProfile = NbtUtils.readGameProfile(tag.getCompound(TAG_STORED_PLAYER_PROFILE));
        } else {
            this.storedPlayerProfile = null;
        }
        if (tag.contains(TAG_MEDIA, Tag.TAG_LONG)) {
            this.storedMedia = tag.getLong(TAG_MEDIA);
        } else {
            this.storedMedia = 0L;
        }
    }

    public void insertMedia(long toAdd){
        if (this.storedMedia <= MAX_MEDIA_CAPACITY){
            long newMedia = this.storedMedia + Math.max(0L, toAdd);
            this.storedMedia = Math.max(0L, Math.min(MAX_MEDIA_CAPACITY, newMedia));
        }
    }

    public void insertMedia(ItemStack stack) {
        if (storedMedia >= 0 && !stack.isEmpty() && stack.getItem() == HexItems.CREATIVE_UNLOCKER) {
            this.setInfiniteMedia();
            stack.shrink(1);
        } else {
            var mediamount = extractMediaFromInsertedItem(stack, false);
            if (mediamount > 0) {
                this.storedMedia = Math.min(mediamount + this.storedMedia, MAX_MEDIA_CAPACITY);
                this.sync();
            }
        }
    }

    public void extractMedia(long toSubtract){
        long newMedia = this.storedMedia - Math.max(0L, toSubtract);
        this.storedMedia = Math.max(0L, newMedia);
    }

    public long getStoredMedia() {
        return storedMedia;
    }

    protected @Nullable
    GameProfile getPlayerName() {
        if (this.level instanceof ServerLevel) {
            Player player = getStoredPlayer();
            if (player != null) {
                return player.getGameProfile();
            }
        }

        return this.storedPlayerProfile;
    }

    public static void applyScryingLensOverlay(List<Pair<ItemStack, Component>> lines,
                                               BlockState state, BlockPos pos, Player observer, Level world, Direction hitFace){
        if (world.getBlockEntity(pos) instanceof ConceptCoreBlockEntity core) {
            if (core.getStoredMedia() < 0) {
                lines.add(new Pair<>(new ItemStack(HexItems.AMETHYST_DUST), ItemCreativeUnlocker.infiniteMedia(world)));
            } else {
                var dustCount = (float) core.getStoredMedia() / (float) MediaConstants.DUST_UNIT;
                var dustCmp = Component.translatable("hexcasting.tooltip.media",
                        DUST_AMOUNT.format(dustCount));
                lines.add(new Pair<>(new ItemStack(HexItems.AMETHYST_DUST), dustCmp));
            }
            var name = core.getPlayerName();
            if (name != null) {
                if (!name.equals(core.storedPlayerProfile) || core.storedPlayerHeadStack == null) {
                    core.storedPlayerProfile = name;
                    var head = new ItemStack(Items.PLAYER_HEAD);
                    NBTHelper.put(head, "SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), name));
                    head.getItem().verifyTagAfterLoad(head.getOrCreateTag());
                    core.storedPlayerHeadStack = head;
                }
                lines.add(new Pair<>(core.storedPlayerHeadStack,
                        Component.translatable("hexcasting.tooltip.lens.impetus.redstone.bound", name.getName())));
            } else {
                lines.add(new Pair<>(new ItemStack(Items.BARRIER),
                        Component.translatable("hexcasting.tooltip.lens.impetus.redstone.bound.none")));
            }
        }
    }

    private static final int[] SLOTS = {0};

    @Override
    public int[] getSlotsForFace(Direction var1) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction dir) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
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
    public ItemStack getItem(int index) {
        return ItemStack.EMPTY.copy();
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ItemStack.EMPTY.copy();
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStack.EMPTY.copy();
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        insertMedia(stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {
        // NO-OP
    }

    public long remainingMediaCapacity() {
        if (this.storedMedia < 0) {
            return 0;
        }
        return Math.max(0, MAX_MEDIA_CAPACITY - this.storedMedia);
    }

    public long extractMediaFromInsertedItem(ItemStack stack, boolean simulate) {
        if (this.storedMedia < 0) {
            return 0;
        }
        return MediaHelper.extractMedia(stack, remainingMediaCapacity(), true, simulate);
    }

    public void setInfiniteMedia() {
        this.storedMedia = -1;
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

        var mediamount = extractMediaFromInsertedItem(stack, true);
        return mediamount > 0;
    }
}
