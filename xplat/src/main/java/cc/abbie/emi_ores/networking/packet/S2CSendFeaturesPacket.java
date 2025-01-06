package cc.abbie.emi_ores.networking.packet;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.client.FeaturesReciever;
import cc.abbie.emi_ores.networking.NetworkUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Map;

public class S2CSendFeaturesPacket implements Packet<S2CSendFeaturesPacket> {
    public static final ResourceLocation ID = EmiOres.id("s2c/send_features");

    private final Map<ResourceLocation, PlacedFeature> features;

    public S2CSendFeaturesPacket(Map<ResourceLocation, PlacedFeature> features) {
        this.features = features;
    }

    public S2CSendFeaturesPacket(FriendlyByteBuf buf) {
        this.features = buf.readMap(
                FriendlyByteBuf::readResourceLocation,
                friendlyByteBuf -> NetworkUtils.readNbtWithCodec(buf, PlacedFeature.DIRECT_CODEC)
        );
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(
                features,
                FriendlyByteBuf::writeResourceLocation,
                (friendlyByteBuf, feature) -> NetworkUtils.writeNbtWithCodec(buf, PlacedFeature.DIRECT_CODEC, feature)
        );
    }

    public Map<ResourceLocation, PlacedFeature> getFeatures() {
        return features;
    }

    @Override
    public void handle() {
        FeaturesReciever.receive(this);
    }

    @Override
    public S2CSendFeaturesPacket create(FriendlyByteBuf buf) {
        return new S2CSendFeaturesPacket(buf);
    }
}
