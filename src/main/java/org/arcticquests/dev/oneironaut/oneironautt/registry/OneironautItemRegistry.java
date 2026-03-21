package org.arcticquests.dev.oneironaut.oneironautt.registry;

import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.items.ItemStaff;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.arcticquests.dev.oneironaut.oneironautt.ClientTime;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.item.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class OneironautItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,Oneironaut.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create( Registries.CREATIVE_MODE_TAB,Oneironaut.MODID);

    //I will not scream at my computer over this

    public static void init(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        TABS.register(modEventBus);
    }


    private static final Item.Properties ONEIRONAUT_STACKABLE64 = new Item.Properties().stacksTo(64);
    private static final Item.Properties ONEIRONAUT_STACKABLE64_NOTAB = new Item.Properties().stacksTo(64);
    private static final Item.Properties ONEIRONAUT_STACKABLE16 = new Item.Properties().stacksTo(16);
    private static final Item.Properties ONEIRONAUT_UNSTACKABLE = new Item.Properties().stacksTo(1);
    private static final Item.Properties ONEIRONAUT_UNSTACKABLE_1024 = new Item.Properties().durability(1024);

    public static final RegistryObject<ItemStolenMediaProvider> PSUEDOAMETHYST_SHARD = ITEMS.register("pseudoamethyst_shard", () -> new
            ItemStolenMediaProvider(ONEIRONAUT_STACKABLE64, (int) (MediaConstants.SHARD_UNIT * 1.5), 1500));
    public static final RegistryObject<ShiftingPseudoamethystItem> SHIFTING_PSEUDOAMETHYST = ITEMS.register("shifting_pseudoamethyst", () -> new ShiftingPseudoamethystItem(ONEIRONAUT_STACKABLE64.rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<BucketItem> THOUGHT_SLURRY_BUCKET = ITEMS.register("thought_slurry_bucket", () -> new BucketItem(OneironautMiscRegistry.THOUGHT_SLURRY, ONEIRONAUT_UNSTACKABLE));
    public static final RegistryObject<ReverberationRod> REVERBERATION_ROD = ITEMS.register("reverberation_rod", () -> new ReverberationRod(ONEIRONAUT_UNSTACKABLE));
    public static final RegistryObject<BottomlessMediaItem> BOTTOMLESS_MEDIA_ITEM = ITEMS.register("endless_phial", () -> new BottomlessMediaItem(ONEIRONAUT_UNSTACKABLE));
    public static final RegistryObject<BottomlessCastingItem> BOTTOMLESS_CASTING_ITEM = ITEMS.register("bottomless_trinket", () -> new BottomlessCastingItem(ONEIRONAUT_UNSTACKABLE));
    public static final RegistryObject<ItemStaff> ECHO_STAFF = ITEMS.register("echo_staff", () -> new GeneralNoisyStaff(ONEIRONAUT_UNSTACKABLE, SoundEvents.SCULK_CLICKING, SoundEvents.SCULK_SHRIEKER_SHRIEK, null));
    public static final RegistryObject<ItemStaff> BEACON_STAFF = ITEMS.register("beacon_staff", () -> new GeneralNoisyStaff(ONEIRONAUT_UNSTACKABLE, SoundEvents.BEACON_ACTIVATE, SoundEvents.BEACON_DEACTIVATE, null));
    public static final RegistryObject<ShovelItem> SPOON_STAFF = ITEMS.register("spoon_staff", () -> new ShovelItem(Tiers.IRON, 1.5F, -3.0F, ONEIRONAUT_UNSTACKABLE_1024));
    public static final RegistryObject<GeneralPigmentItem> PIGMENT_NOOSPHERE = ITEMS.register("pigment_noosphere", () -> new GeneralPigmentItem(ONEIRONAUT_STACKABLE64, GeneralPigmentItem.colors_noosphere));
    public static final RegistryObject<GeneralPigmentItem> PIGMENT_FLAME = ITEMS.register("pigment_flame", () -> new GeneralPigmentItem(ONEIRONAUT_STACKABLE64, GeneralPigmentItem.colors_flame));
    public static final RegistryObject<GeneralPigmentItem> PIGMENT_ECHO = ITEMS.register("pigment_echo", () -> new GeneralPigmentItem(ONEIRONAUT_STACKABLE64, GeneralPigmentItem.colors_echo));
    public static final RegistryObject<GeneralPigmentItem> PIGMENT_FRENZY = ITEMS.register("pigment_frenzyflame", () -> new GeneralPigmentItem(ONEIRONAUT_STACKABLE64, GeneralPigmentItem.colors_frenzy));
    public static final RegistryObject<Item> PIGMENT_CLOCK = ITEMS.register("pigment_clock", () -> new ArbitaryDeltaPigmentItem(ONEIRONAUT_STACKABLE64, ArbitaryDeltaPigmentItem.skyColors,
            () -> {
                if (!Oneironaut.isServerThread()) {
                    return ((ClientTime.getClientDayTime() + 3000) % 24000) / ArbitaryDeltaPigmentItem.twentyMinutesInTicks;
                } else {
                    return 12000.0;
                }
            }));
    public static final RegistryObject<MemoryFragmentItem> MEMORY_FRAGMENT = ITEMS.register("memory_fragment", () -> new MemoryFragmentItem(ONEIRONAUT_UNSTACKABLE.rarity(Rarity.RARE), MemoryFragmentItem.NAMES_TOWER));
    public static final RegistryObject<WispCaptureItem> WISP_CAPTURE_ITEM = ITEMS.register("wisp_capture_device", () -> new WispCaptureItem(ONEIRONAUT_UNSTACKABLE));
    public static final RegistryObject<MindScalpelItem> MIND_SCALPEL = ITEMS.register("mind_scalpel", () -> new MindScalpelItem(ONEIRONAUT_UNSTACKABLE.rarity(Rarity.RARE)));
    public static final RegistryObject<RenderThorns> RENDER_THORNS = ITEMS.register("rending_thorns", () -> new RenderThorns(ONEIRONAUT_STACKABLE64.rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<ItemLibraryCard> LIBRARY_CARD = ITEMS.register("library_card", () -> new ItemLibraryCard(ONEIRONAUT_UNSTACKABLE));
    public static final RegistryObject<Item> RIFT_RESIDUE = ITEMS.register("rift_residue", () -> new RiftResidueItem(ONEIRONAUT_STACKABLE64, ArbitaryDeltaPigmentItem.skyColors,
            () -> ((System.currentTimeMillis() + TimeZone.getDefault().getRawOffset()) % ArbitaryDeltaPigmentItem.irlDayInMilliseconds) / ArbitaryDeltaPigmentItem.irlDayInMilliseconds));

    public static final RegistryObject<BlockItem> PSUEDOAMETHYST_BLOCK_ITEM = ITEMS.register("pseudoamethyst_block", () -> new BlockItem(OneironautBlockRegistry.PSUEDOAMETHYST_BLOCK.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> PSUEDOAMETHYST_BLOCK_INSUBSTANTIAL_ITEM = ITEMS.register("insubstantial_pseudoamethyst_block", () -> new BlockItem(OneironautBlockRegistry.PSUEDOAMETHYST_BLOCK_INSUBSTANTIAL.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> SUPER_BUDDING_ITEM = ITEMS.register("super_budding", () -> new BlockItem(OneironautBlockRegistry.SUPER_BUDDING.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> NOOSPHERE_BASALT_ITEM = ITEMS.register("noosphere_basalt", () -> new BlockItem(OneironautBlockRegistry.NOOSPHERE_BASALT.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> WISP_LANTERN_ITEM = ITEMS.register("wisp_lantern", () -> new BlockItem(OneironautBlockRegistry.WISP_LANTERN.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> WISP_LANTERN_TINTED_ITEM = ITEMS.register("wisp_lantern_tinted", () -> new BlockItem(OneironautBlockRegistry.WISP_LANTERN_TINTED.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> SENTINEL_SENSOR_ITEM = ITEMS.register("sentinel_sensor", () -> new BlockItem(OneironautBlockRegistry.SENTINEL_SENSOR.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> SENTINEL_TRAP_ITEM = ITEMS.register("sentinel_trap", () -> new BlockItem(OneironautBlockRegistry.SENTINEL_TRAP.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> RAYCAST_BLOCKER_ITEM = ITEMS.register("raycast_blocker", () -> new BlockItem(OneironautBlockRegistry.RAYCAST_BLOCKER.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> RAYCAST_BLOCKER_GLASS_ITEM = ITEMS.register("raycast_blocker_glass", () -> new BlockItem(OneironautBlockRegistry.RAYCAST_BLOCKER_GLASS.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> HEX_RESISTANT_BLOCK_ITEM = ITEMS.register("hex_resistant_block", () -> new BlockItem(OneironautBlockRegistry.HEX_RESISTANT_BLOCK.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> CIRCLE_ITEM = ITEMS.register("circle", () -> new BlockItem(OneironautBlockRegistry.CIRCLE.get(), new Item.Properties().fireResistant().rarity(Rarity.EPIC)));
    public static final RegistryObject<BlockItem> MEDIA_ICE_ITEM = ITEMS.register("media_ice", () -> new BlockItem(OneironautBlockRegistry.MEDIA_ICE.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> MEDIA_GEL_ITEM = ITEMS.register("media_gel", () -> new BlockItem(OneironautBlockRegistry.MEDIA_GEL.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> CELL_ITEM = ITEMS.register("cell", () -> new BlockItem(OneironautBlockRegistry.CELL.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> WISP_BATTERY_ITEM = ITEMS.register("wisp_battery", () -> new BlockItem(OneironautBlockRegistry.WISP_BATTERY.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> WISP_BATTERY_DECORATIVE_ITEM = ITEMS.register("decorative_wisp_battery", () -> new BlockItem(OneironautBlockRegistry.WISP_BATTERY_DECORATIVE.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> HOVER_ELEVATOR_ITEM = ITEMS.register("hover_elevator", () -> new BlockItem(OneironautBlockRegistry.HOVER_ELEVATOR.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> HOVER_REPEATER_ITEM = ITEMS.register("hover_repeater", () -> new BlockItem(OneironautBlockRegistry.HOVER_REPEATER.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> PSEUDOAMETHYST_BUD_SMALL_ITEM = ITEMS.register("pseudoamethyst_bud_small", () -> new BlockItem(OneironautBlockRegistry.PSEUDOAMETHYST_BUD_SMALL.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> PSEUDOAMETHYST_BUD_MEDIUM_ITEM = ITEMS.register("pseudoamethyst_bud_medium", () -> new BlockItem(OneironautBlockRegistry.PSEUDOAMETHYST_BUD_MEDIUM.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> PSEUDOAMETHYST_BUD_LARGE_ITEM = ITEMS.register("pseudoamethyst_bud_large", () -> new BlockItem(OneironautBlockRegistry.PSEUDOAMETHYST_BUD_LARGE.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> PSEUDOAMETHYST_CLUSTER_ITEM = ITEMS.register("pseudoamethyst_cluster", () -> new BlockItem(OneironautBlockRegistry.PSEUDOAMETHYST_CLUSTER.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> SPACE_BOMB_ITEM = ITEMS.register("spacebomb", () -> new BlockItem(OneironautBlockRegistry.SPACE_BOMB.get(), ONEIRONAUT_UNSTACKABLE));
    public static final RegistryObject<BlockItem> SLIPWAY_SUPPRESSOR_ITEM = ITEMS.register("slipwaysuppressor", () -> new BlockItem(OneironautBlockRegistry.SLIPWAY_SUPPRESSOR.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> EXTRADIM_LOCUS_ITEM = ITEMS.register("extradimensional_border", () -> new BlockItem(OneironautBlockRegistry.EXTRADIM_LOCUS.get(), ONEIRONAUT_STACKABLE64));

    public static final RegistryObject<BlockItem> CONCEPT_MODIFIER_EMPTY = ITEMS.register("concept_modifier_empty", () -> new BlockItem(OneironautBlockRegistry.CONCEPT_MODIFIER_EMPTY.get(), ONEIRONAUT_STACKABLE64));
    public static final RegistryObject<BlockItem> CONCEPT_MODIFIER_SUS = ITEMS.register("concept_modifier_sus", () -> new BlockItem(OneironautBlockRegistry.CONCEPT_MODIFIER_EMPTY.get(), ONEIRONAUT_STACKABLE64_NOTAB));

    //these only exist to look good in patchouli and satisfy hexdoc
    public static final RegistryObject<BlockItem> INACTIVE_SLIPWAY_ITEM = ITEMS.register("inactiveslipway", () -> new BlockItem(OneironautBlockRegistry.INACTIVE_SLIPWAY.get(), new SortOfImmutableItemSettings()));
    public static final RegistryObject<BlockItem> RIFT_RESIDUE_DROPPER_ITEM = ITEMS.register("rift_residue_dropper", () -> new BlockItem(OneironautBlockRegistry.INSTANT_BREAKER_RIFTRESIDUE.get(), new SortOfImmutableItemSettings()));

    public static final Map<DyeColor, RegistryObject<BlockItem>> COLORFUL_CONCEPT_MODIFIERS = new HashMap<>();
    static {
        for (DyeColor color : DyeColor.values()) {
            RegistryObject<BlockItem> supplier = ITEMS.register("concept_decorator_color_" + color.getName(), () -> new BlockItem(OneironautBlockRegistry.COLORFUL_CONCEPT_MODIFIERS.get(color).get(), ONEIRONAUT_STACKABLE64));
            COLORFUL_CONCEPT_MODIFIERS.put(color, supplier);
        }
    }

    public static final RegistryObject<WriteableBlockItem> CONCEPT_MODIFIER_GRIDSIZE = ITEMS.register("concept_modifier_gridsize", () -> new WriteableBlockItem(OneironautBlockRegistry.CONCEPT_MODIFIER_GRIDSIZE.get(),
            ONEIRONAUT_UNSTACKABLE, (iota) -> iota instanceof DoubleIota && Math.abs(((DoubleIota) iota).getDouble()) <= 2.0));
    public static final RegistryObject<WriteableBlockItem> CONCEPT_MODIFIER_MAXHEALTH = ITEMS.register("concept_modifier_maxhealth", () -> new WriteableBlockItem(OneironautBlockRegistry.CONCEPT_MODIFIER_MAXHEALTH.get(),
            ONEIRONAUT_UNSTACKABLE, (iota) -> iota instanceof DoubleIota && Math.abs(((DoubleIota) iota).getDouble()) <= 10.0));
    public static final RegistryObject<WriteableBlockItem> CONCEPT_MODIFIER_GTP_DROP = ITEMS.register("concept_modifier_gtp_drop", () -> new WriteableBlockItem(OneironautBlockRegistry.CONCEPT_MODIFIER_GTP_DROP.get(),
            ONEIRONAUT_UNSTACKABLE, (iota) -> iota instanceof DoubleIota));
    public static final RegistryObject<WriteableBlockItem> CONCEPT_MODIFIER_ANTIEROSION = ITEMS.register("concept_modifier_antierosion", () -> new WriteableBlockItem(OneironautBlockRegistry.CONCEPT_MODIFIER_ANTIEROSION.get(),
            ONEIRONAUT_UNSTACKABLE, (iota) -> false));
    public static final RegistryObject<WriteableBlockItem> CONCEPT_MODIFIER_REFERENCE_FALSY = ITEMS.register("concept_modifier_falsy", () -> new WriteableBlockItem(OneironautBlockRegistry.CONCEPT_MODIFIER_REFERENCE_FALSY.get(),
            ONEIRONAUT_UNSTACKABLE, (iota) -> false));
    public static final RegistryObject<WriteableBlockItem> CONCEPT_MODIFIER_REFERENCE_COMPARISON = ITEMS.register("concept_modifier_comparison", () -> new WriteableBlockItem(OneironautBlockRegistry.CONCEPT_MODIFIER_REFERENCE_COMPARISON.get(),
            ONEIRONAUT_UNSTACKABLE, (iota) -> iota instanceof BooleanIota));
    public static final RegistryObject<WriteableBlockItem> CONCEPT_MODIFIER_STACK_SIZE = ITEMS.register("concept_modifier_stack_size", () -> new WriteableBlockItem(OneironautBlockRegistry.CONCEPT_MODIFIER_STACK_SIZE.get(),
            ONEIRONAUT_UNSTACKABLE, (iota) -> false));
    public static final RegistryObject<WriteableBlockItem> CONCEPT_CORE = ITEMS.register("concept_core", () -> new WriteableBlockItem(OneironautBlockRegistry.CONCEPT_CORE.get(), ONEIRONAUT_UNSTACKABLE,
            (iota) -> iota instanceof EntityIota && ((EntityIota) iota).getEntity() instanceof Player));

    public static final RegistryObject<BlockItem> CONCEPT_CONNECTOR = ITEMS.register("concept_connector", () -> new BlockItem(OneironautBlockRegistry.CONCEPT_CONNECTOR.get(), ONEIRONAUT_STACKABLE64));

    public static final FoodProperties MONKFRUIT_FOOD = (new FoodProperties.Builder()).nutrition(4).saturationMod(0.6F).fast().alwaysEat().build();
    public static final FoodProperties MONKFRUIT_FOOD_COOKED = (new FoodProperties.Builder()).nutrition(6).saturationMod(0.8F).alwaysEat().build();
    public static final FoodProperties MONKFRUIT_FOOD_JAM = (new FoodProperties.Builder()).nutrition(6).saturationMod(1.0F).alwaysEat().build();
    public static final RegistryObject<MonkfruitItem> MONKFRUIT = ITEMS.register("monkfruit", () -> new MonkfruitItem(OneironautBlockRegistry.RENDER_BUSH.get(), ((ONEIRONAUT_STACKABLE64).food(MONKFRUIT_FOOD))));
    public static final RegistryObject<MonkfruitItemCooked> MONKFRUIT_COOKED = ITEMS.register("monkfruit_cooked", () -> new MonkfruitItemCooked(((ONEIRONAUT_STACKABLE64).food(MONKFRUIT_FOOD_COOKED))));
    public static final RegistryObject<MonkfruitItemJam> MONKFRUIT_JAM = ITEMS.register("hexjam", () -> new MonkfruitItemJam(((ONEIRONAUT_STACKABLE64).food(MONKFRUIT_FOOD_JAM))));


    public static final RegistryObject<CreativeModeTab> ONEIRONAUT_GROUP =
            TABS.register("oneironaut", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.oneironaut.oneironaut"))
                    .icon(() -> new ItemStack(OneironautItemRegistry.PSUEDOAMETHYST_SHARD.get()))
                    .displayItems((params, output) -> {

                        // Items
                        output.accept(PSUEDOAMETHYST_SHARD.get());
                        output.accept(SHIFTING_PSEUDOAMETHYST.get());
                        output.accept(THOUGHT_SLURRY_BUCKET.get());
                        output.accept(REVERBERATION_ROD.get());
                        output.accept(BOTTOMLESS_MEDIA_ITEM.get());
                        output.accept(BOTTOMLESS_CASTING_ITEM.get());
                        output.accept(ECHO_STAFF.get());
                        output.accept(BEACON_STAFF.get());
                        output.accept(SPOON_STAFF.get());
                        output.accept(PIGMENT_NOOSPHERE.get());
                        output.accept(PIGMENT_FLAME.get());
                        output.accept(PIGMENT_ECHO.get());
                        output.accept(PIGMENT_FRENZY.get());
                        output.accept(PIGMENT_CLOCK.get());
                        output.accept(MEMORY_FRAGMENT.get());
                        output.accept(WISP_CAPTURE_ITEM.get());
                        output.accept(MIND_SCALPEL.get());
                        output.accept(RENDER_THORNS.get());
                        output.accept(LIBRARY_CARD.get());
                        output.accept(RIFT_RESIDUE.get());

                        // BlockItems
                        output.accept(PSUEDOAMETHYST_BLOCK_ITEM.get());
                        output.accept(PSUEDOAMETHYST_BLOCK_INSUBSTANTIAL_ITEM.get());
                        output.accept(SUPER_BUDDING_ITEM.get());
                        output.accept(NOOSPHERE_BASALT_ITEM.get());
                        output.accept(WISP_LANTERN_ITEM.get());
                        output.accept(WISP_LANTERN_TINTED_ITEM.get());
                        output.accept(SENTINEL_SENSOR_ITEM.get());
                        output.accept(SENTINEL_TRAP_ITEM.get());
                        output.accept(RAYCAST_BLOCKER_ITEM.get());
                        output.accept(RAYCAST_BLOCKER_GLASS_ITEM.get());
                        output.accept(HEX_RESISTANT_BLOCK_ITEM.get());
                        output.accept(CIRCLE_ITEM.get());
                        output.accept(MEDIA_ICE_ITEM.get());
                        output.accept(MEDIA_GEL_ITEM.get());
                        output.accept(CELL_ITEM.get());
                        output.accept(WISP_BATTERY_ITEM.get());
                        output.accept(WISP_BATTERY_DECORATIVE_ITEM.get());
                        output.accept(HOVER_ELEVATOR_ITEM.get());
                        output.accept(HOVER_REPEATER_ITEM.get());
                        output.accept(PSEUDOAMETHYST_BUD_SMALL_ITEM.get());
                        output.accept(PSEUDOAMETHYST_BUD_MEDIUM_ITEM.get());
                        output.accept(PSEUDOAMETHYST_BUD_LARGE_ITEM.get());
                        output.accept(PSEUDOAMETHYST_CLUSTER_ITEM.get());
                        output.accept(SPACE_BOMB_ITEM.get());
                        output.accept(SLIPWAY_SUPPRESSOR_ITEM.get());
                        output.accept(EXTRADIM_LOCUS_ITEM.get());

                        // Concept modifiers
                        output.accept(CONCEPT_MODIFIER_EMPTY.get());
                        output.accept(CONCEPT_MODIFIER_SUS.get());

                        // “Patchouli/hexdoc only” items (if you still want them visible)
                        output.accept(INACTIVE_SLIPWAY_ITEM.get());
                        output.accept(RIFT_RESIDUE_DROPPER_ITEM.get());

                        // Color variants
                        for (DyeColor color : DyeColor.values()) {
                            RegistryObject<BlockItem> obj = COLORFUL_CONCEPT_MODIFIERS.get(color);
                            if (obj != null) output.accept(obj.get());
                        }

                        output.accept(CONCEPT_MODIFIER_GRIDSIZE.get());
                        output.accept(CONCEPT_MODIFIER_MAXHEALTH.get());
                        output.accept(CONCEPT_MODIFIER_GTP_DROP.get());
                        output.accept(CONCEPT_MODIFIER_ANTIEROSION.get());
                        output.accept(CONCEPT_MODIFIER_REFERENCE_FALSY.get());
                        output.accept(CONCEPT_MODIFIER_REFERENCE_COMPARISON.get());
                        output.accept(CONCEPT_MODIFIER_STACK_SIZE.get());
                        output.accept(CONCEPT_CORE.get());

                        output.accept(CONCEPT_CONNECTOR.get());

                        output.accept(MONKFRUIT.get());
                        output.accept(MONKFRUIT_COOKED.get());
                        output.accept(MONKFRUIT_JAM.get());
                    })
                    .build()
            );


}