package org.arcticquests.dev.oneironaut.oneironautt.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;

public final class OneironautNetwork {
    private OneironautNetwork() {}

    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "main"))
            .networkProtocolVersion(() -> PROTOCOL)
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .simpleChannel();

    private static int id = 0;

    public static void register() {
        CHANNEL.messageBuilder(HoverliftAntiDesyncPacket.class, id++)
                .encoder((HoverliftAntiDesyncPacket.Companion::encode)) // no payload
                .decoder(HoverliftAntiDesyncPacket.Companion::decode)
                .consumerMainThread(HoverliftAntiDesyncPacket.Companion::handle)
                .add();

        CHANNEL.messageBuilder(ParticleBurstPacket.class, id++)
                .encoder(ParticleBurstPacket::serialize)
                .decoder(ParticleBurstPacket.Companion::deserialise)
                .consumerMainThread((msg, ctx) -> {
                    ParticleBurstPacket.Companion.handle(msg);
                })
                .add();

        CHANNEL.messageBuilder(FireballUpdatePacket.class, id++)
                .encoder(FireballUpdatePacket::serialize)
                .decoder(FireballUpdatePacket.Companion::deserialise)
                .consumerMainThread((msg, ctx) -> {
                    FireballUpdatePacket.Companion.handle(msg);
                })
                .add();

        CHANNEL.messageBuilder(ItemUpdatePacket.class, id++)
                .encoder(ItemUpdatePacket::serialize)
                .decoder(ItemUpdatePacket.Companion::deserialise)
                .consumerMainThread((msg, ctx) -> {
                    ItemUpdatePacket.Companion.handle(msg);
                })
                .add();

        CHANNEL.messageBuilder(SpoopyScreamPacket.class, id++)
                .encoder(SpoopyScreamPacket::serialize)
                .decoder(SpoopyScreamPacket.Companion::deserialise)
                .consumerMainThread((msg, ctx) -> {
                    SpoopyScreamPacket.Companion.handle(msg);
                })
                .add();

        CHANNEL.messageBuilder(UnBrainsweepPacket.class, id++)
                .encoder(UnBrainsweepPacket::serialize)
                .decoder(UnBrainsweepPacket.Companion::deserialise)
                .consumerMainThread((msg, ctx) -> {
                    UnBrainsweepPacket.Companion.handle(msg);
                })
                .add();
    }
}