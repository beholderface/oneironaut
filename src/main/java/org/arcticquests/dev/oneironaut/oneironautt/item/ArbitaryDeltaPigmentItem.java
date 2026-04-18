package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.api.addldata.ADPigment;
import at.petrak.hexcasting.api.item.PigmentItem;
import at.petrak.hexcasting.api.pigment.ColorProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class ArbitaryDeltaPigmentItem extends Item implements PigmentItem {

    public static final int[] skyColors = {0x080085, 0xff4c00, 0x8bd4ff, 0x8bd4ff, 0x8bd4ff, 0xce3d00, 0x080085, 0x090092};
    private static final Map<Item, ArbitraryDeltaColorProvider> COLOR_PROVIDER_MAP = new HashMap<>();

    public static final double irlDayInMilliseconds = 24 * 60 * 60 * 1000;
    public static final double twentyMinutesInTicks = 20 * 60 * 20;

    public ArbitaryDeltaPigmentItem(Properties settings, int[] colors, Supplier<Double> deltaGetter) {
        super(settings);
        COLOR_PROVIDER_MAP.put(this, new ArbitraryDeltaColorProvider(deltaGetter, colors));
    }

    protected static class ArbitraryDeltaColorProvider extends ColorProvider {
        protected static final Vec3 vec = new Vec3(1, 1, 1);
        private final Supplier<Double> deltaGetter;
        private final int[] colors;
        public ArbitraryDeltaColorProvider(Supplier<Double> getter, int[] colors){
            super();
            this.deltaGetter = getter;
            this.colors = colors;
        }
        @Override
        protected int getRawColor(float time, Vec3 position) {
            return ADPigment.morphBetweenColors(colors, vec, deltaGetter.get().floatValue(), Vec3.ZERO);
        }
    }

    @Override
    public ColorProvider provideColor(ItemStack stack, UUID owner) {
        return COLOR_PROVIDER_MAP.get(this);
    }
}
