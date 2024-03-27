package cc.abbie.emi_ores.platform.services;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface Server {
    boolean canSend(ServerPlayer player, ResourceLocation channelName);
    void send(ServerPlayer player, ResourceLocation channelName, FriendlyByteBuf buf);
}
