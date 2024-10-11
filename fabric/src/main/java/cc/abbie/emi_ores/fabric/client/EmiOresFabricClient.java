package cc.abbie.emi_ores.fabric.client;

import cc.abbie.emi_ores.client.EmiOresClient;
import cc.abbie.emi_ores.client.FeaturesReciever;
import cc.abbie.emi_ores.networking.payload.S2CSendBiomeInfoPayload;
import cc.abbie.emi_ores.networking.payload.S2CSendFeaturesPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class EmiOresFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EmiOresClient.init();

        ClientPlayNetworking.registerGlobalReceiver(S2CSendBiomeInfoPayload.TYPE, (payload, context) -> {
            FeaturesReciever.receive(payload);
        });

        ClientPlayNetworking.registerGlobalReceiver(S2CSendFeaturesPayload.TYPE, (payload, context) -> {
            FeaturesReciever.receive(payload);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> FeaturesReciever.clearFeatures());
    }
}
