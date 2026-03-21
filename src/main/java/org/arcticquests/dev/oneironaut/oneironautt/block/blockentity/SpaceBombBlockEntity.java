package org.arcticquests.dev.oneironaut.oneironautt.block.blockentity;

import at.petrak.hexcasting.common.blocks.circles.BlockSlate;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.casting.OvercastDamageEnchant;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautTags;
import org.jetbrains.annotations.Nullable;
import ram.talia.hexal.common.lib.HexalBlocks;

import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.signum;

public class SpaceBombBlockEntity extends BlockEntity {
    public static final int COUNTDOWN_LENGTH = 20 * 30;
    public static final String COUNTDOWN_TAG = "countdown";
    private int countdown = COUNTDOWN_LENGTH;
    public static final String POSITION_TAG = "cached_position";
    private BlockPos cachedPos = null;
    public SpaceBombBlockEntity(BlockPos pos, BlockState state) {
        super(OneironautBlockRegistry.SPACE_BOMB_ENTITY.get(), pos, state);
        if (this.cachedPos == null){
            this.cachedPos = pos;
        }
    }

    public void tick(Level world, BlockPos pos, BlockState state){
        //no cardboard box fuckery for you
        if (!this.worldPosition.equals(cachedPos)){
            this.countdown = COUNTDOWN_LENGTH;
            cachedPos = this.worldPosition;
            if (state.getValue(BlockSlate.ENERGIZED)){
                world.setBlockAndUpdate(pos, state.setValue(BlockSlate.ENERGIZED, false));
            }
        }
        if (this.level != null){
            Vec3 doublePos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            boolean imminent = this.countdown < COUNTDOWN_LENGTH / (COUNTDOWN_LENGTH / 100);
            boolean preparing = state.getValue(BlockSlate.ENERGIZED);
            if (preparing){
                this.tickCountdown();
                if (imminent){
                    List<Entity> nearbyLiving = world.getEntities((Entity) null, new AABB(pos).inflate(16.0),
                            (it)-> it.position().distanceTo(doublePos) <= 16.0 && it instanceof LivingEntity);
                    for (Entity entity : nearbyLiving){
                        entity.addDeltaMovement(entity.getEyePosition().subtract(doublePos).normalize().scale(0.025));
                    }
                    if (this.countdown <= 0){
                        if (!world.isClientSide){
                            this.explode(nearbyLiving);
                        }
                        for (Entity entity : nearbyLiving){
                            LivingEntity livingEntity = (LivingEntity) entity;
                            livingEntity.addDeltaMovement(livingEntity.getEyePosition().subtract(doublePos).normalize().scale(2));
                        }
                    }
                }
            } else {
                if (this.level.getGameTime() % 10 == 0 && this.countdown < COUNTDOWN_LENGTH){
                    this.countdown++;
                }
            }
            if (this.countdown < COUNTDOWN_LENGTH && world.isClientSide){
                showParticles(preparing, imminent);
            }
        }
    }

    private void showParticles(boolean preparing, boolean imminent){
        boolean exploding = this.countdown < 15;
        assert this.level != null;
        assert this.level.isClientSide;
        Vec3 doublePos = new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
        RandomSource rand = this.level.random;
        double offset = preparing ? 0.0 : 0.5;
        int direction = preparing ? (imminent ? (exploding ? 25 : 5) : 1) : -1;
        int i = 0;
        //doesn't just kill people, also kills their GPUs sometimes
        double risingParticles = (Math.pow(100 - this.countdown, 1.5)) + 8;
        while (i < ((preparing ? (imminent ? risingParticles : 8) : 2))){
            i++;
            double speedMultiplier = 0.025;
            double gaussX = rand.nextGaussian();
            double gaussY = rand.nextGaussian();
            double gaussZ = rand.nextGaussian();
            while (gaussX == 0){
                gaussX = rand.nextGaussian();
            }
            double gaussNormalize = 1 / (pow((pow(gaussX, 2) + pow(gaussY, 2) + pow(gaussZ, 2)), 0.5));
            gaussX = gaussX * gaussNormalize;
            gaussY = gaussY * gaussNormalize;
            gaussZ = gaussZ * gaussNormalize;
            double particlePosX = doublePos.x + gaussX * (rand.nextDouble() + offset);
            double particlePosY = doublePos.y + gaussY * (rand.nextDouble() + offset);
            double particlePosZ = doublePos.z + gaussZ * (rand.nextDouble() + offset);
            double particleVelX = (signum(particlePosX - doublePos.x) * speedMultiplier) * rand.nextDouble();
            double particleVelY = (signum(particlePosY - doublePos.y) * speedMultiplier) * rand.nextDouble();
            double particleVelZ = (signum(particlePosZ - doublePos.z) * speedMultiplier) * rand.nextDouble();
            boolean blue = rand.nextInt(4) == 0;
            level.addParticle(
                    new ConjureParticleOptions(preparing ? (blue ? 0x3296ff : 0xae31d2) : 0x6a31d2),
                    particlePosX, particlePosY, particlePosZ,
                    particleVelX * direction, particleVelY * direction, particleVelZ * direction
            );
        }
        if (imminent){
            level.playSound(null, this.worldPosition, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 2.0f, 1.0f);
        }
    }

