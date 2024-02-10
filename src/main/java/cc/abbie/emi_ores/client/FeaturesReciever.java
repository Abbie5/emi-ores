package cc.abbie.emi_ores.client;

import cc.abbie.emi_ores.networking.packet.S2CSendFeaturesPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Map;

public class FeaturesReciever {
    public static Map<ResourceLocation, PlacedFeature> FEATURES = null;

    public static void receive(S2CSendFeaturesPacket packet, LocalPlayer player, PacketSender responseSender) {
        FEATURES = packet.getFeatures();
    }
}
