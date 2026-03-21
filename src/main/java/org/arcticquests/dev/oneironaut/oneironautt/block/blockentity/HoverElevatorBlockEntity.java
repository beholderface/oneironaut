package org.arcticquests.dev.oneironaut.oneironautt.block.blockentity;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.misc.PlayerPositionRecorder;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.block.HoverElevatorBlock;
import org.arcticquests.dev.oneironaut.oneironautt.network.HoverliftAntiDesyncPacket;
import org.arcticquests.dev.oneironaut.oneironautt.network.OneironautNetwork;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautTags;

import java.util.*;

import static org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt.vecProximity;


public class HoverElevatorBlockEntity extends BlockEntity {

    public static final Map<LivingEntity, Integer> SERVER_HOVER_MAP = new HashMap<>();
    public static final Map<LivingEntity, Integer> CLIENT_HOVER_MAP = new HashMap<>();
    private static Pair<Long, Boolean> LAST_CALL;// = new Pair<>(0L, null);
    private static final DirectionProperty FACING = HoverElevatorBlock.FACING;
    public static final int color = new FrozenPigment(HexItems.DYE_PIGMENTS.get(DyeColor.PURPLE).getDefaultInstance(), Util.NIL_UUID).getColorProvider().getColor(0f, Vec3.ZERO);

    private AABB pairCuboid = null;
    private int level = 0;
    private final AABB defaultCuboid;

    public HoverElevatorBlockEntity(BlockPos pos, BlockState state) {
        super(OneironautBlockRegistry.HOVER_ELEVATOR_ENTITY.get(), pos, state);
        this.defaultCuboid = new AABB(pos);
    }

    public void tick(Level world, BlockPos pos, BlockState state){
        RandomSource rand = world.random;
        Direction dir = state.getValue(FACING);
        Vec3i dirVec = dir.getNormal();
        Vec3 dirVec3d = new Vec3(dirVec.getX(), dirVec.getY(), dirVec.getZ());
        int num = 0;
        if (state.getValue(HoverElevatorBlock.POWERED)){
            if (this.pairCuboid == null || world.getGameTime() % 20 == 0){
                //search for paired elevator
                this.pairCuboid = findPair(world, pos, state);
                /*if (!this.pairCuboid.equals(this.defaultCuboid)){
                    Oneironautfinal.LOGGER.info("Found paired elevator");
                }*/
            }
            List<LivingEntity> detectedEntities = new ArrayList<>();
            for (Entity e : world.getEntities((Entity) null, pairCuboid, (entity)-> {return true;})){
                if (e instanceof LivingEntity le){
                    if (le instanceof Player player && (player.isSpectator() || player.getAbilities().flying)){
                        continue;
                    }
                    detectedEntities.add(le);
                    /*if (world.getTime() % 20 == 0 && !world.isClient){
                        Oneironautfinal.LOGGER.info("Found living entity " + le.getDisplayName());
                    }*/
                }
            }
            num = Math.min(detectedEntities.size(), 15);
            //world.updateListeners(pos, state, state.with(HoverElevatorBlock.LEVEL, Math.max(detectedEntities.size(), 15)), 2);
            int axialBit = switch (state.getValue(FACING).getAxis()){
                case X -> 1;
                case Y -> 2;
                case Z -> 4;
            };
            Map<LivingEntity, Integer> relevantMap = !world.isClientSide ? SERVER_HOVER_MAP : CLIENT_HOVER_MAP;
            for (LivingEntity livingEntity : detectedEntities){
                int found = relevantMap.getOrDefault(livingEntity, 0);
                relevantMap.put(livingEntity, found | axialBit);
                Vec3 entityPos = livingEntity.position().add(livingEntity.getBoundingBox().getXsize() / 2, 0, livingEntity.getBoundingBox().getZsize() / 2);
                Vec3 entityVel = livingEntity.getDeltaMovement();
                if (world.isClientSide && world instanceof ClientLevel clientWorld){
                    clientWorld.addParticle(new ConjureParticleOptions(livingEntity instanceof Player player ?
                                    IXplatAbstractions.INSTANCE.getPigment(player).getColorProvider().getColor(world.getGameTime(), player.position()) : color),
                            entityPos.x + (((rand.nextGaussian() * 2) - 1) / 5), entityPos.y + (((rand.nextGaussian() * 2) - 1) / 5) + (rand.nextIntBetweenInclusive(0, (int) (livingEntity.getBbHeight() * 20f)) / 20f),
                            entityPos.z + (((rand.nextGaussian() * 2) - 1) / 5), entityVel.x, entityVel.y + 0.1, entityVel.z);
                }
            }
            if (world.isClientSide && world instanceof ClientLevel clientWorld && !pairCuboid.equals(defaultCuboid)){
                Vec3 particleCenter = Vec3.atCenterOf(new Vec3i(pos.getX(), pos.getY(), pos.getZ())).add(dirVec3d.scale(0.5));
                Vec3 dirVelVec = dirVec3d.scale(0.25);
                if (rand.nextIntBetweenInclusive(1, 10) <= 3){
                    clientWorld.addParticle(new ConjureParticleOptions(color),
                            particleCenter.x + (((rand.nextGaussian() * 2) - 1) / 7), particleCenter.y + (((rand.nextGaussian() * 2) - 1) / 7),
                            particleCenter.z + (((rand.nextGaussian() * 2) - 1) / 7), dirVelVec.x, dirVelVec.y, dirVelVec.z);
                }
            }
        }
        if (world.getGameTime() % 10 == 0){
            this.updateLevel(num, world, pos);
        }
    }

