package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.common.lib.HexSounds;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryFragmentItem extends Item {

    public final List<ResourceLocation> names;

    public MemoryFragmentItem(Properties settings, List<ResourceLocation> advancementNames) {
        super(settings);
        this.names = advancementNames;
    }

    public static final List<ResourceLocation> NAMES_TOWER = List.of(new ResourceLocation[]{
             ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "lore/treatise1"),
             ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "lore/treatise2"),
             ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "lore/treatise3"),
             ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "lore/treatise4"),
             ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "lore/science1"),
             ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "lore/science2"),
             ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "lore/science3")
    });

    public static final String CRITEREON_KEY = "grant";

    //mostly stolen from base hex lore fragment code
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        player.playSound(HexSounds.READ_LORE_FRAGMENT, 1f, 1f);
        var handStack = player.getItemInHand(usedHand);
        if (!(player instanceof ServerPlayer splayer)) {
            handStack.shrink(1);
            return InteractionResultHolder.success(handStack);
        }
        PlayerAdvancements tracker = splayer.getAdvancements();
        Advancement rootAdvancement = splayer.level().getServer().getAdvancements().getAdvancement( ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "lore/root"));
        if (!tracker.getOrStartProgress(rootAdvancement).isDone()){
            tracker.award(rootAdvancement, CRITEREON_KEY);
        }
        Advancement unfoundLore = null;
        var shuffled = new ArrayList<>(this.names);
        Collections.shuffle(shuffled);
        for (var advID : shuffled) {
            var adv = splayer.level().getServer().getAdvancements().getAdvancement(advID);
            if (adv == null) {
                continue; // uh oh
            }

            if (!tracker.getOrStartProgress(adv).isDone()) {
                unfoundLore = adv;
                break;
            }
        }

        if (unfoundLore == null) {
            splayer.displayClientMessage(Component.translatable("item.oneironaut.memory_fragment.all"), true);
            splayer.giveExperiencePoints(20);
            level.playSound(null, player.position().x, player.position().y, player.position().z,
                    HexSounds.READ_LORE_FRAGMENT, SoundSource.PLAYERS, 1f, 1f);
        } else {
            tracker.award(unfoundLore, CRITEREON_KEY);
        }

        CriteriaTriggers.CONSUME_ITEM.trigger(splayer, handStack);
        splayer.awardStat(Stats.ITEM_USED.get(this));
        handStack.shrink(1);

        return InteractionResultHolder.success(handStack);
    }

    @Override
    public boolean canBeDepleted(){
        return false;
    }
}
