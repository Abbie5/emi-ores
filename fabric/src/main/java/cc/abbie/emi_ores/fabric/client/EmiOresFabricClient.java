package cc.abbie.emi_ores.fabric.client;

import cc.abbie.emi_ores.client.EmiOresClient;
import cc.abbie.emi_ores.client.FeaturesReciever;
import cc.abbie.emi_ores.networking.packet.S2CSendBiomeInfoPacket;
import cc.abbie.emi_ores.networking.packet.S2CSendFeaturesPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class EmiOresFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EmiOresClient.init();

        ClientPlayNetworking.registerGlobalReceiver(S2CSendFeaturesPacket.ID, (client, handler, buf, sender) -> new S2CSendFeaturesPacket(buf).handle());
        ClientPlayNetworking.registerGlobalReceiver(S2CSendBiomeInfoPacket.ID, (client, handler, buf, sender) -> new S2CSendBiomeInfoPacket(buf).handle());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> FeaturesReciever.clearFeatures());
    }
}
