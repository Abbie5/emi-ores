package cc.abbie.emi_ores.fabric;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.networking.FeaturesSender;
import cc.abbie.emi_ores.networking.payload.S2CSendBiomeInfoPayload;
import cc.abbie.emi_ores.networking.payload.S2CSendFeaturesPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class EmiOresFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EmiOres.init();

        PayloadTypeRegistry.playS2C().register(S2CSendBiomeInfoPayload.TYPE, S2CSendBiomeInfoPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(S2CSendFeaturesPayload.TYPE, S2CSendFeaturesPayload.CODEC);

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(EmiOresFabric::onSyncDatapackContents);
    }

    public static void onSyncDatapackContents(ServerPlayer player, boolean joined) {
        FeaturesSender.onSyncDataPackContents(
                player,
                ServerPlayNetworking::canSend,
                ServerPlayNetworking::send
        );
    }
}
