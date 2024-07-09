package cc.abbie.emi_ores.networking.payload;

import cc.abbie.emi_ores.EmiOres;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public record S2CSendBiomeInfoPayload(SetMultimap<ResourceKey<PlacedFeature>, ResourceKey<Biome>> biomeInfo) implements CustomPacketPayload {
    public static final Type<S2CSendBiomeInfoPayload> TYPE = new Type<>(EmiOres.id("send_biome_info"));
    public static final StreamCodec<FriendlyByteBuf, S2CSendBiomeInfoPayload> CODEC = StreamCodec.of(S2CSendBiomeInfoPayload::encode, S2CSendBiomeInfoPayload::decode);

    public static S2CSendBiomeInfoPayload decode(FriendlyByteBuf buffer) {
        SetMultimap<ResourceKey<PlacedFeature>, ResourceKey<Biome>> map = HashMultimap.create();

        buffer.readMap(
            ResourceKey.streamCodec(Registries.PLACED_FEATURE),
            ResourceKey.streamCodec(Registries.BIOME).apply(ByteBufCodecs.list())
        ).forEach(map::putAll);

        return new S2CSendBiomeInfoPayload(map);
    }

    public static void encode(FriendlyByteBuf buffer, S2CSendBiomeInfoPayload packet) {
        buffer.writeMap(packet.biomeInfo.asMap(),
            ResourceKey.streamCodec(Registries.PLACED_FEATURE),
            ResourceKey.streamCodec(Registries.BIOME).apply(ByteBufCodecs.collection(HashSet::new))
        );
    }

    @NotNull
    @Override
    public Type<S2CSendBiomeInfoPayload> type() {
        return TYPE;
    }
}
