package cc.abbie.emi_ores.networking;

import cc.abbie.emi_ores.networking.payload.S2CSendBiomeInfoPayload;
import cc.abbie.emi_ores.networking.payload.S2CSendFeaturesPayload;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class FeaturesSender {
    public static void onSyncDataPackContents(ServerPlayer player, BiPredicate<ServerPlayer, CustomPacketPayload.Type<?>> canSend, BiConsumer<ServerPlayer, CustomPacketPayload> sender) {
        if (!canSend.test(player, S2CSendBiomeInfoPayload.TYPE) || !canSend.test(player, S2CSendFeaturesPayload.TYPE)) {
            return;
        }

        Map<ResourceLocation, PlacedFeature> featureMap = new HashMap<>();
        SetMultimap<ResourceKey<PlacedFeature>, ResourceKey<Biome>> features2biomes = HashMultimap.create();
        RegistryAccess access = player.server.registryAccess();
        Registry<PlacedFeature> placedFeatureRegistry = access.registryOrThrow(Registries.PLACED_FEATURE);
        placedFeatureRegistry.entrySet().forEach(entry -> {
            // we only care about ore features for now
            PlacedFeature pf = entry.getValue();
            ConfiguredFeature<?, ?> cf = pf.feature().value();
            FeatureConfiguration fc = cf.config();
            if (fc instanceof OreConfiguration || fc instanceof GeodeConfiguration) {
                // remove problematic placement modifiers
                List<PlacementModifier> newModifiers = pf.placement()
                        .stream()
                        .filter(FeaturesSender::isSupported)
                        .toList();

                featureMap.put(entry.getKey().location(), new PlacedFeature(pf.feature(), newModifiers));
            }
        });
        access.registryOrThrow(Registries.BIOME).entrySet().forEach(biomeEntry -> {
            biomeEntry.getValue().getGenerationSettings().features().forEach(placedFeatureHolderSet -> {
                placedFeatureHolderSet.forEach(placedFeatureHolder -> {
                    placedFeatureHolder.unwrapKey().ifPresent(placedFeatureResourceKey -> {
                        features2biomes.put(placedFeatureResourceKey, biomeEntry.getKey());
                    });
                });
            });
        });

        var features2biomes2 = Multimaps.filterKeys(features2biomes, k -> featureMap.containsKey(k.location()));
//        var featureMap2 = Maps.filterKeys(featureMap, k -> features2biomes2.containsKey(ResourceKey.create(Registries.PLACED_FEATURE, k)));

        sender.accept(player, new S2CSendBiomeInfoPayload(features2biomes2));
        sender.accept(player, new S2CSendFeaturesPayload(featureMap));
    }

    private static boolean isSupported(PlacementModifier modifier) {
        return modifier instanceof HeightRangePlacement
                || modifier instanceof BiomeFilter
                || modifier instanceof CountPlacement
                || modifier instanceof RarityFilter;
    }
}
