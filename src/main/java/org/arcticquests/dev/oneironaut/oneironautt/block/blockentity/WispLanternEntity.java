package org.arcticquests.dev.oneironaut.oneironautt.block.blockentity;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.jetbrains.annotations.Nullable;

public class WispLanternEntity extends BlockEntity {

    public WispLanternEntity(BlockPos pos, BlockState state){
        super(OneironautBlockRegistry.WISP_LANTERN_ENTITY.get(), pos, state);
    }

    private FrozenPigment color = FrozenPigment.DEFAULT.get();

    public void setColor(ItemStack item, Player player){
        if (item.getItem() != Items.BARRIER){
            color = new FrozenPigment(new ItemStack(item.getItemHolder()), player.getUUID());
        } else {
            color = IXplatAbstractions.INSTANCE.getPigment(player);
        }

    }

    @Override
    public void saveAdditional(CompoundTag nbt){
        nbt.put("color", color.serializeToNBT());
    }

    @Override
    public void load(CompoundTag nbt){
        super.load(nbt);
        color = FrozenPigment.fromNBT(nbt.getCompound("color"));
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

    public void tick(Level world, BlockPos pos, BlockState state){
        RandomSource rand = world.random;
        if (world.isClientSide){
            Vec3 jarCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5);
            //render a wisp-like thing
            world.addParticle(
                    new ConjureParticleOptions(color.getColorProvider().getColor(rand.nextInt(), jarCenter)),
                    jarCenter.x + ((rand.nextGaussian() - 0.5) / 50),
                    jarCenter.y,
                    jarCenter.z + ((rand.nextGaussian() - 0.5) / 50),
                    (rand.nextDouble() - 0.5) / 100,
                    0.02 * (rand.nextDouble() - 0.5) / 100,
                    (rand.nextDouble() - 0.5) / 100
            );
        }
    }
}
