package org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.casting.idea.IdeaKeyable;
import org.arcticquests.dev.oneironaut.oneironautt.item.BottomlessMediaItem;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautIotaTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class SoulprintIota extends Iota implements IdeaKeyable {
    public SoulprintIota(@NotNull Tuple<UUID, String> payload){
        super(OneironautIotaTypeRegistry.UUID, payload);
    }

    @Override
    public boolean isTruthy() {
        return true;
    }

    protected boolean toleratesOther(Iota that) {
        if (that.getType().equals(this.type)){
            SoulprintIota other = (SoulprintIota) that;
            Tuple<UUID, String> thisPayload = (Tuple<UUID, String>) this.payload;
            Tuple<UUID, String> thatPayload = (Tuple<UUID, String>) other.payload;
            //not checking the entity name because player name changes are a thing
            return thisPayload.getA().equals(thatPayload.getA());
        }
        return false;
    }

    public @NotNull Tag serialize() {
        var data = new CompoundTag();
        var payload = (Tuple<UUID, String>) this.payload;
        data.putUUID("iota_uuid", payload.getA());
        data.putString("entity_name", payload.getB());
        return data;
    }
    public @NotNull UUID getEntity(){
        return ((Tuple<UUID, String>) this.payload).getA();
    }
    public static IotaType<SoulprintIota> TYPE = new IotaType<>() {
        @Override
        public SoulprintIota deserialize(Tag tag, ServerLevel world) throws IllegalArgumentException {
            var ctag = HexUtils.downcast(tag, CompoundTag.TYPE);
            return new SoulprintIota(new Tuple<UUID, String>(ctag.getUUID("iota_uuid"), ctag.getString("entity_name")));
        }

        @Override
        public Component display(Tag tag) {
            var ctag = HexUtils.downcast(tag, CompoundTag.TYPE);
            var name = ctag.getString("entity_name");
            var uuid = ctag.getUUID("iota_uuid");
            Component original = Component.translatable("hexcasting.iota.oneironaut:uuid.label", name);
            ItemStack soulglimmerStack = HexItems.UUID_PIGMENT.getDefaultInstance();
            FrozenPigment soulglimmercolor = new FrozenPigment(soulglimmerStack, uuid);
            Style coloredStyle = original.getStyle().withColor(IXplatAbstractions.INSTANCE.getColorProvider(soulglimmercolor).getColor(BottomlessMediaItem.time, Vec3.ZERO));
            return original.copy().setStyle(coloredStyle);
        }
        @Override
        public int color() {
            return 0xff_7a63bc;
        }
    };

    @Override
    public String getKey() {
        return this.getEntity().toString() + "soul";
    }

    @Override
    public boolean isValidKey(CastingEnvironment env) {
        return true;
    }
}
