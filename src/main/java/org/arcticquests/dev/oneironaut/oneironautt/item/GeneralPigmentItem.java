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

public class GeneralPigmentItem extends Item implements PigmentItem {

    public static final int[] colors_noosphere = {0x7A00C1, 0x46006F, 0x120049, 0x3200CB, 0x6724DD};
    public static final int[] colors_flame = {0xff0000, 0xff4600, 0xff8400, 0xffb300, 0x003994, 0xff0000, 0xff4600, 0xff8400, 0xffb300, 0x003994, 0xffffff};
    public static final int[] colors_frenzy = {0xffd900, 0xff8f00, 0xff7c00, 0xff5500, 0xffb300, 0xff7c00};
    public static final int[] colors_echo = {0x034150, 0x0a5060, 0x034150, 0x0a5060, 0x009295, 0x29dfeb};

    private static final Map<Item, GeneralColorProvider> COLOR_PROVIDER_MAP = new HashMap<>();
    public GeneralPigmentItem(Properties settings, int[] colors) {
        super(settings);
        COLOR_PROVIDER_MAP.put(this, new GeneralColorProvider(colors));
    }

    protected static class GeneralColorProvider extends ColorProvider{
        protected final int[] colors;
        protected static final Vec3 vec = new Vec3(0.1, 0.1, 0.1);
        protected GeneralColorProvider(int[] colors){
            this.colors = colors;
        }
        @Override
        protected int getRawColor(float time, Vec3 position) {
            return ADPigment.morphBetweenColors(this.colors, vec, time / 20 / 20, position);
        }
    }

    @Override
    public ColorProvider provideColor(ItemStack stack, UUID owner) {
        return COLOR_PROVIDER_MAP.get(this);
    }
}
