package net.beholderface.oneironaut.item;

import at.petrak.hexcasting.api.addldata.ADPigment;
import at.petrak.hexcasting.api.item.PigmentItem;
import at.petrak.hexcasting.api.pigment.ColorProvider;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class GeneralPigmentItem extends Item implements PigmentItem {
    public static int[] colors_noosphere = {0x7A00C1, 0x46006F, 0x120049, 0x3200CB, 0x6724DD};
    public static int[] colors_flame = {0xff0000, 0xff4600, 0xff8400, 0xffb300, 0x003994, 0xff0000, 0xff4600, 0xff8400, 0xffb300, 0x003994, 0xffffff};
    public static int[] colors_echo = {0x034150, 0x0a5060, 0x034150, 0x0a5060, 0x009295, 0x29dfeb};
    private final int[] colors;
    public GeneralPigmentItem(Settings settings, int[] colors) {
        super(settings);
        this.colors = colors;
    }

    @Override
    public ColorProvider provideColor(ItemStack stack, UUID owner) {
        return new FrozenPigment(stack, owner).getColorProvider();
    }
}
