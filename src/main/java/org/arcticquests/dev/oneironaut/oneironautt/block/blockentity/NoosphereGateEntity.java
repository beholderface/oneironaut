package org.arcticquests.dev.oneironaut.oneironautt.block.blockentity;

import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;

public class NoosphereGateEntity extends BlockEntity {
    public static Map<ResourceKey<Level>, Map<BlockPos, Vec3>> gateLocationMap = new HashMap<>();
    public NoosphereGateEntity(BlockPos pos, BlockState state) {
        super(OneironautBlockRegistry.NOOSPHERE_GATE_ENTITY.get(), pos, state);
        //Oneironautfinal.LOGGER.info("super Creating blockentity.");
    }
    public void tick(Level world, BlockPos pos, BlockState state){
        //Oneironautfinal.LOGGER.info("Spam.");
        Vec3 doublePos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        if (!world.isClientSide){
            Player possiblePassenger = world.getNearestPlayer(doublePos.x, doublePos.y, doublePos.z, 2.0, false);
            if (possiblePassenger != null){
                MinecraftServer server = possiblePassenger.getServer();
                ServerPlayer player = (ServerPlayer) possiblePassenger;
                //Vec3d passengerMidpoint = new Vec3d(possiblePassenger.getX(), possiblePassenger.getY() + (possiblePassenger.getHeight() / 2), possiblePassenger.getZ());
                if (player.getBoundingBox().intersects(new AABB(pos))){
                    //possiblePassenger.sendMessage(Text.of("teleporting"));
                    ServerLevel noosphere = null;
                    ServerLevel origin = (ServerLevel) player.level();
                    ServerLevel current = origin;
                    //Oneironautfinal.LOGGER.info("Current: " + current.getRegistryKey().getValue().toString());
                    if (!(current.dimension().location().toString().equals("oneironaut:noosphere"))){
                        //Oneironautfinal.LOGGER.info("Iterating.");
                        for (ServerLevel serverWorld : server.getAllLevels()) {
                            current = serverWorld;
                            //Oneironautfinal.LOGGER.info("Current: " + current.getRegistryKey().getValue().toString());
                            if (current.dimension().location().toString().equals("oneironaut:noosphere")) {
                                noosphere = current;
                                //Oneironautfinal.LOGGER.info("Noosphere REALLY shouldn't be null.");
                                break;
                            }
                        }
                    } else {
                        noosphere = current;
                    }
                    /*if (noosphere != null){
                        Oneironautfinal.LOGGER.info("Noosphere: " + noosphere.getRegistryKey().getValue().toString());
                    } else {
                        Oneironautfinal.LOGGER.info("Noosphere is null.");
                    }*/
                    //Oneironautfinal.LOGGER.info("Noosphere: " + noosphere.toString());
                    double compressionFactor;
                    if (noosphere != null){
                        if (noosphere == origin){
                            ServerLevel homeDim = server.getLevel(player.getRespawnDimension());
                            boolean antiSoftlock = false;
                            if (homeDim == null || homeDim == noosphere){
                                homeDim = server.overworld();
                                antiSoftlock = true;
                            }
                            compressionFactor = 1 / homeDim.dimensionType().coordinateScale();
                            BlockPos spawnpoint = player.getRespawnPosition();
                            if (spawnpoint == null || antiSoftlock){
                                spawnpoint = homeDim.getSharedSpawnPos();
                                //Oneironautfinal.LOGGER.info("Spawnpoint was null, using world spawn");
                            }
                            player.teleportTo(homeDim, spawnpoint.getX() + 0.5, spawnpoint.getY(), spawnpoint.getZ() + 0.5, player.getYRot(), player.getXRot());
                            player.onUpdateAbilities();
                        } else {
                            //iterate to find the ground
                            double altitude = 321;
                            while (noosphere.getBlockState(new BlockPos(pos.getX(), (int) altitude, pos.getZ())).isAir()){
                                altitude -= 1;
                                //don't go into the void
                                if (altitude < -64){
                                    altitude = 321;
                                    break;
                                }
                            }
                            compressionFactor = origin.dimensionType().coordinateScale();
                            Vec3 destPos = new Vec3(floor(pos.getX() * compressionFactor) + 0.5, altitude + 1, floor(pos.getZ() * compressionFactor) + 0.5);
                            WorldBorder border = noosphere.getWorldBorder();
                            //make sure you don't end up outside the world border
                            if (destPos.x > border.getMaxX()){
                                destPos = new Vec3((border.getMaxX() - 2), destPos.y, destPos.z);
                            } else if (destPos.x < border.getMinX()){
                                destPos = new Vec3((border.getMinX() + 2), destPos.y, destPos.z);
                            }
                            if (destPos.z > border.getMaxZ()){
                                destPos = new Vec3(destPos.x, destPos.y, (border.getMaxZ() - 2));
                            } else if (destPos.z < border.getMinZ()){
                                destPos = new Vec3(destPos.x, destPos.y, (border.getMinZ() + 2));
                            }
                            Vec3i destPosI = MiscAPIKt.toVec3i(destPos);
                            if (noosphere.getBlockState(new BlockPos(destPosI).below()).getBlock().equals(OneironautBlockRegistry.NOOSPHERE_GATE.get())){
                                //Oneironautfinal.LOGGER.info("found a portal at the destination OwO " + new BlockPos(destPos).down().toString());
                                if (!(noosphere.getBlockState(new BlockPos(destPosI).east().below()).getBlock().equals(OneironautBlockRegistry.NOOSPHERE_GATE.get()))){
                                    //Oneironautfinal.LOGGER.info("found a portal at the east OwO " + new BlockPos(destPos).down().east().toString());
                                    destPos = destPos.add(1, 0, 0);
                                } else if (!(noosphere.getBlockState(new BlockPos(destPosI).west().below()).getBlock().equals(OneironautBlockRegistry.NOOSPHERE_GATE.get()))){
                                    //Oneironautfinal.LOGGER.info("found a portal at the west OwO " + new BlockPos(destPos).down().west().toString());
                                    destPos = destPos.add(-1, 0, 0);
                                } else if (!(noosphere.getBlockState(new BlockPos(destPosI).south().below()).getBlock().equals(OneironautBlockRegistry.NOOSPHERE_GATE.get()))){
                                    //Oneironautfinal.LOGGER.info("found a portal at the south OwO " + new BlockPos(destPos).down().south().toString());
                                    destPos = destPos.add(0, 0, 1);
                                } else if (!(noosphere.getBlockState(new BlockPos(destPosI).north().below()).getBlock().equals(OneironautBlockRegistry.NOOSPHERE_GATE.get()))){
                                    //Oneironautfinal.LOGGER.info("found a portal at the north OwO " + new BlockPos(destPos).down().north().toString());
                                    destPos = destPos.add(0, 0, -1);
                                }
                            }/* else {
                                Oneironautfinal.LOGGER.info("Couldn't find an orthogonally adjacent spot without a portal. >:(");
                            }*/
                            //Oneironautfinal.LOGGER.info("Teleporting to " + new BlockPos(destPos).toString());
                            player.teleportTo(noosphere, destPos.x, destPos.y, destPos.z, player.getYRot(), player.getXRot());
                            player.onUpdateAbilities();
                            if (altitude >= 321){
                                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 1200));
                            }
                        }
                    }
                }
            }
            //maintain wisps
            ResourceKey<Level> worldKey = world.dimension();
            //don't do that if the current world is the noosphere, because the wisp will never check the map if it's in the noosphere and it'd be pointless lag
            if (!(gateLocationMap.containsKey(worldKey)) && !worldKey.location().toString().equals("oneironaut:noosphere")){
                Map<BlockPos, Vec3> newMap = new HashMap<BlockPos, Vec3>();
                newMap.put(pos, new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
                gateLocationMap.put(worldKey, newMap);
            } else if (gateLocationMap.containsKey(worldKey)){
                Map<BlockPos, Vec3> existingMap = gateLocationMap.get(worldKey);
                if (!(existingMap.containsKey(pos))){
                    existingMap.put(pos, new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
                }
            }
        } else {
            //purple slipway thing
            RandomSource rand = world.random;
            int i = 0;
            while (i < 8){
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
                if(!world.dimension().location().toString().equals("oneironaut:noosphere")){
                    double particlePosX = doublePos.x + gaussX * rand.nextDouble();
                    double particlePosY = doublePos.y + gaussY * rand.nextDouble();
                    double particlePosZ = doublePos.z + gaussZ * rand.nextDouble();
                    double particleVelX = (signum(particlePosX - doublePos.x) * speedMultiplier) * rand.nextDouble();
                    double particleVelY = (signum(particlePosY - doublePos.y) * speedMultiplier) * rand.nextDouble();
                    double particleVelZ = (signum(particlePosZ - doublePos.z) * speedMultiplier) * rand.nextDouble();
                    world.addParticle(
                            new ConjureParticleOptions(0x6a31d2),
                            particlePosX, particlePosY, particlePosZ,
                            particleVelX, particleVelY, particleVelZ
                    );
                } else {
                    double particlePosX = doublePos.x + gaussX * (rand.nextDouble() + 1); /*(((rand.nextInt(9) + 1) - 5) / noosphereBoxDivisor);*/
                    double particlePosY = doublePos.y + gaussY * (rand.nextDouble() + 1);
                    double particlePosZ = doublePos.z + gaussZ * (rand.nextDouble() + 1);
                    double particleVelX = (signum(particlePosX - doublePos.x) * speedMultiplier)/* * rand.nextDouble()*/;
                    double particleVelY = (signum(particlePosY - doublePos.y) * speedMultiplier)/* * rand.nextDouble()*/;
                    double particleVelZ = (signum(particlePosZ - doublePos.z) * speedMultiplier)/* * rand.nextDouble()*/;
                    world.addParticle(
                            new ConjureParticleOptions(0x6a31d2),
                            particlePosX, particlePosY, particlePosZ,
                            particleVelX * -1, particleVelY * -1, particleVelZ * -1
                    );
                }
            }
        }

        //world.setBlockState(pos.add(0, 1, 0), Blocks.ACACIA_FENCE.getDefaultState());
    }
}