package cc.abbie.emi_ores.neoforge;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.client.FeaturesReciever;
import cc.abbie.emi_ores.networking.FeaturesSender;
import cc.abbie.emi_ores.networking.payload.S2CSendBiomeInfoPayload;
import cc.abbie.emi_ores.networking.payload.S2CSendFeaturesPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

@Mod(EmiOres.MODID)
public class EmiOresNeoForge {
    public EmiOresNeoForge(ModContainer mod, IEventBus modBus) {
        EmiOres.init();

        NeoForge.EVENT_BUS.addListener(EmiOresNeoForge::onDatapackSync);
        modBus.addListener(this::registerPayloads);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();

        registrar.playToClient(S2CSendBiomeInfoPayload.TYPE, S2CSendBiomeInfoPayload.CODEC, (payload, context) -> {
            FeaturesReciever.receive(payload);
        });

        registrar.playToClient(S2CSendFeaturesPayload.TYPE, S2CSendFeaturesPayload.CODEC, (payload, context) -> {
            FeaturesReciever.receive(payload);
        });
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        List<ServerPlayer> players = event.getPlayer() == null ? event.getPlayerList().getPlayers() : List.of(event.getPlayer());
        players.forEach(player -> FeaturesSender.onSyncDataPackContents(
                player,
                (p, t) -> true,
                PacketDistributor::sendToPlayer
        ));
    }
}
