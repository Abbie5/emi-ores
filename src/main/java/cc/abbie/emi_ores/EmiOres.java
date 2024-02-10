package cc.abbie.emi_ores;

import cc.abbie.emi_ores.networking.packet.S2CSendFeaturesPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.HashMap;
import java.util.Map;

public class EmiOres implements ModInitializer {
    public static final String MODID = "emi_ores";
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            Map<ResourceLocation, PlacedFeature> featureMap = new HashMap<>();
            player.server.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).entrySet().forEach(entry -> {
                // we only care about ore features for now
                PlacedFeature pf = entry.getValue();
                if (pf.feature().value().config() instanceof OreConfiguration)
                    featureMap.put(entry.getKey().location(), pf);
            });
            ServerPlayNetworking.send(player, new S2CSendFeaturesPacket(featureMap));
        });
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }
}
