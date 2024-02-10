package cc.abbie.emi_ores.networking.packet;

import cc.abbie.emi_ores.EmiOres;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Map;

public class S2CSendFeaturesPacket implements FabricPacket {
    public static final PacketType<S2CSendFeaturesPacket> TYPE = PacketType.create(EmiOres.id("s2c/send_features"), S2CSendFeaturesPacket::new);

    private final Map<ResourceLocation, PlacedFeature> features;

    public S2CSendFeaturesPacket(Map<ResourceLocation, PlacedFeature> features) {
        this.features = features;
    }

    public S2CSendFeaturesPacket(FriendlyByteBuf buf) {
        this.features = buf.readMap(
                FriendlyByteBuf::readResourceLocation,
                friendlyByteBuf -> friendlyByteBuf.readJsonWithCodec(PlacedFeature.DIRECT_CODEC)
        );
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(
                features,
                FriendlyByteBuf::writeResourceLocation,
                (friendlyByteBuf, feature) -> friendlyByteBuf.writeJsonWithCodec(PlacedFeature.DIRECT_CODEC, feature)
        );
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public Map<ResourceLocation, PlacedFeature> getFeatures() {
        return features;
    }
}