    private void updateLevel(int newLevel, Level world, BlockPos pos){
        if (this.level != newLevel){
            this.level = newLevel;
            world.blockUpdated(pos, OneironautBlockRegistry.HOVER_ELEVATOR.get());
        }
    }


    private AABB findPair(Level world, BlockPos pos, BlockState state){
        Direction dir = state.getValue(FACING);
        Vec3i dirVec = dir.getNormal();
        BlockPos current;
        AABB output = this.defaultCuboid;
        int i = 1;
        int initialRange = (dir.getAxis() == Direction.Axis.Y ? 128 : 64) + 1;
        int adjustedRange = initialRange;
        int repeaters = 0;
        for (; i <= adjustedRange; i++){
            current = pos.offset(dirVec.multiply(i));
            BlockState checkedState = world.getBlockState(current);
            if (checkedState.getBlock() == OneironautBlockRegistry.HOVER_ELEVATOR.get()){
                if (checkedState.getValue(HoverElevatorBlock.POWERED)){
                    if(checkedState.getValue(FACING).getOpposite().equals(dir)){
                        output = new AABB(pos, current.offset(1, 1, 1));
                        break;
                    } else if (checkedState.getValue(FACING).equals(dir)) {
                        //not sure if there would be any use case for >   << but it feels weird to let you do that
                        break;
                    }
                }
            } else if (checkedState.getBlock() == OneironautBlockRegistry.HOVER_REPEATER.get() && repeaters < 3){
                adjustedRange = i + (initialRange - 1);
                repeaters++;
            } else if (checkedState.is(OneironautTags.Blocks.breakImmune) || checkedState.is(OneironautTags.Blocks.blocksRaycast)){
                break;
            }
        }
        return output;
    }

