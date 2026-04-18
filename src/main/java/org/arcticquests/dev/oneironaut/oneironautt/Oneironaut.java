package org.arcticquests.dev.oneironaut.oneironautt;

import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexItems;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.arcticquests.dev.oneironaut.oneironautt.block.InactiveSlipwayBlock;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.HoverElevatorBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.casting.DepartureEntry;
import org.arcticquests.dev.oneironaut.oneironautt.casting.DisintegrationProtectionManager;
import org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification.ConceptModifier;
import org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification.ConceptModifierManager;
import org.arcticquests.dev.oneironaut.oneironautt.casting.idea.IdeaInscriptionManager;
import org.arcticquests.dev.oneironaut.oneironautt.item.BottomlessMediaItem;
import org.arcticquests.dev.oneironaut.oneironautt.network.OneironautNetwork;
import org.arcticquests.dev.oneironaut.oneironautt.recipe.OneironautRecipesForge;
import org.arcticquests.dev.oneironaut.oneironautt.registry.*;
import org.arcticquests.dev.oneironaut.oneironautt.status.MediaDisintegrationEffect;
import org.slf4j.Logger;
import ram.talia.hexal.common.entities.WanderingWisp;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Consumer;

import static org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt.stringToWorld;

@Mod(Oneironaut.MODID)
public class Oneironaut {
    public static final String MODID = "oneironaut";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final List<Item> randomWispPigments = new ArrayList<>();
    private static ServerLevel noosphere = null;
    private static ServerLevel deepNoosphere = null;
    private static MinecraftServer server = null;

    public static final Set<Tuple<LivingEntity, MobEffectInstance>> reapplicationSet = new HashSet<>();

    public Oneironaut() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        OneironautBlockRegistry.init(modEventBus);
        OneironautItemRegistry.init(modEventBus);
        OneironautFluidTypes.init(modEventBus);
        OneironautMiscRegistry.init(modEventBus);

        OneironautRecipesForge.init(modEventBus);

        OneironautFeatureRegistry.init(modEventBus);

