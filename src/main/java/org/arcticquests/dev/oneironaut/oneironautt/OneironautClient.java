package org.arcticquests.dev.oneironaut.oneironautt;

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import org.arcticquests.dev.oneironaut.oneironautt.block.InactiveSlipwayBlock;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.ConceptCoreBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.ConceptModifierBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.HoverElevatorBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.WispBatteryEntity;
import org.arcticquests.dev.oneironaut.oneironautt.block.ConceptDecoratorBlock;
import org.arcticquests.dev.oneironaut.oneironautt.block.ConceptModifierBlock;
import org.arcticquests.dev.oneironaut.oneironautt.item.ItemLibraryCard;
import org.arcticquests.dev.oneironaut.oneironautt.item.ReverberationRod;
import org.arcticquests.dev.oneironaut.oneironautt.item.WispCaptureItem;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautItemRegistry;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Forge client entrypoint replacement for the Fabric-style OneironautClient.init().
 *
 * Fabric-only APIs removed/replaced:
 * - BlockRenderLayerMap -> ItemBlockRenderTypes
 * - FluidRenderHandlerRegistry/SimpleFluidRenderHandler -> Forge fluid client extensions (TODO)
 * - ClientTickEvent.CLIENT_POST -> TickEvent.ClientTickEvent on FORGE bus
 * - ClientLifecycleEvent.CLIENT_STARTED -> use client tick / client setup to cache Minecraft instance
 * - DimensionEffectsAccessor -> reflection into DimensionSpecialEffects.EFFECTS (or Mixin accessor)
 * - ItemPropertiesRegistry -> ItemProperties
 */
public final class OneironautClient {
    private OneironautClient() {}

    public static long lastShiftingHoverTick = 0L;
    public static ItemStack lastHoveredShifting = null;

    private static Minecraft cachedClient = null;
    public static Minecraft getCachedClient() {
        return cachedClient;
    }

    private static int applyBlockRenderLayers(Collection<Block> blocks, RenderType layer) {
        int applied = 0;
        for (Block block : blocks) {
            ItemBlockRenderTypes.setRenderLayer(block, layer);
            applied++;
        }
        return applied;
    }

    private static float processObservationPredicate(ItemStack stack, ClientLevel world, LivingEntity holder, int holderID) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer cachedPlayer = client.player;

        final float OFF = 0.99f;
        final float ON = -0.01f;
        float output = ON;

        int fov = client.options.fov().get();
        double threshold = fov / (fov <= 85 ? 90.0 : 100.0);

        if (cachedPlayer != null) {
            if (stack.isFramed() && stack.getFrame() != null) {
                if (MiscAPIKt.vecProximity(stack.getFrame().position().subtract(cachedPlayer.getEyePosition()), cachedPlayer.getLookAngle()) <= threshold) {
                    output = OFF;
                }
            }
            if (stack.getEntityRepresentation() != null && stack.getEntityRepresentation() != cachedPlayer) {
                Vec3 holderCenterApprox = stack.getEntityRepresentation().position().add(stack.getEntityRepresentation().getEyePosition()).scale(0.5);
                if (MiscAPIKt.vecProximity(holderCenterApprox.subtract(cachedPlayer.getEyePosition()), cachedPlayer.getLookAngle()) <= threshold) {
                    output = OFF;
                }
            }
            if (holder == cachedPlayer && (holder.getItemInHand(InteractionHand.MAIN_HAND) == stack || holder.getItemInHand(InteractionHand.OFF_HAND) == stack)) {
                output = OFF;
            }
            if (cachedPlayer.containerMenu.getCarried() == stack ||
                    (lastShiftingHoverTick + 1 >= cachedPlayer.level().getGameTime() && lastHoveredShifting == stack)) {
                output = OFF;
            }
        }

