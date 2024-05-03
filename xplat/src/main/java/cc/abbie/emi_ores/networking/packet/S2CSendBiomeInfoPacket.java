package cc.abbie.emi_ores.networking.packet;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.client.FeaturesReciever;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class S2CSendBiomeInfoPacket implements Packet<S2CSendBiomeInfoPacket> {
    public static final ResourceLocation ID = EmiOres.id("s2c/send_biome_info");

    private final SetMultimap<ResourceKey<PlacedFeature>, ResourceKey<Biome>> multimap;

    public S2CSendBiomeInfoPacket(SetMultimap<ResourceKey<PlacedFeature>, ResourceKey<Biome>> multimap) {
        this.multimap = multimap;
    }

    public S2CSendBiomeInfoPacket(FriendlyByteBuf buf) {
        this.multimap = HashMultimap.create();
        buf.readMap(
                buf1 -> buf1.readResourceKey(Registries.PLACED_FEATURE),
                buf1 -> buf1.readList(buf2 -> buf2.readResourceKey(Registries.BIOME))
        ).forEach(this.multimap::putAll);
    }

    public SetMultimap<ResourceKey<PlacedFeature>, ResourceKey<Biome>> getBiomes() {
        return multimap;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(
                multimap.asMap(),
                FriendlyByteBuf::writeResourceKey,
                (buf1, collection) -> buf1.writeCollection(collection, FriendlyByteBuf::writeResourceKey)
        );
    }

    @Override
    public void handle() {
        FeaturesReciever.receive(this);
    }

    @Override
    public S2CSendBiomeInfoPacket create(FriendlyByteBuf buf) {
        return new S2CSendBiomeInfoPacket(buf);
    }
}
