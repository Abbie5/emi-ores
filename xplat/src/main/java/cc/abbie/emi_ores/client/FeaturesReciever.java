package cc.abbie.emi_ores.client;

import cc.abbie.emi_ores.networking.payload.S2CSendBiomeInfoPayload;
import cc.abbie.emi_ores.networking.payload.S2CSendFeaturesPayload;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Collections;
import java.util.Map;

public class FeaturesReciever {
    private static Map<ResourceLocation, PlacedFeature> FEATURES = Collections.emptyMap();
    private static SetMultimap<ResourceKey<PlacedFeature>, ResourceKey<Biome>> BIOMES = HashMultimap.create();

    public static void clearFeatures() {
        FEATURES = Collections.emptyMap();
        BIOMES = HashMultimap.create();
    }

    public static Map<ResourceLocation, PlacedFeature> getFeatures() {
        return Collections.unmodifiableMap(FEATURES);
    }

    public static SetMultimap<ResourceKey<PlacedFeature>, ResourceKey<Biome>> getBiomes() {
        return BIOMES;
    }

    public static void receive(S2CSendFeaturesPayload payload) {
        FEATURES = payload.features();
    }

    public static void receive(S2CSendBiomeInfoPayload payload) {
        BIOMES = payload.biomeInfo();
    }
}