    public int getCountdown(){
        return this.countdown;
    }

    private void tickCountdown(){
        this.countdown--;
        assert this.level != null;
        if (this.countdown % 20 == 0 && this.countdown > 0){
            boolean imminent = this.countdown < COUNTDOWN_LENGTH / (COUNTDOWN_LENGTH / 100);
            SoundEvent sound = imminent ? SoundEvents.BELL_BLOCK : SoundEvents.UI_BUTTON_CLICK.value();
            float volume = imminent ? 0.75f : 1.0f;
            this.level.playSound(null, this.worldPosition, sound, SoundSource.BLOCKS, volume, imminent ?
                    1.0f : ((float)COUNTDOWN_LENGTH - this.countdown) /*between 0 and 600*// ((float) COUNTDOWN_LENGTH / 1.25f)
            );
        }
    }

    public void explode(List<Entity> living){
        assert this.level != null;
        Vec3 doublePos = new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
        BlockState createdBlockstate = !Oneironaut.isWorldNoosphere((ServerLevel) this.level) ? HexalBlocks.SLIPWAY.defaultBlockState() : OneironautBlockRegistry.NOOSPHERE_GATE.get().defaultBlockState();
        this.level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
        this.level.explode(null, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), 5.0f, Level.ExplosionInteraction.BLOCK);
        for (Entity entity : living){
            //check for warded obsidian in the path
            Vec3 entityPos = entity.position();
            boolean shielded = false;
            double d = 0.0;
            Vec3 checkedPos = doublePos.lerp(entityPos, d);
            for (; doublePos.distanceTo(checkedPos) <= doublePos.distanceTo(entityPos); d += 1.0/64.0){
                if (this.level.getBlockState(BlockPos.containing(checkedPos.x, checkedPos.y, checkedPos.z)).getBlock() == OneironautBlockRegistry.HEX_RESISTANT_BLOCK.get()){
                    shielded = true;
                    break;
                }
                checkedPos = doublePos.lerp(entityPos, d);
            }
            if (!shielded){
                LivingEntity livingEntity = (LivingEntity) entity;
                OvercastDamageEnchant.applyMindDamage(null, livingEntity,
                        (int)Math.floor(16 - entityPos.distanceTo(doublePos)) * 2,
                        livingEntity.getType().is(OneironautTags.Entities.mindRenderAutospare));
            }
        }
        this.level.playSound(null, this.worldPosition, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 2.0f, 0.75f);
        this.level.setBlockAndUpdate(this.worldPosition, createdBlockstate);
    }

    public void sync() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt(COUNTDOWN_TAG, this.countdown);
        int[] posArray = {this.cachedPos.getX(), this.cachedPos.getY(), this.cachedPos.getZ()};
        nbt.putIntArray(POSITION_TAG, posArray);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.countdown = nbt.getInt(COUNTDOWN_TAG);
        int[] posArray = nbt.getIntArray(POSITION_TAG);
        this.cachedPos = new BlockPos(posArray[0], posArray[1], posArray[2]);
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
