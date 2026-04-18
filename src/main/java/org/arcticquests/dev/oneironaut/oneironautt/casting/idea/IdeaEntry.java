package org.arcticquests.dev.oneironaut.oneironautt.casting.idea;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.utils.NBTHelper;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.arcticquests.dev.oneironaut.oneironautt.OneironautConfig;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

public class IdeaEntry<T> {

    public static final String TAG_ENTRY_TYPE = "entryType";
    public static final String TAG_ENTRY_DATA = "entryData";
    public static final String TAG_ENTRY_TIMESTAMP = "entryTimestamp";
    public static final String TAG_ENTRY_WRITER_ID = "entryWriterID";
    public static final String TAG_ENTRY_WRITER_NAME = "entryWriterName";

    public final T payload;
    public final EntryType type;
    public final long creationTimestamp;
    public final long lifetime;
    @Nullable
    public final UUID writerID;
    @Nullable
    public final Component writerName;

    public IdeaEntry(T payload, long timestamp, @Nullable Entity writer){
        assert payload != null;
        if (payload instanceof Entity original){
            Entity copied = original.getType().create(original.level());
            if (copied != null){
                copied.restoreFrom(original);
            }
            payload = (T) copied;
        }
        this.payload = payload;
        EntryType type = EntryType.getTypeFromObject(payload);
        if (type == null){
            throw new IllegalArgumentException("Could not find appropriate idea entry type for payload " + payload);
        }
        this.type = type;
        this.creationTimestamp = timestamp;
        this.lifetime = (long) (OneironautConfig.getServer().getIdeaLifetime() * type.lifetimeMultiplier);
        if (writer != null){
            this.writerID = writer.getUUID();
            this.writerName = writer.getName();
        } else {
            this.writerName = Component.empty();
            this.writerID = Util.NIL_UUID;
        }
    }
    public IdeaEntry(T payload, long timestamp, Component writerName, UUID writerID){
        assert payload != null;
        this.payload = payload;
        EntryType type = EntryType.getTypeFromObject(payload);
        if (type == null){
            throw new IllegalArgumentException("Could not find appropriate idea entry type for payload " + payload);
        }
        this.type = type;
        this.creationTimestamp = timestamp;
        this.lifetime = (long) (OneironautConfig.getServer().getIdeaLifetime() * type.lifetimeMultiplier);
        this.writerName = writerName != null ? writerName : Component.empty();
        this.writerID = writerID != null ? writerID : Util.NIL_UUID;
    }

    public CompoundTag serialize(){
        CompoundTag inner = this.type.serializer.apply(this);
        if (inner == null){
            return null;
        }
        CompoundTag outer = new CompoundTag();
        NBTHelper.putCompound(outer, TAG_ENTRY_DATA, inner);
        outer.putString(TAG_ENTRY_TYPE, this.type.toString());
        outer.putLong(TAG_ENTRY_TIMESTAMP, this.creationTimestamp);
        outer.putUUID(TAG_ENTRY_WRITER_ID, this.writerID);
        outer.putString(TAG_ENTRY_WRITER_NAME, Component.Serializer.toJson(this.writerName));
        return outer;
    }

    public static IdeaEntry<?> deserialize(CompoundTag nbt, ServerLevel world){
        return EntryType.valueOf(nbt.getString(TAG_ENTRY_TYPE)).deserializer.apply(nbt, world);
    }

    public boolean isExpired(long currentTime){
        return this.creationTimestamp + this.lifetime < currentTime;
    }

    public enum EntryType {
        IOTA(IdeaEntry::deserializeIotaEntry, IdeaEntry::serializeIotaEntry, (checked)->checked instanceof Iota, 1.0),
        ENTITY(IdeaEntry::deserializeEntityEntry, IdeaEntry::serializeEntityEntry, (checked)->checked instanceof Entity
                && !(checked instanceof Player), 1.0);

        public final BiFunction<CompoundTag, ServerLevel, IdeaEntry<?>> deserializer;
        public final Function<IdeaEntry<?>, CompoundTag> serializer;
        public final Function<Object, Boolean> checker;
        public final double lifetimeMultiplier;

