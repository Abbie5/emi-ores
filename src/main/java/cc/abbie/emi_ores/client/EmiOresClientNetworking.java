package cc.abbie.emi_ores.client;

import cc.abbie.emi_ores.networking.packet.S2CSendFeaturesPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class EmiOresClientNetworking {
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(S2CSendFeaturesPacket.TYPE, FeaturesReciever::receive);
    }
}
