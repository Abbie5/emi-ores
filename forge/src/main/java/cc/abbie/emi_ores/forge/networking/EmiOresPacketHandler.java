package cc.abbie.emi_ores.forge.networking;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.networking.packet.S2CSendBiomeInfoPacket;
import cc.abbie.emi_ores.networking.packet.S2CSendFeaturesPacket;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class EmiOresPacketHandler {
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            EmiOres.id("channel"),
            () -> "",
            s -> true,
            s -> true
    );

    public static void init() {
        int id = 0;
        CHANNEL.registerMessage(
                id++,
                S2CSendFeaturesPacket.class,
                S2CSendFeaturesPacket::write,
                S2CSendFeaturesPacket::new,
                (pkt, ctx) -> {
                    pkt.handle();
                    ctx.get().setPacketHandled(true);
                },
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        CHANNEL.registerMessage(
                id++,
                S2CSendBiomeInfoPacket.class,
                S2CSendBiomeInfoPacket::write,
                S2CSendBiomeInfoPacket::new,
                (pkt, ctx) -> {
                    pkt.handle();
                    ctx.get().setPacketHandled(true);
                },
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }
}