        if (!client.isWindowActive()) output = ON;
        return output;
    }

    @Mod.EventBusSubscriber(modid = Oneironaut.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBus {
        private ModBus() {}

        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                cachedClient = Minecraft.getInstance();

                ScryingLensOverlayRegistry.addDisplayer(
                        OneironautBlockRegistry.WISP_BATTERY.get(),
                        WispBatteryEntity::applyScryingLensOverlay
                );

                List<RegistryShim<ConceptModifierBlock>> conceptModifiers = List.of(
                        new RegistryShim<>(OneironautBlockRegistry.CONCEPT_MODIFIER_GRIDSIZE),
                        new RegistryShim<>(OneironautBlockRegistry.CONCEPT_MODIFIER_ANTIEROSION),
                        new RegistryShim<>(OneironautBlockRegistry.CONCEPT_MODIFIER_MAXHEALTH),
                        new RegistryShim<>(OneironautBlockRegistry.CONCEPT_MODIFIER_GTP_DROP),
                        new RegistryShim<>(OneironautBlockRegistry.CONCEPT_MODIFIER_REFERENCE_COMPARISON),
                        new RegistryShim<>(OneironautBlockRegistry.CONCEPT_MODIFIER_REFERENCE_FALSY),
                        new RegistryShim<>(OneironautBlockRegistry.CONCEPT_MODIFIER_STACK_SIZE)
                );
                for (RegistryShim<ConceptModifierBlock> supplier : conceptModifiers) {
                    ScryingLensOverlayRegistry.addDisplayer(supplier.get(), ConceptModifierBlockEntity::applyScryingLensOverlay);
                }
                ScryingLensOverlayRegistry.addDisplayer(OneironautBlockRegistry.CONCEPT_CORE.get(), ConceptCoreBlockEntity::applyScryingLensOverlay);

                List<Block> cutoutBlocks = new ArrayList<>(List.of(
                        OneironautBlockRegistry.WISP_LANTERN.get(),
                        OneironautBlockRegistry.WISP_LANTERN_TINTED.get(),
                        OneironautBlockRegistry.WISP_BATTERY.get(),
                        OneironautBlockRegistry.WISP_BATTERY_DECORATIVE.get(),
                        OneironautBlockRegistry.CIRCLE.get(),
                        OneironautBlockRegistry.PSEUDOAMETHYST_CLUSTER.get(),
                        OneironautBlockRegistry.PSEUDOAMETHYST_BUD_LARGE.get(),
                        OneironautBlockRegistry.PSEUDOAMETHYST_BUD_MEDIUM.get(),
                        OneironautBlockRegistry.PSEUDOAMETHYST_BUD_SMALL.get(),
                        OneironautBlockRegistry.RENDER_BUSH.get(),
                        OneironautBlockRegistry.DEEP_NOOSPHERE_FLOOR.get(),
                        OneironautBlockRegistry.CONCEPT_MODIFIER_REFERENCE_FALSY.get(),
                        OneironautBlockRegistry.CONCEPT_MODIFIER_GRIDSIZE.get(),
                        OneironautBlockRegistry.CONCEPT_MODIFIER_EMPTY.get(),
                        OneironautBlockRegistry.CONCEPT_MODIFIER_SUS.get(),
                        OneironautBlockRegistry.CONCEPT_MODIFIER_ANTIEROSION.get(),
                        OneironautBlockRegistry.CONCEPT_MODIFIER_MAXHEALTH.get(),
                        OneironautBlockRegistry.CONCEPT_MODIFIER_GTP_DROP.get(),
                        OneironautBlockRegistry.CONCEPT_MODIFIER_REFERENCE_COMPARISON.get(),
                        OneironautBlockRegistry.CONCEPT_MODIFIER_STACK_SIZE.get(),
                        OneironautBlockRegistry.CONCEPT_CONNECTOR.get(),
                        OneironautBlockRegistry.CONCEPT_CORE.get()
                ));

                for (RegistryObject<ConceptDecoratorBlock> ro : OneironautBlockRegistry.COLORFUL_CONCEPT_MODIFIERS.values()) {
                    cutoutBlocks.add(ro.get());
                }

                Block[] translucentBlocks = {
                        OneironautBlockRegistry.RAYCAST_BLOCKER_GLASS.get(),
                        OneironautBlockRegistry.MEDIA_GEL.get(),
                        OneironautBlockRegistry.CELL.get(),
                        OneironautBlockRegistry.INSTANT_BREAKER_RIFTRESIDUE.get(),
                        OneironautBlockRegistry.PSUEDOAMETHYST_BLOCK_INSUBSTANTIAL.get()
                };

                Oneironaut.LOGGER.info("Applied cutout layer to {} blocks", applyBlockRenderLayers(cutoutBlocks, RenderType.cutout()));
                Oneironaut.LOGGER.info("Applied translucent layer to {} blocks", applyBlockRenderLayers(List.of(translucentBlocks), RenderType.translucent()));


                ItemPackagedHex[] castingItems = {
                        OneironautItemRegistry.REVERBERATION_ROD.get(),
                        OneironautItemRegistry.BOTTOMLESS_CASTING_ITEM.get()
                };
                for (ItemPackagedHex item : castingItems) {
                    ItemProperties.register(item, ItemPackagedHex.HAS_PATTERNS_PRED,
                            (stack, world, holder, holderID) -> item.hasHex(stack) ? 0.99f : -0.01f);
                }

                ItemProperties.register(OneironautItemRegistry.REVERBERATION_ROD.get(), ReverberationRod.CASTING_PREDICATE,
                        (stack, world, holder, holderID) -> holder != null && holder.getUseItem().equals(stack) ? 0.99f : -0.01f);

                ItemProperties.register(OneironautItemRegistry.WISP_CAPTURE_ITEM.get(), WispCaptureItem.FILLED_PREDICATE,
                        (stack, world, holder, holderID) -> ((WispCaptureItem) stack.getItem()).hasWisp(stack, world) ? 0.99f : -0.01f);

                ItemProperties.register(
                        OneironautItemRegistry.SHIFTING_PSEUDOAMETHYST.get(),
                        ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "observation"),
                        OneironautClient::processObservationPredicate
                );

                ItemProperties.register(
                        OneironautItemRegistry.LIBRARY_CARD.get(),
                        ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "written"),
                        (stack, world, holder, holderID) -> ((ItemLibraryCard) stack.getItem()).getDimension(stack) != null ? 0.99f : -0.01f
                );

                Item[] nameSensitiveStaves = {
                        OneironautItemRegistry.ECHO_STAFF.get(),
                        OneironautItemRegistry.BEACON_STAFF.get(),
                        OneironautItemRegistry.SPOON_STAFF.get()
                };
                for (Item staff : nameSensitiveStaves) {
                    ItemProperties.register(staff, ItemStaff.FUNNY_LEVEL_PREDICATE, (stack, level, holder, holderID) -> {
                        if (!stack.hasCustomHoverName()) return 0f;
                        var name = stack.getHoverName().getString().toLowerCase(Locale.ROOT);
                        if (name.contains("old")) return 1f;
                        if (name.contains("wand of the forest")) return 2f;
                        return 0f;
                    });
                }
                InactiveSlipwayBlock.init();
                Oneironaut.LOGGER.info("Cached client object. Player: {}", cachedClient.player);
            });
        }
    }

    @Mod.EventBusSubscriber(modid = Oneironaut.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeBus {
        private ForgeBus() {}

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            Minecraft client = Minecraft.getInstance();
            try {
                HoverElevatorBlockEntity.processHover(false, client.level != null ? client.level.getGameTime() : -1L);
            } catch (ConcurrentModificationException exception) {
                Oneironaut.LOGGER.error("Oopsie client-side hoverlift exception {}", exception.getMessage());
            }
        }
    }

    public static boolean isWorldClientNoosphere(Level world) {
        if (world instanceof ClientLevel clientWorld) {
            return clientWorld.effects().getClass() == NoosphereDimensionEffects.class
                    || clientWorld.effects().getClass() == DeepNoosphereDimensionEffects.class;
        }
        return false;
    }

    public static boolean isWorldClientDeepNoosphere(Level world) {
        if (world instanceof ClientLevel clientWorld) {
            return clientWorld.effects().getClass() == DeepNoosphereDimensionEffects.class;
        }
        return false;
    }

    private static final class RegistryShim<T> {
        private final RegistryObject<T> ro;
        private RegistryShim(RegistryObject<T> ro) {
            this.ro = ro;
        }
        private T get() {
            return ro.get();
        }
    }
}