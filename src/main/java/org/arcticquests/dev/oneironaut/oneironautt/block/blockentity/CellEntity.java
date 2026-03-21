package org.arcticquests.dev.oneironaut.oneironautt.block.blockentity;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellEntity extends BlockEntity {

    /*
    * the plan is to eventually make it act as a 3D cellular automaton, with rule B6/S567 as mentioned here https://conwaylife.com/wiki/Three-dimensional_cellular_automaton
    * will produce plasmodial psyche, and something else that I haven't decided
    */
    public final Map<BlockPos, BlockState> neighborMap;
    public BlockPos initialPos;
    private Boolean verified;
    public CellEntity(BlockPos pos, BlockState state){
        super(OneironautBlockRegistry.CELL_ENTITY.get(), pos, state);
        this.initialPos = this.worldPosition;
        Level world = this.level;
        this.neighborMap = new HashMap<>();
        if (world != null){
            for(BlockPos neighborPos : getNeighbors(this.worldPosition)){
                neighborMap.put(neighborPos, world.getBlockState(neighborPos));
            }
        }
        this.verified = false;
    }

    public void tick(Level world, BlockPos pos, BlockState state){
        if (this.initialPos == null){
            this.initialPos = pos;
        }
        removeIfMoved();
    }

    public boolean updateNeighborMap(){
        if (removeIfMoved() || this.level == null){
            return false;
        }
        for (BlockPos neighborPos : this.neighborMap.keySet()){
            neighborMap.put(neighborPos, this.level.getBlockState(neighborPos));
        }
        return true;
    }

    private boolean removeIfMoved(){
        /*
        * I am intentionally allowing you to move it to the same coordinates in other dimensions, because that is complex
        * and/or expensive enough to offset the value of the exploit, IMO.
        */
        if (!this.initialPos.equals(this.worldPosition) && level != null){
            Oneironaut.LOGGER.info("position mismatch: initial pos " + this.initialPos.toShortString() + " is not " + this.worldPosition.toShortString());
            level.removeBlockEntity(this.worldPosition);
            level.removeBlock(this.worldPosition, false);
            return true;
        }
        return false;
    }

    private static List<BlockPos> getNeighbors(BlockPos pos){
        return MiscAPIKt.getPositionsInCuboid(pos.offset(-1,-1,-1), pos.offset(1, 1, 1), pos);
    }

    //there's something wrong here that causes /setblock and similar to error when you tell it to produce one with empty NBT, but I have no idea why.
    //the error message is not informative in the slightest
    @Override
    public void saveAdditional(CompoundTag nbt){
        if (this.initialPos == null){
            this.initialPos = this.worldPosition;
        }
        if (this.verified == null){
            this.verified = false;
        }
        nbt.putIntArray("initialPos", new int[]{this.initialPos.getX(),this.initialPos.getY(),this.initialPos.getZ()});
        nbt.putBoolean("verified", this.verified);
    }
    @Override
    public void load(CompoundTag nbt){
        super.load(nbt);
        int[] posArray = nbt.getIntArray("initialPos");
        this.initialPos = posArray == null ? this.worldPosition : new BlockPos(posArray[0],posArray[1],posArray[2]);
        this.verified = nbt.getBoolean("verified");
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

    public boolean getVerified(){
        return this.verified;
    }
    public void setVerified(boolean verification){
        this.verified = verification;
    }
}
