package cc.abbie.emi_ores.client;

import cc.abbie.emi_ores.networking.packet.S2CSendFeaturesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Collections;
import java.util.Map;

public class FeaturesReciever {
    private static Map<ResourceLocation, PlacedFeature> FEATURES = Collections.emptyMap();

    public static void clearFeatures() {
        FEATURES = Collections.emptyMap();
    }

    public static Map<ResourceLocation, PlacedFeature> getFeatures() {
        return Collections.unmodifiableMap(FEATURES);
    }

    public static void receive(S2CSendFeaturesPacket packet) {
        FEATURES = packet.getFeatures();
    }
}
