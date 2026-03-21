package org.arcticquests.dev.oneironaut.oneironautt.casting;

import at.petrak.hexcasting.api.utils.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DisintegrationProtectionManager extends SavedData {

    public static final String ID = Oneironaut.MODID + "_disintegration";
    private final Map<UUID, DisintegrationProtectionEntry> entries = new HashMap<>();
    public static final String TAG_ENTRIES = "entries";

    public DisintegrationProtectionManager() {
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag list = new ListTag();
        for (DisintegrationProtectionEntry entry : entries.values()) {
            list.add(entry.serialize());
        }
        NBTHelper.putList(nbt, TAG_ENTRIES, list);
        return nbt;
    }

    public static DisintegrationProtectionManager createFromNbt(CompoundTag nbt) {
        DisintegrationProtectionManager manager = new DisintegrationProtectionManager();
        ListTag list = NBTHelper.getList(nbt, TAG_ENTRIES, ListTag.TAG_COMPOUND);
        if (list == null) {
            throw new IllegalStateException("NbtCompound supplied to DisintegrationProtectionManager#createFromNbt did not contain the proper list");
        }
        manager.entries.clear();
        for (Tag element : list) {
            if (element instanceof CompoundTag compound) {
                DisintegrationProtectionEntry entry = DisintegrationProtectionEntry.deserialize(compound);
                manager.entries.put(entry.getUuid(), entry);
            }
        }
        Oneironaut.LOGGER.info("Reconstructed protection map with {} entries", manager.entries.size());
        return manager;
    }

    public static DisintegrationProtectionManager getServerState(MinecraftServer server) {
        DimensionDataStorage stateManager = Oneironaut.getDeepNoosphere().getDataStorage();
        DisintegrationProtectionManager manager = stateManager.computeIfAbsent(
                DisintegrationProtectionManager::createFromNbt,
                DisintegrationProtectionManager::new,
                ID
        );
        manager.setDirty();
        return manager;
    }

    public void cleanEntries() {
        var iterator = this.entries.values().iterator();
        DisintegrationProtectionEntry entry;
        int removed = 0;
        while (iterator.hasNext()) {
            entry = iterator.next();
            if (entry.isBroken()) {
                iterator.remove();
                removed++;
            }
        }
        Oneironaut.LOGGER.info("Disintegration manager found and removed {} dead entries", removed);
    }

    public void removeEntry(DisintegrationProtectionEntry entry) {
        this.entries.remove(entry.getUuid());
        this.setDirty();
    }

    public void addEntry(DisintegrationProtectionEntry entry) {
        entries.put(entry.getUuid(), entry);
        this.setDirty();
    }

    @Nullable
    public DisintegrationProtectionEntry getProtectionEntry(Vec3 pos) {
        var iterator = this.entries.values().iterator();
        DisintegrationProtectionEntry entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            if (!entry.isBroken()) {
                if (entry.canProtect(pos)) {
                    return entry;
                }
            } else {
                iterator.remove();
                this.setDirty();
            }
        }
        return null;
    }

    public static class DisintegrationProtectionEntry {
        private final AABB bounds;
        private long hits = 0;
        private long durability = 1;
        private final UUID uuid;

        public DisintegrationProtectionEntry(BlockPos cornerA, BlockPos cornerB, long durability) {
            this.bounds = new AABB(cornerA, cornerB.offset(1, 1, 1));
            this.hits = 0;
            this.durability = durability;
            this.uuid = UUID.randomUUID();
        }

        private DisintegrationProtectionEntry(AABB bounds, long hits, long durability, @Nullable UUID uuid) {
            this.bounds = bounds;
            this.hits = hits;
            this.durability = durability;
            this.uuid = uuid != null ? uuid : UUID.randomUUID();
        }

        public DisintegrationProtectionEntry indestructible(AABB bounds) {
            return new DisintegrationProtectionEntry(bounds, -1L, -1L, MiscAPIKt.toUUID(MiscAPIKt.toBlockPos(bounds.getCenter())));
        }

        public AABB getBounds() {
            return bounds;
        }

        public boolean canProtect(Vec3 pos) {
            return !this.isBroken() && MiscAPIKt.containsPermissive(this.bounds, pos);
        }

        public boolean canProtect(Vec3i pos) {
            return canProtect(Vec3.atLowerCornerOf(pos));
        }

        public long getHits() {
            return hits != -1 ? hits : Long.MIN_VALUE;
        }

        public long getDurability() {
            return durability != -1 ? durability : Long.MAX_VALUE;
        }

        public UUID getUuid() {
            return uuid;
        }

        private void addHits(long addedHits) {
            if (this.hits != -1) {
                this.hits += addedHits;
            }
        }

        public boolean hit(long addedHits, Vec3 pos, ServerLevel world) {
            boolean startedBroken = this.isBroken();
            this.addHits(addedHits);
            boolean newlyBroken = (this.isBroken() && !startedBroken);

            if (world != null) {
                ClientboundSoundPacket hitSoundMessage = getHitMessage(pos, world, newlyBroken);

                for (ServerPlayer player : world.players()) {
                    sendSoundPacketIfInRange(world, player, false, pos.x, pos.y, pos.z, hitSoundMessage);
                }
            }
            return newlyBroken;
        }


        private static void sendSoundPacketIfInRange(ServerLevel level, ServerPlayer player, boolean longDistance,
                                                     double x, double y, double z, ClientboundSoundPacket packet) {
            if (player.level() != level) {
                return;
            }

            BlockPos blockpos = player.blockPosition();
            double range = longDistance ? 512.0D : 32.0D;
            if (blockpos.closerToCenterThan(new Vec3(x, y, z), range)) {
                player.connection.send(packet);
            }
        }

        private static @NotNull ClientboundSoundPacket getHitMessage(Vec3 pos, ServerLevel world, boolean newlyBroken) {
            if (newlyBroken) {
                return new ClientboundSoundPacket(
                        Holder.direct(SoundEvents.GLASS_BREAK),
                        SoundSource.BLOCKS,
                        pos.x, pos.y, pos.z,
                        0.5f, 0.2f,
                        world.getSeed()
                );
            } else {
                return new ClientboundSoundPacket(
                        Holder.direct(SoundEvents.GLASS_HIT),
                        SoundSource.BLOCKS,
                        pos.x, pos.y, pos.z,
                        1f, 0.5f,
                        world.getSeed()
                );
            }
        }

        public boolean hit(Vec3 pos, ServerLevel world) {
            return this.hit(1, pos, world);
        }

        public boolean isBroken() {
            return this.getHits() >= this.getDurability();
        }

        public static final String TAG_HITS = "hits";
        public static final String TAG_DURABILITY = "durability";
        public static final String TAG_CORNER_1 = "corner1";
        public static final String TAG_CORNER_2 = "corner2";
        public static final String TAG_UUID = "uuid";

        public CompoundTag serialize() {
            CompoundTag nbt = new CompoundTag();
            nbt.putLong(TAG_HITS, this.hits);
            nbt.putLong(TAG_DURABILITY, this.durability);
            nbt.putUUID(TAG_UUID, this.getUuid());
            NBTHelper.putCompound(nbt, TAG_CORNER_1, NbtUtils.writeBlockPos(new BlockPos((int) this.bounds.minX, (int) this.bounds.minY, (int) this.bounds.minZ)));
            NBTHelper.putCompound(nbt, TAG_CORNER_2, NbtUtils.writeBlockPos(new BlockPos((int) this.bounds.maxX, (int) this.bounds.maxY, (int) this.bounds.maxZ)));
            return nbt;
        }

        public static DisintegrationProtectionEntry deserialize(CompoundTag compound) {
            long hits = compound.getLong(TAG_HITS);
            CompoundTag compoundA = NBTHelper.getCompound(compound, TAG_CORNER_1);
            CompoundTag compoundB = NBTHelper.getCompound(compound, TAG_CORNER_2);
            if (compoundA == null || compoundB == null) {
                throw new IllegalStateException("NbtCompound supplied to DisintegrationProtectionEntry#deserialize did not contain one or both corner tags.");
            }
            BlockPos cornerA = NbtUtils.readBlockPos(compoundA);
            BlockPos cornerB = NbtUtils.readBlockPos(compoundB);
            long durability = compound.getLong(TAG_DURABILITY);
            UUID uuid = compound.getUUID(TAG_UUID);
            return new DisintegrationProtectionEntry(new AABB(cornerA, cornerB), hits, durability, uuid);
        }
    }
}