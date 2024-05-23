package cc.abbie.emi_ores.forge.networking;

import cc.abbie.emi_ores.networking.packet.Packet;
import cc.abbie.emi_ores.networking.packet.S2CSendBiomeInfoPacket;
import cc.abbie.emi_ores.networking.packet.S2CSendFeaturesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EmiOresPacketHandler {
    public static Map<Class<? extends Packet<?>>, SimpleChannel> CHANNELS = new HashMap<>();

    public static final SimpleChannel S2C_SEND_FEATURES = createChannel(S2CSendFeaturesPacket.ID, S2CSendFeaturesPacket.class);
    public static final SimpleChannel S2C_SEND_BIOME_INFO = createChannel(S2CSendBiomeInfoPacket.ID, S2CSendBiomeInfoPacket.class);

    private static <T extends Packet<?>> SimpleChannel createChannel(ResourceLocation id, Class<T> clazz) {
        SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(id)
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .networkProtocolVersion(() -> "1")
                .simpleChannel();
        CHANNELS.put(clazz, channel);
        return channel;
    }

    public static void init() {
        S2C_SEND_FEATURES.registerMessage(
                0,
                S2CSendFeaturesPacket.class,
                S2CSendFeaturesPacket::write,
                S2CSendFeaturesPacket::new,
                (pkt, ctx) -> {
                    pkt.handle();
                    ctx.get().setPacketHandled(true);
                },
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        S2C_SEND_BIOME_INFO.registerMessage(
                0,
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
