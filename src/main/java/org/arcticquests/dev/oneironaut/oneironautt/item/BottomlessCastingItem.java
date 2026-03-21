package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class BottomlessCastingItem extends ItemPackagedHex {
    public BottomlessCastingItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean breakAfterDepletion() {
        return false;
    }

    @Override
    public boolean canDrawMediaFromInventory(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canRecharge(ItemStack stack) {
        return false;
    }

    @Override
    public long getMedia(ItemStack stack) {
        return MediaConstants.DUST_UNIT / 10;
    }

    @Override
    public long getMaxMedia(ItemStack stack) {
        return Long.MAX_VALUE;
    }

    @Override
    public void setMedia(ItemStack stack, long media) {
        //no-op
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return this.hasHex(pStack);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        if (Minecraft.getInstance() != null && Minecraft.getInstance().level != null){
            return Color.HSBtoRGB(((float) Minecraft.getInstance().level.getGameTime()) / 100.0f, 1f, 1f);
        }
        return 0;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        return 13;
    }

    @Override
    public void writeHex(ItemStack stack, List<Iota> program, @Nullable FrozenPigment pigment, long media) {
        ListTag patsTag = new ListTag();
        for (Iota pat : program) {
            patsTag.add(IotaType.serialize(pat));
        }

        NBTHelper.putList(stack, TAG_PROGRAM, patsTag);
        if (pigment != null)
            NBTHelper.putCompound(stack, TAG_PIGMENT, pigment.serializeToNBT());
    }

    public int cooldown(){
        return 5;
    }

    //why are these private in ItemMediaHolder anyway?
    public static final DecimalFormat DUST_AMOUNT = new DecimalFormat("###,###.##");
    public static final DecimalFormat PERCENTAGE = new DecimalFormat("####");


    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                              TooltipFlag pIsAdvanced) {
            long media = MediaConstants.DUST_UNIT / 10;

            TextColor color = TextColor.fromRgb(MediaHelper.mediaBarColor(media, Long.MAX_VALUE));

            MutableComponent mediamount = Component.literal(DUST_AMOUNT.format(media / (float) MediaConstants.DUST_UNIT));
            MutableComponent percentFull = Component.literal(PERCENTAGE.format(0) + "%");
            //infinity!
            MutableComponent maxCapacity = Component.nullToEmpty("∞").copy();

            mediamount.withStyle(style -> style.withColor(HEX_COLOR));
            maxCapacity.withStyle(style -> style.withColor(HEX_COLOR));
            percentFull.withStyle(style -> style.withColor(color));

            pTooltipComponents.add(
                    Component.translatable("hexcasting.tooltip.media_amount.advanced",
                            mediamount, maxCapacity, percentFull));
    }
}
