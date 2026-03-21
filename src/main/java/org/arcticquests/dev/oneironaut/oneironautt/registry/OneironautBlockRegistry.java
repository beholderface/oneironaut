package org.arcticquests.dev.oneironaut.oneironautt.registry;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.common.blocks.circles.BlockSlate;
import at.petrak.hexcasting.common.lib.HexAttributes;
import at.petrak.hexcasting.common.lib.HexBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.block.*;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.*;
import org.arcticquests.dev.oneironaut.oneironautt.casting.conceptmodification.ConceptModifier;
import ram.talia.hexal.common.lib.HexalBlocks;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public final class OneironautBlockRegistry {
    private OneironautBlockRegistry() {}

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Oneironaut.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Oneironaut.MODID);

    public static void init(IEventBus modBus) {
        BLOCKS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
    }

    public static final RegistryObject<Block> PSUEDOAMETHYST_BLOCK = BLOCKS.register("pseudoamethyst_block", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)
                    .destroyTime(1.5f)
                    .sound(SoundType.AMETHYST)
                    .explosionResistance(5)
                    .lightLevel(state -> 7)));

    public static final RegistryObject<Block> NOOSPHERE_BASALT = BLOCKS.register("noosphere_basalt", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.BASALT)
                    .destroyTime(1f)
                    .sound(SoundType.BASALT)
                    .explosionResistance(4)));

    public static final RegistryObject<Block> PSUEDOAMETHYST_BLOCK_INSUBSTANTIAL = BLOCKS.register("insubstantial_pseudoamethyst_block", () ->
            new Block(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)
                    .destroyTime(1f)
                    .sound(SoundType.AMETHYST)
                    .explosionResistance(4)
                    .lightLevel(state -> 5)
                    .noOcclusion()));

    public static final RegistryObject<NoosphereGateway> NOOSPHERE_GATE = BLOCKS.register("noosphere_gate", () ->
            new NoosphereGateway(BlockBehaviour.Properties.copy(Blocks.END_PORTAL)
                    .lightLevel(state -> 15)
                    .noCollission()
                    .destroyTime(-1)));

    public static final RegistryObject<BlockEntityType<NoosphereGateEntity>> NOOSPHERE_GATE_ENTITY =
            BLOCK_ENTITIES.register("noosphere_gate_entity", () ->
                    BlockEntityType.Builder.of(NoosphereGateEntity::new, NOOSPHERE_GATE.get()).build(null));

    public static final RegistryObject<WispLantern> WISP_LANTERN = BLOCKS.register("wisp_lantern", () ->
            new WispLantern(BlockBehaviour.Properties.copy(Blocks.GLASS)
                    .lightLevel(state -> 15)
                    .sound(SoundType.GLASS)));

    public static final RegistryObject<WispLanternTinted> WISP_LANTERN_TINTED = BLOCKS.register("wisp_lantern_tinted", () ->
            new WispLanternTinted(BlockBehaviour.Properties.copy(Blocks.GLASS)
                    .sound(SoundType.GLASS)));

    public static final RegistryObject<BlockEntityType<WispLanternEntity>> WISP_LANTERN_ENTITY =
            BLOCK_ENTITIES.register("wisp_lantern_entity", () ->
                    BlockEntityType.Builder.of(WispLanternEntity::new, WISP_LANTERN.get()).build(null));

    public static final RegistryObject<BlockEntityType<WispLanternEntityTinted>> WISP_LANTERN_ENTITY_TINTED =
            BLOCK_ENTITIES.register("wisp_lantern_entity_tinted", () ->
                    BlockEntityType.Builder.of(WispLanternEntityTinted::new, WISP_LANTERN_TINTED.get()).build(null));


    public static final RegistryObject<ThoughtSlurryBlock> THOUGHT_SLURRY_BLOCK = BLOCKS.register("thought_slurry",
            () -> new ThoughtSlurryBlock(
                    OneironautMiscRegistry.THOUGHT_SLURRY,
                   BlockBehaviour.Properties
                            .copy(Blocks.WATER)
                            .noOcclusion()
                            .mapColor(MapColor.COLOR_PURPLE)
            )
    );

    public static final RegistryObject<SuperBuddingBlock> SUPER_BUDDING =
            BLOCKS.register("super_budding", () -> new SuperBuddingBlock(BlockBehaviour.Properties.copy(Blocks.BUDDING_AMETHYST)));

    public static final RegistryObject<SentinelTrapImpetus> SENTINEL_TRAP =
            BLOCKS.register("sentinel_trap", () -> new SentinelTrapImpetus(BlockBehaviour.Properties.copy(HexBlocks.SLATE_BLOCK).destroyTime(2f)));

    public static final RegistryObject<BlockEntityType<SentinelTrapImpetusEntity>> SENTINEL_TRAP_ENTITY =
            BLOCK_ENTITIES.register("sentinel_trap_entity", () ->
                    BlockEntityType.Builder.of(SentinelTrapImpetusEntity::new, SENTINEL_TRAP.get()).build(null));

    public static final RegistryObject<SentinelSensor> SENTINEL_SENSOR =
            BLOCKS.register("sentinel_sensor", () -> new SentinelSensor(BlockBehaviour.Properties.copy(HexBlocks.SLATE_BLOCK)));

    public static final RegistryObject<BlockEntityType<SentinelSensorEntity>> SENTINEL_SENSOR_ENTITY =
            BLOCK_ENTITIES.register("sentinel_sensor_entity", () ->
                    BlockEntityType.Builder.of(SentinelSensorEntity::new, SENTINEL_SENSOR.get()).build(null));

    public static final RegistryObject<Block> RAYCAST_BLOCKER =
            BLOCKS.register("raycast_blocker", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)));

    public static final RegistryObject<Block> RAYCAST_BLOCKER_GLASS =
            BLOCKS.register("raycast_blocker_glass", () -> new RaycastBlockerGlass(BlockBehaviour.Properties.copy(Blocks.TINTED_GLASS)));

    public static final RegistryObject<Block> HEX_RESISTANT_BLOCK =
            BLOCKS.register("hex_resistant_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN).destroyTime(1.5f)));

    public static final RegistryObject<Block> CIRCLE =
            BLOCKS.register("circle", () -> new CircleBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_CONCRETE).noOcclusion().instabreak()));

    public static final RegistryObject<Block> MEDIA_ICE =
            BLOCKS.register("media_ice", () -> new Block(BlockBehaviour.Properties.copy(Blocks.PACKED_ICE).friction(1.1f).mapColor(MapColor.ICE)));

    public static final RegistryObject<Block> MEDIA_ICE_FROSTED =
            BLOCKS.register("media_ice_frosted", () -> new FrostedMediaIceBlock(BlockBehaviour.Properties.copy(Blocks.PACKED_ICE)
                    .friction(1.08f).mapColor(MapColor.ICE).randomTicks().strength(0.5f).sound(SoundType.GLASS)));

    public static final RegistryObject<MediaGelBlock> MEDIA_GEL =
            BLOCKS.register("media_gel", () -> new MediaGelBlock(BlockBehaviour.Properties.copy(Blocks.SLIME_BLOCK)
                    .speedFactor(0.05f).jumpFactor(0.25f).mapColor(MapColor.ICE).sound(SoundType.SLIME_BLOCK).noOcclusion()
                    .destroyTime(Blocks.SOUL_SAND.defaultDestroyTime())));

    public static final RegistryObject<CellBlock> CELL =
            BLOCKS.register("cell", () -> new CellBlock(BlockBehaviour.Properties.copy(Blocks.SLIME_BLOCK)
                    .speedFactor(0.6f).jumpFactor(0.75f).mapColor(MapColor.ICE).sound(SoundType.SLIME_BLOCK).noOcclusion()
                    .destroyTime(Blocks.SOUL_SAND.defaultDestroyTime())));

    public static final RegistryObject<BlockEntityType<CellEntity>> CELL_ENTITY =
            BLOCK_ENTITIES.register("cell_entity", () ->
                    BlockEntityType.Builder.of(CellEntity::new, CELL.get()).build(null));

    // --- Wisp battery ---
    public static final RegistryObject<WispBattery> WISP_BATTERY =
            BLOCKS.register("wisp_battery", () ->
                    new WispBattery(BlockBehaviour.Properties.copy(HexBlocks.SLATE_BLOCK)
                            .lightLevel(createLightLevelFromBoolBlockState(WispBattery.REDSTONE_POWERED, 15))));

    public static final RegistryObject<BlockEntityType<WispBatteryEntity>> WISP_BATTERY_ENTITY =
            BLOCK_ENTITIES.register("wisp_battery_entity", () ->
                    BlockEntityType.Builder.of(WispBatteryEntity::new, WISP_BATTERY.get()).build(null));

    public static final RegistryObject<WispBatteryFake> WISP_BATTERY_DECORATIVE =
            BLOCKS.register("decorative_wisp_battery", () ->
                    new WispBatteryFake(BlockBehaviour.Properties.copy(HexBlocks.SLATE_BLOCK)
                            .lightLevel(createLightLevelFromBoolBlockState(WispBatteryFake.REDSTONE_POWERED, 15))));

    public static final RegistryObject<BlockEntityType<WispBatteryEntityFake>> WISP_BATTERY_ENTITY_DECORATIVE =
            BLOCK_ENTITIES.register("decorative_wisp_battery_entity", () ->
                    BlockEntityType.Builder.of(WispBatteryEntityFake::new, WISP_BATTERY_DECORATIVE.get()).build(null));

    public static final RegistryObject<EdifiedTreeSpawnerBlock> EDIFIED_TREE_SPAWNER =
            BLOCKS.register("edified_tree_spawner", () -> new EdifiedTreeSpawnerBlock(BlockBehaviour.Properties.copy(Blocks.AIR)));

    public static final RegistryObject<BlockEntityType<EdifiedTreeSpawnerBlockEntity>> EDIFIED_TREE_SPAWNER_ENTITY =
            BLOCK_ENTITIES.register("edified_tree_spawner_entity", () ->
                    BlockEntityType.Builder.of(EdifiedTreeSpawnerBlockEntity::new, EDIFIED_TREE_SPAWNER.get()).build(null));

    public static final RegistryObject<HoverElevatorBlock> HOVER_ELEVATOR =
            BLOCKS.register("hover_elevator", () ->
                    new HoverElevatorBlock(BlockBehaviour.Properties.copy(HexBlocks.SLATE_BLOCK)
                            .lightLevel(createLightLevelFromBoolBlockState(HoverElevatorBlock.POWERED, 15))));

    public static final RegistryObject<BlockEntityType<HoverElevatorBlockEntity>> HOVER_ELEVATOR_ENTITY =
            BLOCK_ENTITIES.register("hover_elevator_entity", () ->
                    BlockEntityType.Builder.of(HoverElevatorBlockEntity::new, HOVER_ELEVATOR.get()).build(null));

    public static final RegistryObject<Block> HOVER_REPEATER =
            BLOCKS.register("hover_repeater", () ->
                    new HoverRepeaterBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DEEPSLATE)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.DEEPSLATE)
                            .instabreak()
                            .noOcclusion()
                            .noCollission()));

    public static final RegistryObject<AmethystClusterBlock> PSEUDOAMETHYST_CLUSTER =
            BLOCKS.register("pseudoamethyst_cluster", () -> new AmethystClusterBlock(7, 3, BlockBehaviour.Properties.copy(Blocks.AMETHYST_CLUSTER)));

    public static final RegistryObject<AmethystClusterBlock> PSEUDOAMETHYST_BUD_LARGE =
            BLOCKS.register("pseudoamethyst_bud_large", () -> new AmethystClusterBlock(5, 3, BlockBehaviour.Properties.copy(Blocks.LARGE_AMETHYST_BUD)));

    public static final RegistryObject<AmethystClusterBlock> PSEUDOAMETHYST_BUD_MEDIUM =
            BLOCKS.register("pseudoamethyst_bud_medium", () -> new AmethystClusterBlock(4, 3, BlockBehaviour.Properties.copy(Blocks.MEDIUM_AMETHYST_BUD)));

    public static final RegistryObject<AmethystClusterBlock> PSEUDOAMETHYST_BUD_SMALL =
            BLOCKS.register("pseudoamethyst_bud_small", () -> new AmethystClusterBlock(3, 4, BlockBehaviour.Properties.copy(Blocks.SMALL_AMETHYST_BUD)));

    public static final RegistryObject<SpaceBombBlock> SPACE_BOMB =
            BLOCKS.register("spacebomb", () ->
                    new SpaceBombBlock(BlockBehaviour.Properties.copy(Blocks.RESPAWN_ANCHOR)
                            .lightLevel(createLightLevelFromBoolBlockState(BlockSlate.ENERGIZED, 13))));

    public static final RegistryObject<BlockEntityType<SpaceBombBlockEntity>> SPACE_BOMB_ENTITY =
            BLOCK_ENTITIES.register("spacebomb_entity", () ->
                    BlockEntityType.Builder.of(SpaceBombBlockEntity::new, SPACE_BOMB.get()).build(null));

    public static final RegistryObject<InactiveSlipwayBlock> INACTIVE_SLIPWAY =
            BLOCKS.register("inactiveslipway", () -> new InactiveSlipwayBlock(BlockBehaviour.Properties.copy(HexalBlocks.SLIPWAY)));

    public static final RegistryObject<SlipwaySuppressorBlock> SLIPWAY_SUPPRESSOR =
            BLOCKS.register("slipwaysuppressor", () -> new SlipwaySuppressorBlock(BlockBehaviour.Properties.copy(HexBlocks.SLATE_BLOCK)));

    public static final RegistryObject<RenderBerryBushBlock> RENDER_BUSH =
            BLOCKS.register("monkfruit_bush", () -> new RenderBerryBushBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)));

    public static final RegistryObject<ExtradimensionalBoundaryLocus> EXTRADIM_LOCUS =
            BLOCKS.register("extradimensional_border", () ->
                    new ExtradimensionalBoundaryLocus(BlockBehaviour.Properties.copy(HexBlocks.SLATE_BLOCK)
                            .lightLevel(createLightLevelFromBoolBlockState(BlockCircleComponent.ENERGIZED, 14))));

    public static final RegistryObject<DeepNoosphereFloorBlock> DEEP_NOOSPHERE_FLOOR =
            BLOCKS.register("deep_border", () ->
                    new DeepNoosphereFloorBlock(BlockBehaviour.Properties.copy(Blocks.BEDROCK).noParticlesOnBreak().mapColor(MapColor.ICE)));

    public static final RegistryObject<ConceptCoreBlock> CONCEPT_CORE =
            BLOCKS.register("concept_core", () -> new ConceptCoreBlock(BlockBehaviour.Properties.copy(HexBlocks.SLATE_BLOCK)));

    public static final RegistryObject<BlockEntityType<ConceptCoreBlockEntity>> CONCEPT_CORE_ENTITY =
            BLOCK_ENTITIES.register("concept_core_entity", () ->
                    BlockEntityType.Builder.of(ConceptCoreBlockEntity::new, CONCEPT_CORE.get()).build(null));

    public static final RegistryObject<ConceptConnectorBlock> CONCEPT_CONNECTOR =
            BLOCKS.register("concept_connector", () -> new ConceptConnectorBlock(BlockBehaviour.Properties.copy(HexBlocks.SLATE_BLOCK)));

    public static final Supplier<BlockBehaviour.Properties> CONCEPT_MODIFIER_SETTINGS =
            () -> BlockBehaviour.Properties.copy(HexBlocks.SLATE_BLOCK).lightLevel(state -> 15);

    public static final RegistryObject<ConceptDecoratorBlock> CONCEPT_MODIFIER_EMPTY =
            BLOCKS.register("concept_modifier_empty", () -> new ConceptDecoratorBlock(CONCEPT_MODIFIER_SETTINGS.get()));

    public static final RegistryObject<ConceptDecoratorBlock> CONCEPT_MODIFIER_SUS =
            BLOCKS.register("concept_modifier_sus", () -> new ConceptDecoratorBlock(CONCEPT_MODIFIER_SETTINGS.get()));

    public static final Map<DyeColor, RegistryObject<ConceptDecoratorBlock>> COLORFUL_CONCEPT_MODIFIERS = new EnumMap<>(DyeColor.class);
    static {
        for (DyeColor color : DyeColor.values()) {
            String name = "concept_decorator_color_" + color.getName();
            RegistryObject<ConceptDecoratorBlock> ro =
                    BLOCKS.register(name, () -> new ConceptDecoratorBlock(CONCEPT_MODIFIER_SETTINGS.get()));
            COLORFUL_CONCEPT_MODIFIERS.put(color, ro);
        }
    }

    private static final Function<CompoundTag, Double> ATTRIBUTE_CONCEPT_CALCULATOR = (nbt) -> {
        double potency = nbt.getDouble("potency");
        if (potency <= 1) {
            potency = (1 - potency) + 1;
            return -Math.pow(potency * 5.0, 2.0);
        } else {
            return Math.pow(potency * 10.0, 2.0);
        }
    };

    public static final RegistryObject<ConceptModifierBlock> CONCEPT_MODIFIER_GRIDSIZE =
            BLOCKS.register("concept_modifier_gridsize", () ->
                    new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.ATTRIBUTE, HexAttributes.GRID_ZOOM, ATTRIBUTE_CONCEPT_CALCULATOR));

    public static final RegistryObject<ConceptModifierBlock> CONCEPT_MODIFIER_MAXHEALTH =
            BLOCKS.register("concept_modifier_maxhealth", () ->
                    new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.ATTRIBUTE, Attributes.MAX_HEALTH, ATTRIBUTE_CONCEPT_CALCULATOR));

    public static final RegistryObject<ConceptModifierBlock> CONCEPT_MODIFIER_ANTIEROSION =
            BLOCKS.register("concept_modifier_antierosion", () ->
                    new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.ANTIEROSION, nbt -> 10000.0));

    public static final RegistryObject<ConceptModifierBlock> CONCEPT_MODIFIER_REFERENCE_FALSY =
            BLOCKS.register("concept_modifier_falsy", () ->
                    new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.FALSY_REFERENCE, nbt -> 1000.0));

    public static final RegistryObject<ConceptModifierBlock> CONCEPT_MODIFIER_REFERENCE_COMPARISON =
            BLOCKS.register("concept_modifier_comparison", () ->
                    new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.REFERENCE_COMPARISON, nbt -> 1000.0));

    public static final RegistryObject<ConceptModifierBlock> CONCEPT_MODIFIER_GTP_DROP =
            BLOCKS.register("concept_modifier_gtp_drop", () ->
                    new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.GTP_DROPREDUCTION, nbt -> 1000.0));

    public static final RegistryObject<ConceptModifierBlock> CONCEPT_MODIFIER_STACK_SIZE =
            BLOCKS.register("concept_modifier_stack_size", () ->
                    new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.STACK_LIMIT, nbt -> 1000.0));

    public static final RegistryObject<BlockEntityType<ConceptModifierBlockEntity>> CONCEPT_MODIFIER_ENTITY =
            BLOCK_ENTITIES.register("concept_modifier_entity", () -> BlockEntityType.Builder.of(
                    ConceptModifierBlockEntity::new,
                    CONCEPT_MODIFIER_GRIDSIZE.get(),
                    CONCEPT_MODIFIER_MAXHEALTH.get(),
                    CONCEPT_MODIFIER_GTP_DROP.get(),
                    CONCEPT_MODIFIER_ANTIEROSION.get(),
                    CONCEPT_MODIFIER_REFERENCE_FALSY.get(),
                    CONCEPT_MODIFIER_REFERENCE_COMPARISON.get(),
                    CONCEPT_MODIFIER_STACK_SIZE.get()
            ).build(null));

    public static final RegistryObject<TranformingSkullBlock> TRANFORMING_SKULL =
            BLOCKS.register("transformingskull", () -> new TranformingSkullBlock(BlockBehaviour.Properties.copy(Blocks.ZOMBIE_HEAD)));

    public static final RegistryObject<TranformingWallSkullBlock> TRANFORMING_WALL_SKULL =
            BLOCKS.register("transformingskull_wall", () -> new TranformingWallSkullBlock(BlockBehaviour.Properties.copy(Blocks.ZOMBIE_HEAD)));

    public static final RegistryObject<BlockEntityType<TransformingSkullBlockEntity>> TRANFORMING_SKULL_ENTITY =
            BLOCK_ENTITIES.register("transformingskull_entity", () ->
                    BlockEntityType.Builder.of(TransformingSkullBlockEntity::new, TRANFORMING_SKULL.get(), TRANFORMING_WALL_SKULL.get()).build(null));

    public static final BlockBehaviour.Properties INSTANT_BREAKER_SETTINGS =
            BlockBehaviour.Properties.of()
                    .strength(-1.0F, 3600000.8F)
                    .noOcclusion()
                    .noParticlesOnBreak()
                    .pushReaction(PushReaction.BLOCK)
                    .noCollission()
                    .randomTicks();

    public static final RegistryObject<InstantBreakingBlock> INSTANT_BREAKER_RIFTRESIDUE =
            BLOCKS.register("rift_residue", () -> new InstantBreakingBlock(INSTANT_BREAKER_SETTINGS));

    public static final RegistryObject<BlockEntityType<InstantBreakingBlockEntity>> INSTANT_BREAKER_ENTITY =
            BLOCK_ENTITIES.register("instant_breaker_entity", () ->
                    BlockEntityType.Builder.of(InstantBreakingBlockEntity::new, INSTANT_BREAKER_RIFTRESIDUE.get()).build(null));

    private static ToIntFunction<BlockState> createLightLevelFromBoolBlockState(BooleanProperty property, int litLevel) {
        return state -> state.getValue(property) ? litLevel : 0;
    }

    public static final BooleanProperty ETERNAL = BooleanProperty.create("eternal");

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return OneironautItemRegistry.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}