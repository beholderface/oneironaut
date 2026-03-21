package org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.utils.HexUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.arcticquests.dev.oneironaut.oneironautt.ClientTime;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautIotaTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

public class DimIota extends Iota {
    public DimIota(@NotNull String dim){
        super(OneironautIotaTypeRegistry.DIM, dim);
    }
    public DimIota(@NotNull ServerLevel world){
        super(OneironautIotaTypeRegistry.DIM, world.dimension().location().toString());
    }
    public DimIota(@NotNull ResourceKey<Level> worldRegistryKey){
        super(OneironautIotaTypeRegistry.DIM, worldRegistryKey.location().toString());
    }
    public static final String DIM_KEY = "dim_key";

    /*public NbtElement getKey(){
        var ctag = HexUtils.downcast(this.payload, NbtCompound.TYPE);
        return (RegistryKey<World>) ctag.get("dim_key");
    }*/

    @Override
    public boolean isTruthy() {
        return true;
    }

    protected boolean toleratesOther(Iota that) {
        if (that.getType().equals(this.type)){
            DimIota other = (DimIota) that;
            return this.payload.equals(other.payload);
        }
        return false;
    }

    public String getDimString(){
        return this.payload.toString();
    }
    public ResourceKey<Level> getWorldKey(){
        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(this.payload.toString()));
    }
    public ServerLevel toWorld(MinecraftServer server){
        return server.getLevel(this.getWorldKey());
    }

    public @NotNull Tag serialize() {
        var data = new CompoundTag();
        var payload = this.payload;
        data.putString(DIM_KEY, (String) payload);
        return data;
    }

    private static final Map<UUID, TextTransformer> styleTransformers = new HashMap<>();

    public static void registerTransformer(TextTransformer transformer){
        styleTransformers.put(transformer.uuid, transformer);
    }
    public static TextTransformer getTransformer(UUID uuid){
        return styleTransformers.get(uuid);
    }
    public static void removeTransformer(UUID uuid){
        styleTransformers.remove(uuid);
    }

    public static IotaType<DimIota> TYPE = new IotaType<>() {
        @Override
        public DimIota deserialize(Tag tag, ServerLevel world) throws IllegalArgumentException {
            var ctag = HexUtils.downcast(tag, CompoundTag.TYPE);
            return new DimIota(ctag.getString(DIM_KEY));
        }

        static {
            registerTransformer(TextTransformer.colorizer("minecraft:overworld", 0x00aa00));
            registerTransformer(TextTransformer.colorizer("minecraft:the_nether", 0xaa0000));
            registerTransformer(TextTransformer.colorizer("minecraft:the_end", 0xffff55));
            registerTransformer(new TextTransformer(UUID.randomUUID(), (t, s)->{
                if (s.equals("oneironaut:noosphere")){
                    return t.copy().setStyle(t.getStyle().withColor(0xaa00aa).withBold(true));
                }
                return t;
            }));
            registerTransformer(new TextTransformer(UUID.randomUUID(), (t, s)->{
                if (s.equals("oneironaut:deep_noosphere")){
                    return t.copy().setStyle(randomizedFormatting(t.getStyle().withColor(0xb300de)));
                }
                return t;
            }));
        }

        @Override
        public Component display(Tag tag) {
            var ctag = HexUtils.downcast(tag, CompoundTag.TYPE);
            String worldKey = ctag.getString(DIM_KEY);
            Component text = Component.nullToEmpty(worldKey).copy();
            String originalString = text.getString();
            text = text.copy().setStyle(text.getStyle().withColor(0x5555ff)); //default coloring
            for (TextTransformer transformer : styleTransformers.values()){
                text = transformer.transform(text, worldKey);
            }
            //don't let people change the actual string of the text server-side, it might break hexes that rely on stuff like Scrivener's
            if (Oneironaut.isServerThread() && !text.getString().equals(originalString)){
                text = Component.nullToEmpty(worldKey).copy().setStyle(text.getStyle());
            }

            return text;
        }
        static final boolean isClient = FMLEnvironment.dist == Dist.CLIENT;
        private static Style randomizedFormatting(Style original){
            if (isClient){
                RandomSource random = RandomSource.create(ClientTime.getClientTime() / 5);
                if (random.nextInt(3) == 0){
                    original = original.withBold(true);
                }
                if (random.nextInt(4) == 0){
                    original = original.withItalic(true);
                }
                if (random.nextInt(4) == 0){
                    original = original.withStrikethrough(true);
                }
                if (random.nextInt(4) == 0){
                    original = original.withUnderlined(true);
                }
                //usually make it illegible
                if (random.nextInt(10) != 0){
                    int choice = random.nextInt(3);
                    if (choice == 0){
                        original = original.withObfuscated(true);
                    } else {
                        String fontID = switch (choice){
                            case 1: yield "minecraft:alt";
                            case 2: yield "minecraft:illageralt";
                            default: yield "minecraft:uniform"; //pretty sure this will never actually come up
                        };
                        original = original.withFont( ResourceLocation.parse(fontID));
                    }
                }
                return original;
            } else {
                return original.withBold(true);
            }
        }

        @Override
        public int color() {
            return 0xff_5555FF;
        }
    };

    public static class TextTransformer {
        public final UUID uuid;
        protected final BiFunction<Component, String, Component> function;
        public TextTransformer(UUID uuid, BiFunction<Component, String, Component> function){
            this.uuid = uuid;
            this.function = function;
        }
        public Component transform(Component text, String worldKey){
            return function.apply(text, worldKey);
        }

        public static TextTransformer colorizer(String keyToColorize, int color){
            BiFunction<Component, String, Component> transformer = (original, string) ->{
                if (string.equals(keyToColorize)){
                    return original.copy().setStyle(original.getStyle().withColor(color));
                } else {
                    return original;
                }
            };
            return new TextTransformer(UUID.randomUUID(), transformer);
        }
    }
}
