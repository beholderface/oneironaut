package org.arcticquests.dev.oneironaut.oneironautt.registry;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.casting.actions.spells.OpMakePackagedSpell;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.OneironautConfig;
import org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.*;
import org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.rod.*;
import org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.*;
import org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.great.*;
import org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.idea.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class OneironautPatternRegistry {
    public static final List<Triple<HexPattern, ResourceLocation, Action>> PATTERNS = new ArrayList<>();
    public static final List<Triple<HexPattern, ResourceLocation, Action>> PER_WORLD_PATTERNS = new ArrayList<>();
    //operators/other actions
    public static final HexPattern GETDIM_SELF = register(HexPattern.fromAngles("wqwqwqwqwqwaeqqe", HexDir.WEST), "getdim1", new OpGetDim(false));
    public static final HexPattern GETDIM_SENTINEL = register(HexPattern.fromAngles("wqwqwqwqwqwaqeeq", HexDir.WEST), "getdim2", new OpGetDim(true));
    public static final HexPattern GETDIM_OVERWORLD = register(
            HexPattern.fromAngles("wqwqwqwqwqwawedewdwedew", HexDir.NORTH_EAST), "getdim/overworld",
            new OpSpecificDim(ResourceLocation.fromNamespaceAndPath("minecraft", "overworld"), false,
                    () -> OneironautConfig.getServer().getAllowOverworldReflection()));
    public static final HexPattern GETDIM_NETHER = register(
            HexPattern.fromAngles("wqwqwqwqwqwaqaaqaw", HexDir.NORTH_EAST), "getdim/nether",
            new OpSpecificDim(ResourceLocation.fromNamespaceAndPath("minecraft", "the_nether"), true,
                    () -> OneironautConfig.getServer().getAllowNetherReflection()));
    public static final HexPattern GET_DIMHEIGHT = register(HexPattern.fromAngles("awqqqwqwqwqwqwq", HexDir.NORTH_EAST), "getdimheight", new OpDimHeight());
    public static final HexPattern GET_DIMSCALE = register(HexPattern.fromAngles("wawawqwqwqwqwqw", HexDir.NORTH_WEST), "getdimscale", new OpDimScale());
    public static final HexPattern ROD_LOOK = register(HexPattern.fromAngles("qwqqqwqawa", HexDir.SOUTH_EAST), "getrodlook", new OpGetInitialRodState(1));
    public static final HexPattern ROD_POS = register(HexPattern.fromAngles("qwqqqwqawaa", HexDir.SOUTH_EAST), "getrodpos", new OpGetInitialRodState(2));
    public static final HexPattern ROD_STAMP = register(HexPattern.fromAngles("qwqqqwqawaaw", HexDir.SOUTH_EAST), "getrodstamp", new OpGetInitialRodState(3));
    public static final HexPattern ROD_RAM_READ = register(HexPattern.fromAngles("qeeweeewddw", HexDir.NORTH_EAST), "readrodram", new OpAccessRAM(false));
    public static final HexPattern ROD_RAM_WRITE = register(HexPattern.fromAngles("eqqwqqqwaaw", HexDir.NORTH_WEST), "writerodram", new OpAccessRAM(true));
    public static final HexPattern ROD_RAM_READ_REMOTE = register(HexPattern.fromAngles("qwaqawewewaqawewddw", HexDir.NORTH_EAST), "readrodramremote", new OpAccessRAMRemote(false));
    public static final HexPattern ROD_RAM_WRITE_REMOTE = register(HexPattern.fromAngles("ewdedwqwqwdedwqwaaw", HexDir.NORTH_WEST), "writerodramremote", new OpAccessRAMRemote(true));

    public static final HexPattern READ_IDEA = register(HexPattern.fromAngles("qwqwqwqwqwqqqwedewq", HexDir.WEST), "readidea", new OpGetIdea());
    public static final HexPattern READ_IDEA_TIME = register(HexPattern.fromAngles("qwqwqwqwqwqqqeqaqeq", HexDir.WEST), "readideatime", new OpGetIdeaTimestamp());
    public static final HexPattern COMPARE_IDEA_WRITER = register(HexPattern.fromAngles("qwqwqwqwqwqaeqedeqe", HexDir.WEST), "readideawriter", new OpGetIdeaWriter());
    public static final HexPattern READ_IDEA_TYPE = register(HexPattern.fromAngles("qwqwqqqaqqqwqwqqqaq", HexDir.WEST), "readideatype", new OpGetIdeaType());
    public static final HexPattern READ_SENTINEL = register(HexPattern.fromAngles("waeawaeddwwd", HexDir.EAST), "readsentinel", new OpReadSentinel());
    public static final HexPattern DETECT_SHROUDED = register(HexPattern.fromAngles("qqqqqwwaawewaawdww", HexDir.SOUTH_EAST), "detectshroud", new OpDetectShrouded());
    //normal spells
    public static final HexPattern DELAY_ROD = register(HexPattern.fromAngles("qwqqqwqaqddq", HexDir.SOUTH_EAST), "delayrod", new OpDelayRod());
    public static final HexPattern HALT_ROD = register(HexPattern.fromAngles("aqdeeweeew", HexDir.SOUTH_WEST), "haltrod", new OpHaltRod(0));
    public static final HexPattern RESET_ROD = register(HexPattern.fromAngles("deaqqwqqqw", HexDir.SOUTH_EAST), "resetrod", new OpHaltRod(1));
    public static final HexPattern QUERY_ROD = register(HexPattern.fromAngles("qwqqqwqaeaqa", HexDir.SOUTH_EAST), "queryrod", new OpCheckForRod());
    public static final HexPattern ROD_LOOP_ACTIVE = register(HexPattern.fromAngles("qwqqqwqaqded", HexDir.SOUTH_EAST), "rodloopactive", new OpCheckForRodOther());
    public static final HexPattern WRITE_IDEA_IOTA = register(HexPattern.fromAngles("eweweweweweeewqaqwe", HexDir.EAST), "writeidea", new OpStoreIota());
    public static final HexPattern GET_SOULPRINT = register(HexPattern.fromAngles("qqaqwedee", HexDir.EAST), "getsoulprint", new OpGetSoulprint());
    public static final HexPattern SIGN_ITEM = register(HexPattern.fromAngles("qqaqwedeea", HexDir.EAST), "signitem", new OpSignItem());
    public static final HexPattern CHECK_SIGNATURE = register(HexPattern.fromAngles("qqaqwedeed", HexDir.EAST), "checksignature", new OpCompareSignature());
    public static final HexPattern CIRCLE = register(HexPattern.fromAngles("wwwwwwqwwwwwwqwwwwwwqwwwwwwqwwwwwwqwwwwww", HexDir.SOUTH_EAST), "circle", new OpCircle());
    //it's supposed to look like a classic game of life glider
    public static final HexPattern ADVANCE_AUTOMATON = register(HexPattern.fromAngles("qqwqwqwaqeee", HexDir.SOUTH_WEST), "advanceautomaton", new OpAdvanceAutomaton());
    //public static final HexPattern TRIGGER_AUTOMATON = regi(not actually, hexdoc regex, this is commented out)ster(HexPattern.fromAngles("eewewewdeqqq", HexDir.SOUTH_EAST), "triggerautomaton", new OpTriggerAutomaton());

    /*dang you hexdoc
    public static final HexPattern CRAFT_ROD = register(HexPattern.fromAngles("eqqqqqawweqqqqqawweqqqqqawwdeqewwwwweqeeeqewwwwweqe", HexDir.EAST), "craftrod", new OpMakePackagedSpell<>((ItemPackagedHex) OneironautThingRegistry.REVERBERATION_ROD.get(), MediaConstants.CRYSTAL_UNIT
    public static final HexPattern CRAFT_BOTTOMLESS_TRINKET = register(HexPattern.fromAngles("wwqeeeeewqqqqqewwaqeqwqeqqqeqwqeq", HexDir.EAST), "craftbottomlesstrinket", new OpWriteBottomlessTrinket()*/
    //public static final HexPattern MUFFLE_WISP = dontdoithexdocilleatyourknees(HexPattern.fromAngles("aaqdwaaqaweewaqawee", HexDir.WEST), "mufflewisp", new OpSetWispVolume());

    //great spells
    public static final HexPattern DIM_TELEPORT = registerPerWorld(HexPattern.fromAngles("qeewwwweeqeqeewwwweeqdqqdwwwdqeqdwwwdqdadwwdqdwwddadaqadaawww", HexDir.NORTH_EAST), "dimteleport", new OpDimTeleport());
    public static final HexPattern INFUSE_MEDIA = registerPerWorld(HexPattern.fromAngles("wwaqqqqqeqqqwwwqqeqqwwwqqweqadadadaqeqeqadadadaqe", HexDir.EAST), "infusemedia", new OpInfuseMedia());
    public static final HexPattern SWAP_SPACE = registerPerWorld(HexPattern.fromAngles("wqqqwwwwwqqqwwwqdaqadwqqwdaqadweqeqqqqeqeqaqeqedeqeqa", HexDir.EAST), "swapspace", new OpSwapSpace());
    public static final HexPattern RESIST_DETECTION = registerPerWorld(HexPattern.fromAngles("wawwwdwdwwaqqqqqe", HexDir.EAST), "resistdetection", new OpResistDetection());
    public static final HexPattern APPLY_NOT_MISSING = registerPerWorld(HexPattern.fromAngles("qdaeqeawaeqeadqqdeed", HexDir.SOUTH_WEST), "applynotmissing", new OpMarkEntity());
    public static final HexPattern APPLY_MIND_RENDER = registerPerWorld(HexPattern.fromAngles("qweqadeqadeqadqqqwdaqedaqedaqeqaqdwawdwawdwaqawdwawdwawddwwwwwqdeddw", HexDir.EAST), "applymindrender", new OpApplyOvercastDamage());
    public static final HexPattern REVIVE_FLAYED = registerPerWorld(HexPattern.fromAngles("qeqwqqedeeeeeaqwqeqaqedqde", HexDir.NORTH_EAST), "reviveflayed", new OpReviveFlayed());

    public static final HexPattern WRITE_IDEA_ENTITY = register(HexPattern.fromAngles("weweeedeeewewdqqaw", HexDir.SOUTH_EAST), "writeidea_entity", new OpStoreEntity());
    public static final HexPattern RELEASE_ENTITY = register(HexPattern.fromAngles("wqwqdqaqdqwqwaeedw", HexDir.SOUTH_WEST), "releaseidea_entity", new OpReleaseEntity());

    public static final HexPattern EVAL_EXTRADIMENSIONAL = register(HexPattern.fromAngles("wqwqwqwqwqwaqdeaqqe", HexDir.WEST), "extradimensionaleval", new OpEvalExtradimensional());
    public static final HexPattern SHIFT_SENTINEL = register(HexPattern.fromAngles("wwaeawwaeqqwqwqwqwqwq", HexDir.EAST), "shiftsentinel", new OpShiftSentinel());
    public static final HexPattern EROSION_SHIELD = register(HexPattern.fromAngles("wwqwwqwwqwwqwwqwwaeqwwqqqwwqaeadaqadaawww", HexDir.WEST), "erosionshield", new OpErosionShield());

    public static void init() {
        try {
            for (Triple<HexPattern, ResourceLocation, Action> patternTriple : PATTERNS) {
                Registry.register(HexActions.REGISTRY, patternTriple.getSecond(), new ActionRegistryEntry(patternTriple.getFirst(), patternTriple.getThird()));
            }
            for (Triple<HexPattern, ResourceLocation, Action> patternTriple : PER_WORLD_PATTERNS) {
                Registry.register(HexActions.REGISTRY, patternTriple.getSecond(), new ActionRegistryEntry(patternTriple.getFirst(), patternTriple.getThird()));
            }
            /*for (Triple<String[][], ResourceLocation, ICellSpell> cellTriple : CELL_PATTERNS){
                CellSpellManager.registerCellSpell(cellTriple.getFirst(), cellTriple.getSecond(), cellTriple.getThird());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        registerItemDependentPatterns();
    }

    //stolen from gloop
    private static final Map<Supplier<? extends Item>, UncheckedPatternRegister> itemDependentPatternRegisterers = new HashMap<>();

    static {
        itemDependentPatternRegisterers.put(OneironautItemRegistry.REVERBERATION_ROD, () -> {
            Registry.register(HexActions.REGISTRY, ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "craftrod"),
                    new ActionRegistryEntry(HexPattern.fromAngles("eqqqqqawweqqqqqawweqqqqqawwdeqewwwwweqeeeqewwwwweqe", HexDir.EAST),
                            new OpMakePackagedSpell(OneironautItemRegistry.REVERBERATION_ROD.get(), MediaConstants.CRYSTAL_UNIT * 10)));
        });
        itemDependentPatternRegisterers.put(OneironautItemRegistry.BOTTOMLESS_CASTING_ITEM, () -> {
            Registry.register(HexActions.REGISTRY, ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "craftbottomlesstrinket"),
                    new ActionRegistryEntry(HexPattern.fromAngles("wwqeeeeewqqqqqewwaqeqwqeqqqeqwqeq", HexDir.EAST), new OpWriteBottomlessTrinket()));
        });
    }

    private static void registerItemDependentPatterns() {
        for (Map.Entry<Supplier<? extends Item>, UncheckedPatternRegister> entry : itemDependentPatternRegisterers.entrySet()) {
            if (entry.getKey().get() != null) {
                try {
                    entry.getValue().register();
                } catch (Exception exn) {
                    exn.printStackTrace();
                }
            }
        }
    }

    private static HexPattern register(HexPattern pattern, String name, Action action) {
        Triple<HexPattern, ResourceLocation, Action> triple = new Triple<>(pattern, Oneironaut.id(name), action);
        PATTERNS.add(triple);
        return pattern;
    }

    private static HexPattern registerPerWorld(HexPattern pattern, String name, Action action) {
        Triple<HexPattern, ResourceLocation, Action> triple = new Triple<>(pattern, Oneironaut.id(name), action);
        PER_WORLD_PATTERNS.add(triple);
        return pattern;
    }

    @FunctionalInterface
    public interface UncheckedPatternRegister {
        void register();
    }

    private record Triple<A, B, C>(A first, B second, C third) {
        public A getFirst() { return first; }
        public B getSecond() { return second; }
        public C getThird() { return third; }
    }
}