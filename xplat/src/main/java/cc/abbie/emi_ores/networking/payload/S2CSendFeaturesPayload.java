package cc.abbie.emi_ores.networking.payload;

import cc.abbie.emi_ores.EmiOres;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record S2CSendFeaturesPayload(Map<ResourceLocation, PlacedFeature> features) implements CustomPacketPayload {
    public static final Type<S2CSendFeaturesPayload> TYPE = new Type<>(EmiOres.id("send_features"));
    public static final StreamCodec<FriendlyByteBuf, S2CSendFeaturesPayload> CODEC = StreamCodec.of(S2CSendFeaturesPayload::encode, S2CSendFeaturesPayload::decode);
    private static final Gson GSON = new Gson();
    private static final StreamCodec<ByteBuf, PlacedFeature> INNER_CODEC = ByteBufCodecs.STRING_UTF8.map(
            json -> {
                JsonElement jsonelement = GsonHelper.fromJson(GSON, json, JsonElement.class);
                DataResult<PlacedFeature> dataresult = PlacedFeature.DIRECT_CODEC.parse(JsonOps.INSTANCE, jsonelement);
                return dataresult.getOrThrow((string) -> {
                    return new DecoderException("Failed to decode json: " + string);
                });
            },
            feature -> {
                return GSON.toJson(PlacedFeature.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, feature).getOrThrow((string) -> {
                    return new EncoderException("Failed to encode json: " + string);
                }));
            }
    );

    public static S2CSendFeaturesPayload decode(FriendlyByteBuf buffer) {
        var features = buffer.readMap(
                ResourceLocation.STREAM_CODEC,
                INNER_CODEC
        );

        return new S2CSendFeaturesPayload(features);
    }

    public static void encode(FriendlyByteBuf buffer, S2CSendFeaturesPayload packet) {
        buffer.writeMap(packet.features,
                ResourceLocation.STREAM_CODEC,
                INNER_CODEC
        );
    }

    @NotNull
    @Override
    public Type<S2CSendFeaturesPayload> type() {
        return TYPE;
    }
}
