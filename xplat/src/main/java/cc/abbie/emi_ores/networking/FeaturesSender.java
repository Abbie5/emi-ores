package cc.abbie.emi_ores.networking;

import cc.abbie.emi_ores.networking.packet.S2CSendFeaturesPacket;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
    public static void onSyncDataPackContents(ServerPlayer player, Predicate<ServerPlayer> canSend, BiConsumer<ServerPlayer, S2CSendFeaturesPacket> sender) {
        if (!canSend.test(player)) return;

        Map<ResourceLocation, PlacedFeature> featureMap = new HashMap<>();
        player.server.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).entrySet().forEach(entry -> {
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
        sender.accept(player, new S2CSendFeaturesPacket(featureMap));
    }

    private static boolean isSupported(PlacementModifier modifier) {
        return modifier instanceof HeightRangePlacement
                || modifier instanceof BiomeFilter
                || modifier instanceof CountPlacement
                || modifier instanceof RarityFilter;
    }
}