        // Register the commonSetup method for modloading (keep for other setup work)
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());

        LOGGER.info("why do they call it oven when you of in the cold food of out hot eat the food");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(OneironautNetwork::register);
        event.enqueueWork(() -> {

            OneironautIotaTypeRegistry.init();
            OneironautPatternRegistry.init();
            ModLoadingContext.get().registerConfig(
                    ModConfig.Type.SERVER,
                    OneironautForgeConfig.INSTANCE.getSERVER_SPEC()
            );
            OneironautConfigForgeBridge.bind();


            // OneironautRecipeSerializer.registerSerializers(OneironautRecipeTypes.Companion.bind(BuiltInRegistries.RECIPE_SERIALIZER));
            // OneironautRecipeTypes.registerTypes(OneironautRecipeTypes.Companion.bind(BuiltInRegistries.RECIPE_TYPE));
        });
    }

    public static class ForgeEventHandler {

        @SubscribeEvent
        public void onServerStarting(ServerStartingEvent event) {
            server = event.getServer();
            noosphere = stringToWorld("oneironaut:noosphere", server);
            deepNoosphere = stringToWorld("oneironaut:deep_noosphere", server);

            if (OneironautMiscRegistry.DISINTEGRATION.get().getAttributeModifiers().isEmpty()) {
                OneironautMiscRegistry.DISINTEGRATION.get().addAttributeModifier(
                        Attributes.MAX_HEALTH,
                        MediaDisintegrationEffect.ATTRIBUTE_UUID_STRING,
                        -1.0,
                        AttributeModifier.Operation.ADDITION
                );
            }

            IdeaInscriptionManager ideaState = IdeaInscriptionManager.getServerState(server);
            IdeaInscriptionManager.cleanMap(server, ideaState);
            ideaState.setDirty();

            DisintegrationProtectionManager disintegrationState = DisintegrationProtectionManager.getServerState(server);
            disintegrationState.cleanEntries();
            disintegrationState.setDirty();

            ConceptModifierManager conceptState = ConceptModifierManager.getServerState(server);
            conceptState.verifyModifiers();
            conceptState.setDirty();

            randomWispPigments.addAll(HexItems.DYE_PIGMENTS.values());
            randomWispPigments.addAll(HexItems.PRIDE_PIGMENTS.values());
            randomWispPigments.add(HexItems.DEFAULT_PIGMENT);
            randomWispPigments.add(HexItems.UUID_PIGMENT);
            randomWispPigments.add(OneironautItemRegistry.PIGMENT_NOOSPHERE.get());
            randomWispPigments.add(OneironautItemRegistry.PIGMENT_FLAME.get());
            OneironautCastEnvComponents.init();
            InactiveSlipwayBlock.init();
        }

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                if (server != null) {
                    BottomlessMediaItem.time = server.overworld().getGameTime();
                }
            } else if (event.phase == TickEvent.Phase.END) {
                if (server != null) {
                    try {
                        HoverElevatorBlockEntity.processHover(true, server.overworld().getGameTime());
                    } catch (ConcurrentModificationException exception) {
                        LOGGER.error("Oopsie server-side hoverlift exception {}", exception.getMessage());
                    }

                    DepartureEntry.clearMap();

                    if (noosphere != null) {
                        ServerPlayer noospherePlayer = noosphere.getRandomPlayer();
                        RandomSource rand = noosphere.random;
                        if (noospherePlayer != null && rand.nextInt(1024) == 0) {
                            double gaussDistance = 16.0;
                            WanderingWisp wisp = new WanderingWisp(noosphere, noospherePlayer.position().add(
                                    rand.nextGaussian() * gaussDistance,
                                    rand.nextGaussian() * gaussDistance,
                                    rand.nextGaussian() * gaussDistance));
                            ItemStack stack = randomWispPigments.get(rand.nextInt(randomWispPigments.size())).getDefaultInstance();
                            wisp.setPigment(new FrozenPigment(stack, wisp.getUUID()));
                            noosphere.addFreshEntity(wisp);
                        }
                    }

                    for (Tuple<LivingEntity, MobEffectInstance> pair : reapplicationSet) {
                        if (!pair.getA().hasEffect(pair.getB().getEffect())) {
                            pair.getA().addEffect(pair.getB());
                        }
                    }
                    reapplicationSet.clear();
                }
            }
        }

        @SubscribeEvent
        public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                ConceptModifierManager conceptModifierManager = ConceptModifierManager.getServerState(player.server);
                for (ConceptModifier modifier : conceptModifierManager.getAllModifiers(player)) {
                    modifier.onApply(player);
                }
            }
        }

        @SubscribeEvent
        public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                ConceptModifierManager conceptModifierManager = ConceptModifierManager.getServerState(player.server);
                for (ConceptModifier modifier : conceptModifierManager.getAllModifiers(player)) {
                    modifier.onApply(player);
                }
            }
        }

        @SubscribeEvent
        public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
            ItemStack heldStack = event.getItemStack();
            if (heldStack.is(OneironautTags.Items.datapackStaves) && !(heldStack.getItem() instanceof ItemStaff)) {
                if (heldStack.is(HexTags.Items.STAVES)) {
                    ItemStack fakeStaffStack = HexItems.STAFF_OAK.getDefaultInstance();
                    fakeStaffStack.use(event.getLevel(), event.getEntity(), event.getHand());
                    event.getEntity().swing(event.getHand());
                } else {
                    LOGGER.info(event.getEntity().getName().getString() +
                            " has right-clicked an item tagged as a datapacked staff, but that item does not have the normal staff tag, which is necessary for the datapack staff functionality to work.");
                }
            }
        }
    }

    public enum Loggers {
        INFO,
        DEBUG,
        WARN,
        ERROR,
        FATAL,
        TRACE
    }

    public static void boolLogger(String str, boolean bool) {
        boolLogger(str, bool, Loggers.INFO);
    }

    public static void boolLogger(String str, boolean bool, Loggers logType) {
        if (bool) {
            Consumer<String> logger = switch (logType) {
                case DEBUG -> LOGGER::debug;
                case WARN -> LOGGER::warn;
                case ERROR -> LOGGER::error;
                case FATAL -> (s) -> LOGGER.error("[FATAL] " + s);
                case TRACE -> LOGGER::trace;
                default -> LOGGER::info;
            };
            logger.accept(str);
        }
    }

    public static ResourceLocation id(String string) {
        return ResourceLocation.fromNamespaceAndPath(MODID, string);
    }

    public static ServerLevel getNoosphere() {
        if (noosphere == null) {
            throw new IllegalStateException("getNoosphere method called before server start");
        }
        return noosphere;
    }

    public static ServerLevel getDeepNoosphere() {
        if (deepNoosphere == null) {
            throw new IllegalStateException("getDeepNoosphere method called before server start");
        }
        return deepNoosphere;
    }

    public static boolean isWorldNoosphere(Level world) {
        try {
            if (world != null) {
                if (world instanceof ServerLevel serverWorld) {
                    return serverWorld == noosphere || serverWorld == deepNoosphere;
                } else if (world.isClientSide) {
                    return OneironautClient.isWorldClientNoosphere(world);
                }
            }
        } catch (Exception e) {
            // just let it return false
        }
        return false;
    }


    public static MinecraftServer getCachedServer() {
        if (server == null) {
            throw new IllegalStateException("getCachedServer method called before server start. or on client. or something else, idfk");
        }
        return server;
    }

    public static boolean isServerThread() {
        if (server != null) {
            return Thread.currentThread() == server.getRunningThread();
        }
        return false;
    }

    public static void processDisintegration(LivingEntity entity) {
        if (!entity.hasEffect(OneironautMiscRegistry.DISINTEGRATION_PROTECTION.get())) {
            if (!entity.hasEffect(OneironautMiscRegistry.DISINTEGRATION.get())) {
                entity.addEffect(new MobEffectInstance(OneironautMiscRegistry.DISINTEGRATION.get(), 100, 0, true, true));
            } else {
                MobEffectInstance instance = entity.getEffect(OneironautMiscRegistry.DISINTEGRATION.get());
                if (instance != null && instance.getDuration() <= 40) {
                    instance.duration += 90;
                }
            }
        }
    }
}