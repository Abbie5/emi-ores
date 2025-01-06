package cc.abbie.emi_ores.forge;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.forge.networking.EmiOresPacketHandler;
import cc.abbie.emi_ores.networking.FeaturesSender;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

@Mod(EmiOres.MODID)
public class EmiOresForge {
    public EmiOresForge() {
        EmiOres.init();
        EmiOresPacketHandler.init();

        MinecraftForge.EVENT_BUS.addListener(EmiOresForge::onDatapackSync);
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        List<ServerPlayer> players = event.getPlayer() == null ? event.getPlayerList().getPlayers() : List.of(event.getPlayer());
        players.forEach(playerListPlayer -> FeaturesSender.onSyncDataPackContents(
                playerListPlayer,
                p -> true,
                (player, packet) ->
                        EmiOresPacketHandler.CHANNELS.get(packet.getClass()).send(PacketDistributor.PLAYER.with(() -> player), packet)
        ));
    }
}
