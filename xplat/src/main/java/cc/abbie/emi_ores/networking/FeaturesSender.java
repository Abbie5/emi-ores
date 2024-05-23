package cc.abbie.emi_ores.networking;

import cc.abbie.emi_ores.networking.packet.Packet;
import cc.abbie.emi_ores.networking.packet.S2CSendBiomeInfoPacket;
import cc.abbie.emi_ores.networking.packet.S2CSendFeaturesPacket;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
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
import java.util.function.Predicate;

public class FeaturesSender {
    public static void onSyncDataPackContents(ServerPlayer player, Predicate<ServerPlayer> canSend, BiConsumer<ServerPlayer, Packet<?>> sender) {
        if (!canSend.test(player)) return;

        Map<ResourceLocation, PlacedFeature> featureMap = new HashMap<>();
        SetMultimap<ResourceKey<PlacedFeature>, ResourceKey<Biome>> features2biomes = HashMultimap.create();
        RegistryAccess access = player.server.registryAccess();
        Registry<PlacedFeature> placedFeatureRegistry = access.registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
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
        access.registryOrThrow(Registry.BIOME_REGISTRY).entrySet().forEach(biomeEntry -> {
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

        sender.accept(player, new S2CSendBiomeInfoPacket(features2biomes2));
        sender.accept(player, new S2CSendFeaturesPacket(featureMap));
    }

    private static boolean isSupported(PlacementModifier modifier) {
        return modifier instanceof HeightRangePlacement
                || modifier instanceof BiomeFilter
                || modifier instanceof CountPlacement
                || modifier instanceof RarityFilter;
    }
}
