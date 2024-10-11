package cc.abbie.emi_ores.forge;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.client.FeaturesReciever;
import cc.abbie.emi_ores.networking.FeaturesSender;
import cc.abbie.emi_ores.networking.payload.S2CSendBiomeInfoPayload;
import cc.abbie.emi_ores.networking.payload.S2CSendFeaturesPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

@Mod(EmiOres.MODID)
public class EmiOresForge {
    public EmiOresForge(IEventBus modBus) {
        EmiOres.init();

        NeoForge.EVENT_BUS.addListener(EmiOresForge::onDatapackSync);
        modBus.addListener(this::registerPayloads);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(EmiOres.MODID).versioned("1.0.0").optional();

        registrar.playToClient(S2CSendBiomeInfoPayload.TYPE, S2CSendBiomeInfoPayload.CODEC, (payload, context) -> {
            context.enqueueWork(() -> FeaturesReciever.receive(payload));
        });

        registrar.playToClient(S2CSendFeaturesPayload.TYPE, S2CSendFeaturesPayload.CODEC, (payload, context) -> {
            context.enqueueWork(() -> FeaturesReciever.receive(payload));
        });
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        List<ServerPlayer> players = event.getPlayer() == null ? event.getPlayerList().getPlayers() : List.of(event.getPlayer());
        players.forEach(player -> FeaturesSender.onSyncDataPackContents(
                player,
                (ply, pay) -> ply.connection.hasChannel(pay),
                PacketDistributor::sendToPlayer
        ));
    }
}
