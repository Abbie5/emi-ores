package cc.abbie.emi_ores.fabric.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import cc.abbie.emi_ores.client.BiomeSpriteRegistry;
import cc.abbie.emi_ores.client.EmiOresClient;
import cc.abbie.emi_ores.client.FeaturesReciever;
import cc.abbie.emi_ores.networking.packet.S2CSendBiomeInfoPacket;
import cc.abbie.emi_ores.networking.packet.S2CSendFeaturesPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
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

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            private final BiomeSpriteRegistry.ReloadListener delegate = new BiomeSpriteRegistry.ReloadListener();
            @Override
            public ResourceLocation getFabricId() {
                return delegate.getId();
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return delegate.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }
        });
        ClientSpriteRegistryCallback.event(InventoryMenu.BLOCK_ATLAS)
                .register((textureAtlas, registry) -> BiomeSpriteRegistry.registerSprites(registry::register));
    }
}
