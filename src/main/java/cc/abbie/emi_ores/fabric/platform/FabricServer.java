package cc.abbie.emi_ores.fabric.platform;

import cc.abbie.emi_ores.platform.services.Server;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class FabricServer implements Server {
    @Override
    public boolean canSend(ServerPlayer player, ResourceLocation channelName) {
        return ServerPlayNetworking.canSend(player, channelName);
    }

    @Override
    public void send(ServerPlayer player, ResourceLocation channelName, FriendlyByteBuf buf) {
        ServerPlayNetworking.send(player, channelName, buf);
    }
}
