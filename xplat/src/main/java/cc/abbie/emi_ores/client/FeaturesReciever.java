package cc.abbie.emi_ores.client;

import cc.abbie.emi_ores.networking.packet.S2CSendFeaturesPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Map;

public class FeaturesReciever {
    public static Map<ResourceLocation, PlacedFeature> FEATURES = null;

    public static void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf) {
        FEATURES = new S2CSendFeaturesPacket(buf).getFeatures();
    }
}
