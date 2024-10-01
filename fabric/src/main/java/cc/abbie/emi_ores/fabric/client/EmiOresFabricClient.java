package cc.abbie.emi_ores.fabric.client;

import cc.abbie.emi_ores.client.BiomeSpriteRegistry;
import cc.abbie.emi_ores.client.EmiOresClient;
import cc.abbie.emi_ores.client.FeaturesReciever;
import cc.abbie.emi_ores.networking.packet.S2CSendBiomeInfoPacket;
import cc.abbie.emi_ores.networking.packet.S2CSendFeaturesPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.world.inventory.InventoryMenu;

public class EmiOresFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EmiOresClient.init();

        ClientPlayNetworking.registerGlobalReceiver(S2CSendFeaturesPacket.ID, (client, handler, buf, sender) -> {
            buf.readByte();
            new S2CSendFeaturesPacket(buf).handle();
        });
        ClientPlayNetworking.registerGlobalReceiver(S2CSendBiomeInfoPacket.ID, (client, handler, buf, sender) -> {
            buf.readByte();
            new S2CSendBiomeInfoPacket(buf).handle();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> FeaturesReciever.clearFeatures());

        ClientSpriteRegistryCallback.event(InventoryMenu.BLOCK_ATLAS)
                .register((textureAtlas, registry) -> BiomeSpriteRegistry.registerSprites(registry::register));
    }
}