    private static Set<ServerPlayer> RECENT_USERS = new HashSet<>();
    public static void processHover(boolean isServer, long timestamp){
        Map<LivingEntity, Integer> relevantMap = isServer ? SERVER_HOVER_MAP : CLIENT_HOVER_MAP;
        double threshold = 0.75;
        if (LAST_CALL != null){
            if (!isServer && Minecraft.getInstance().level == null){
                //Oneironautfinal.LOGGER.info("No client world present, deleting last hoverlift call information.");
                LAST_CALL = null;
                relevantMap.clear();
                return;
            }/* else if (isServer == LAST_CALL.getSecond()){
                Oneironautfinal.LOGGER.info("Client/server issue processing hoverlift. Skipping.   " + isServer + " " + LAST_CALL.getSecond());
                return;
            }*/ else if (LAST_CALL.getFirst() >= timestamp){
                if (timestamp != -1){
                    //Oneironautfinal.LOGGER.info("Wrong hoverlift processing timestamp. Skipping.");
                    relevantMap.clear();
                    return;
                } else {
                    LAST_CALL = new Pair<>(timestamp, isServer);
                }
            }
        } else if (!isServer && Minecraft.getInstance().level == null) {
            relevantMap.clear();
            return;
        } else {
            LAST_CALL = new Pair<>(timestamp, isServer);
        }
        for (LivingEntity entity : relevantMap.keySet()){
            Vec3 hoverVec = Vec3.ZERO;
            Vec3 look = entity.getLookAngle();
            int axesNum = relevantMap.getOrDefault(entity, 0);
            int divisor = 15;
            boolean counterVerticalMomentum = true;
            boolean up = (axesNum & 2) == 2;
            if (!entity.isShiftKeyDown()){
                if ((axesNum & 1) == 1){
                    double eastScore = vecProximity(Direction.EAST, look);
                    double westScore = vecProximity(Direction.WEST, look);
                    if ((eastScore <= threshold || westScore <= threshold) && Math.abs(hoverVec.x) < Math.abs(look.x / divisor)){
                        hoverVec = hoverVec.add(look.x * (1.0 / divisor) * (1 - Math.min(eastScore, westScore)), 0.0, 0.0);
                    }
                }
                if (up){
                    double upScore = vecProximity(Direction.UP, look);
                    double downScore = vecProximity(Direction.DOWN, look);
                    if ((upScore <= threshold || downScore <= threshold) && Math.abs(hoverVec.y) < Math.abs(look.y / divisor)){
                        hoverVec = hoverVec.add(0.0, look.y * (1.0 / divisor) * (1 - Math.min(upScore, downScore)), 0.0);
                        counterVerticalMomentum = false;
                    }
                }
                if ((axesNum & 4) == 4){
                    double southScore = vecProximity(Direction.SOUTH, look);
                    double northScore = vecProximity(Direction.NORTH, look);
                    if ((southScore <= threshold || northScore <= threshold) && Math.abs(hoverVec.z) < Math.abs(look.z / divisor)){
                        hoverVec = hoverVec.add(0.0, 0.0, look.z * (1.0 / divisor) * (1 - Math.min(southScore, northScore)));
                    }
                }
            }
            boolean lookingUp = vecProximity(Direction.UP, look) <= threshold;
            if ((entity.level().getGameTime() % 10 == 0 || !entity.hasEffect(MobEffects.SLOW_FALLING)) && !(lookingUp && up)){
                entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, entity.isShiftKeyDown() ? 60 : 11, 0, true, false, true));
            }
            Vec3 velocity = entity.getDeltaMovement();
            if (entity instanceof ServerPlayer serverPlayerEntity){
                velocity = PlayerPositionRecorder.getMotion(serverPlayerEntity);
                if (serverPlayerEntity.hasEffect(MobEffects.SLOW_FALLING)){
                    RECENT_USERS.add(serverPlayerEntity);
                }
            }
            double antigravNum = lookingUp && up ? 0.08 : 0.01;
            boolean applyAntigrav = true;
            if (counterVerticalMomentum){
                if (velocity.y < -0.0125){
                    hoverVec = hoverVec.add(0.0, 0.025, 0.0);
                } else if (velocity.y > 0.01){
                    applyAntigrav = false;
                    //this feels kinda jank but it's the only thing that's reliably worked for stopping upwards motion
                    //I do not know why
                    hoverVec = hoverVec.add(0.0, velocity.y * -1, 0.0);
                }
            }
            hoverVec = hoverVec.add(new Vec3(0.0, applyAntigrav ? antigravNum : 0.0, 0.0));
            entity.push(hoverVec.x, hoverVec.y, hoverVec.z);
        }
        if (isServer){
            MinecraftServer server = Oneironaut.getCachedServer();
            if (server.overworld().getGameTime() % 10 == 0){
                for (ServerPlayer player : RECENT_USERS){
                    if (!player.hasEffect(MobEffects.SLOW_FALLING)){
                        OneironautNetwork.CHANNEL.send(
                                PacketDistributor.PLAYER.with(() -> player),
                                new HoverliftAntiDesyncPacket()
                        );
                    }
                }
                RECENT_USERS.clear();
            }
        }
        relevantMap.clear();
    }
}