        EntryType(BiFunction<CompoundTag, ServerLevel, IdeaEntry<?>> deserializer, Function<IdeaEntry<?>, CompoundTag> serializer,
                 Function<Object, Boolean> checker, double lifetimeMultiplier){
            this.deserializer = deserializer;
            this.serializer = serializer;
            this.checker = checker;
            this.lifetimeMultiplier = lifetimeMultiplier;
        }

        @Nullable
        public static EntryType getTypeFromObject(Object checked){
            for (EntryType type : EnumSet.allOf(EntryType.class)){
                if (type.checker.apply(checked)){
                    return type;
                }
            }
            return null;
        }
    }

    public static IdeaEntry<Iota> deserializeLegacyEntry(CompoundTag nbt, ServerLevel world){
        CompoundTag iotaNbt = nbt.getCompound("iota");
        if ((iotaNbt.getLong("timestamp") + IdeaInscriptionManager.lifetime) >= world.getGameTime()){
            Iota iota = IotaType.deserialize(iotaNbt/*.getCompound("iota")*/, world);
            UUID uuid = nbt.getUUID("writer");
            Component name = Component.literal("???");
            if (world.getServer().getPlayerList().getPlayer(uuid) != null){
                name = world.getServer().getPlayerList().getPlayer(uuid).getName(); //why is intellij complaining here
            }
            return new IdeaEntry<Iota>(iota, nbt.getLong("timestamp"), name, uuid);
        }
        return null;
    }

    public static final String TAG_IOTA_DATA = "iotaData";

    protected static IdeaEntry<Iota> deserializeIotaEntry(CompoundTag nbt, ServerLevel world){
        CompoundTag inner = nbt.getCompound(TAG_ENTRY_DATA);
        return new IdeaEntry<Iota>(IotaType.deserialize(inner.getCompound(TAG_IOTA_DATA), world), nbt.getLong(TAG_ENTRY_TIMESTAMP),
                Component.Serializer.fromJson(nbt.getString(TAG_ENTRY_WRITER_NAME)), nbt.getUUID(TAG_ENTRY_WRITER_ID));
    }
    protected static CompoundTag serializeIotaEntry(IdeaEntry<?> entry){
        if (entry.payload instanceof Iota payload){
            CompoundTag out = new CompoundTag();
            NBTHelper.putCompound(out, TAG_IOTA_DATA, IotaType.serialize(payload));
            return out;
        } else {
            return null;
        }
    }

    public static final String TAG_ENTITY_TYPE = "entityType";
    public static final String TAG_ENTITY_DATA = "entityData";

    protected static IdeaEntry<Entity> deserializeEntityEntry(CompoundTag nbt, ServerLevel world){
        CompoundTag inner = nbt.getCompound(TAG_ENTRY_DATA);
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(inner.getString(TAG_ENTITY_TYPE)));
        Entity entity = entityType.create(world);
        if (entity == null){
            return null;
        } else {
            entity.load(inner.getCompound(TAG_ENTITY_DATA));
        }
        return new IdeaEntry<Entity>(entity, nbt.getLong(TAG_ENTRY_TIMESTAMP),
                Component.Serializer.fromJson(nbt.getString(TAG_ENTRY_WRITER_NAME)), nbt.getUUID(TAG_ENTRY_WRITER_ID));
    }
    protected static CompoundTag serializeEntityEntry(IdeaEntry<?> entry){
        if (entry.payload instanceof Entity payload){
            CompoundTag entityData = payload.saveWithoutId(new CompoundTag());
            CompoundTag out = new CompoundTag();
            NBTHelper.putCompound(out, TAG_ENTITY_DATA, entityData);
            var maybeType = BuiltInRegistries.ENTITY_TYPE.getResourceKey(payload.getType());
            if (maybeType.isPresent()){
                out.putString(TAG_ENTITY_TYPE, maybeType.get().location().toString());
            } else {
                return null;
            }
            return out;
        } else {
            return null;
        }
    }
}
