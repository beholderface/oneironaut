package org.arcticquests.dev.oneironaut.oneironautt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public final class ClientTime {
    private ClientTime() {}

    public static long getClientTime() {
        ClientLevel level = Minecraft.getInstance().level;
        return level != null ? level.getGameTime() : -1L;
    }

    public static long getClientDayTime() {
        ClientLevel level = Minecraft.getInstance().level;
        return level != null ? level.getDayTime() : -1L;
    }
}