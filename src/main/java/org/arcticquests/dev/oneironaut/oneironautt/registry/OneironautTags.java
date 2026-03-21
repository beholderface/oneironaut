package org.arcticquests.dev.oneironaut.oneironautt.registry;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt;

public class OneironautTags {
    public static class Blocks {
        public static final TagKey<Block> growsMonkfruit = MiscAPIKt.getBlockTagKey("oneironaut:growsmonkfruit");
        public static final TagKey<Block> erosionImmune = MiscAPIKt.getBlockTagKey("oneironaut:deeperosionimmune");
        public static final TagKey<Block> breakImmune = MiscAPIKt.getBlockTagKey("oneironaut:hexbreakimmune");
        public static final TagKey<Block> blocksRaycast = MiscAPIKt.getBlockTagKey("oneironaut:blocksraycast");
    }
    public static class Entities {
        public static final TagKey<EntityType<?>> impulseRedirectBlacklist = MiscAPIKt.getEntityTagKey("oneironaut:impulse_redirect_blacklist");
        public static final TagKey<EntityType<?>> livingInterchangeWhitelist = MiscAPIKt.getEntityTagKey("oneironaut:living_interchange_whitelist");
        public static final TagKey<EntityType<?>> monkfruitBlacklist = MiscAPIKt.getEntityTagKey("oneironaut:monkfruit_blacklist");
        public static final TagKey<EntityType<?>> mindRenderAutospare = MiscAPIKt.getEntityTagKey("oneironaut:render_autospare");
        public static final TagKey<EntityType<?>> mindRenderFlayBlacklist = MiscAPIKt.getEntityTagKey("oneironaut:render_flay_blacklist");
        public static final TagKey<EntityType<?>> mindRenderFlayWhitelist = MiscAPIKt.getEntityTagKey("oneironaut:render_flay_whitelist");
        public static final TagKey<EntityType<?>> ideaUnstorable = MiscAPIKt.getEntityTagKey("oneironaut:unstorable");
    }
    public static class Items {
        public static final TagKey<Item> datapackStaves = MiscAPIKt.getItemTagKey("oneironaut:datapack_staves");
    }
    public static class Actions {
        public static final TagKey<ActionRegistryEntry> noLootScrolls =
                TagKey.create(IXplatAbstractions.INSTANCE.getActionRegistry().key(),  ResourceLocation.fromNamespaceAndPath("oneironaut","nolootscrolls"));
    }
}
