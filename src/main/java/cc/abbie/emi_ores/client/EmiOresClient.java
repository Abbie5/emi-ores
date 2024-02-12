package cc.abbie.emi_ores.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class EmiOresClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EmiOresClientNetworking.registerClient();

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> FeaturesReciever.FEATURES = null);
    }
}
