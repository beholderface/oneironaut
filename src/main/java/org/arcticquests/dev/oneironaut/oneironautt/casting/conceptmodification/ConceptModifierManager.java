package org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification;

import at.petrak.hexcasting.api.utils.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.block.ConceptCoreBlock;
import org.arcticquests.dev.oneironaut.oneironautt.block.ConceptModifierBlock;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.ConceptCoreBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.ConceptModifierBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ConceptModifierManager extends SavedData {

    public static final String ID = Oneironaut.MODID + "_conceptmodification";

    private final Map<UUID, Map<BlockPos, ConceptModifier>> modifierMap = new HashMap<>();

    @Nullable
    public ConceptModifier getModifier(UUID playerID, UUID modifierID){
        Map<BlockPos, ConceptModifier> map = this.modifierMap.get(playerID);
        if (map != null){
            return map.get(MiscAPIKt.toBlockPos(modifierID));
        }
        return null;
    }
    @Nullable
    public ConceptModifier getModifier(ServerPlayer player, UUID id){
        return this.getModifier(player.getUUID(), id);
    }

    public List<ConceptModifier> getAllModifiers(UUID playerID){
        List<ConceptModifier> modifiers = new ArrayList<>();
        if (this.modifierMap.containsKey(playerID)){
            return this.modifierMap.get(playerID).values().stream().toList();
        }
        return modifiers;
    }
    public List<ConceptModifier> getAllModifiers(ServerPlayer player){
        return this.getAllModifiers(player.getUUID());
    }

    public ConceptModifier getModifierByType(UUID playerID, ConceptModifier.ModifierType type){
        Map<BlockPos, ConceptModifier> map = this.modifierMap.get(playerID);
        if (map != null){
            for (ConceptModifier modifier : map.values()){
                if (modifier.type == type){
                    return modifier;
                }
            }
        }
        return null;
    }
    public ConceptModifier getModifierByType(ServerPlayer player, ConceptModifier.ModifierType type){
        return this.getModifierByType(player.getUUID(), type);
    }

    public boolean hasModifierType(UUID playerID, ConceptModifier.ModifierType type){
        return this.getModifierByType(playerID, type) != null;
    }
    public boolean hasModifierType(ServerPlayer player, ConceptModifier.ModifierType type){
        return this.hasModifierType(player.getUUID(), type);
    }

    public void addModifier(UUID playerID, ConceptModifier modifier){
        Map<BlockPos, ConceptModifier> map;
        if (this.modifierMap.containsKey(playerID)){
            map = this.modifierMap.get(playerID);
        } else {
            map = new HashMap<>();
            this.modifierMap.put(playerID, map);
        }
        map.put(modifier.hostPos, modifier);
        this.setDirty();
    }
    public void addModifier(ServerPlayer player, ConceptModifier modifier){
        this.addModifier(player.getUUID(), modifier);
    }

    public void removeModifier(UUID playerID, BlockPos modifierPos){
        Map<BlockPos, ConceptModifier> map = this.modifierMap.get(playerID);
        if (map != null){
            if (Oneironaut.getCachedServer() != null){
                ServerPlayer player = Oneironaut.getCachedServer().getPlayerList().getPlayer(playerID);
                ConceptModifier modifier = map.get(modifierPos);
                if (player != null && modifier != null){
                    modifier.onRemove(player);
                }
            }
            map.remove(modifierPos);
            this.setDirty();
        }
    }
    public void removeModifier(UUID playerID, UUID modifierID){
        this.removeModifier(playerID, MiscAPIKt.toBlockPos(modifierID));
    }
    public void removeModifier(UUID playerID, ConceptModifier modifier){
        if (modifier != null){
            this.removeModifier(playerID, modifier.hostPos);
        }
    }

    public int clearPlayerModifiers(UUID playerID){
        Map<BlockPos, ConceptModifier> map = this.modifierMap.get(playerID);
        if (map != null){
            Iterator<ConceptModifier> iterator = map.values().iterator();
            Set<ConceptModifier> toRemove = new HashSet<>();
            while (iterator.hasNext()){
                toRemove.add(iterator.next());
            }
            for (ConceptModifier modifier : toRemove){
                this.removeModifier(playerID, modifier);
            }
            return map.size();
        }
        return -1;
    }

    public int removeAllModifiers(){
        int i = 0;
        for (Map<BlockPos, ConceptModifier> map : this.modifierMap.values()){
            i += map.size();
        }
        this.modifierMap.clear();
        this.setDirty();
        return i;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        for (UUID playerID : modifierMap.keySet()){
            CompoundTag playerNbt = new CompoundTag();
            Map<BlockPos, ConceptModifier> playerMap = modifierMap.get(playerID);
            for (BlockPos pos : playerMap.keySet()){
                NBTHelper.putCompound(playerNbt, String.valueOf(pos.asLong()), playerMap.get(pos).serialize());
            }
            NBTHelper.putCompound(nbt, playerID.toString(), playerNbt);
        }
        return nbt;
    }

    public static ConceptModifierManager createFromNbt(CompoundTag nbt){
        ConceptModifierManager manager = new ConceptModifierManager();
        for (String s : nbt.getAllKeys()){
            CompoundTag playerNBT = nbt.getCompound(s);
            for (String s2 : playerNBT.getAllKeys()){
                ConceptModifier modifier = ConceptModifier.deserialize(playerNBT.getCompound(s2));
                manager.addModifier(UUID.fromString(s), modifier);
            }
        }
        return manager;
    }

    public static ConceptModifierManager getServerState(MinecraftServer server){
        if (server == null){
            return null;
        }
        DimensionDataStorage stateManager = Oneironaut.getDeepNoosphere().getDataStorage();
        ConceptModifierManager manager = stateManager.computeIfAbsent(ConceptModifierManager::createFromNbt,
                ConceptModifierManager::new, ID);
        manager.setDirty();
        return manager;
    }

    public void verifyModifiers(){
        ServerLevel world = Oneironaut.getDeepNoosphere();
        if (world == null){
            return;
        }
        int i = 0;
        for (UUID playerID : this.modifierMap.keySet()){
            Iterator<ConceptModifier> iterator = this.modifierMap.get(playerID).values().iterator();
            ConceptModifier modifier = null;
            while (iterator.hasNext()){
                modifier = iterator.next();
                Block hostPosBlock = world.getBlockState(modifier.hostPos).getBlock();
                boolean appropriateHost = false;
                ConceptModifierBlockEntity be = null;
                if (hostPosBlock instanceof ConceptModifierBlock conceptModifierBlock){
                    if (conceptModifierBlock.type == modifier.type){
                        appropriateHost = true;
                        be = (ConceptModifierBlockEntity) world.getBlockEntity(modifier.hostPos);
                    }
                }
                BlockState coreState = world.getBlockState(modifier.corePos);
                Block corePosBlock = coreState.getBlock();
                boolean appropriateCore = false;
                if (corePosBlock instanceof ConceptCoreBlock conceptCoreBlock){
                    if (world.getBlockEntity(modifier.corePos) instanceof ConceptCoreBlockEntity core){
                        appropriateCore = playerID.equals(core.getStoredUUID())
                                && conceptCoreBlock.getConnectedModifiers(coreState, modifier.corePos, world, null).contains(be);
                    }
                }
                if (!(appropriateCore && appropriateHost)){
                    iterator.remove();
                    i++;
                }
            }
        }
        this.setDirty();
        Oneironaut.LOGGER.info("Removed {} invalid concept modifiers.", i);
    }
}
