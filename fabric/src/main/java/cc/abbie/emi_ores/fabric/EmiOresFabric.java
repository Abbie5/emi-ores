package cc.abbie.emi_ores.fabric;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.networking.FeaturesSender;
import cc.abbie.emi_ores.networking.packet.S2CSendFeaturesPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class EmiOresFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EmiOres.init();

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(EmiOresFabric::onSyncDatapackContents);
    }

    public static void onSyncDatapackContents(ServerPlayer player, boolean joined) {
        FeaturesSender.onSyncDataPackContents(
                player,
                p -> true,
                (pl, pk) -> {
                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                    buf.writeByte(0); // appease Forge
                    pk.write(buf);
                    ServerPlayNetworking.send(pl, pk.getId(), buf);
                }
        );
    }
}
