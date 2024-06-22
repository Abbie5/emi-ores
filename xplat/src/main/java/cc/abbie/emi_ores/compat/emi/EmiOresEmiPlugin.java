package cc.abbie.emi_ores.compat.emi;

import cc.abbie.emi_ores.client.FeaturesReciever;
import cc.abbie.emi_ores.compat.emi.recipe.GeodeGenEmiRecipe;
import cc.abbie.emi_ores.compat.emi.recipe.OreGenEmiRecipe;
import cc.abbie.emi_ores.compat.emi.stack.BiomeEmiStack;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiRegistryAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Map;

@EmiEntrypoint
public class EmiOresEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        Minecraft client = Minecraft.getInstance();
        client.level.registryAccess()
                .registryOrThrow(Registries.BIOME)
                .stream()
                .map(BiomeEmiStack::of)
                .forEach(registry::addEmiStack);

        registry.addCategory(EmiOresRecipeCategories.OREGEN);
        registry.addCategory(EmiOresRecipeCategories.GEODE);

        registry.addDeferredRecipes(consumer -> {
            Map<ResourceLocation, PlacedFeature> features = FeaturesReciever.getFeatures();
            if (features.isEmpty()) return;

            features.forEach((id, placedFeature) -> {
                FeatureConfiguration fc = placedFeature.feature().value().config();
                if (fc instanceof OreConfiguration)
                    consumer.accept(new OreGenEmiRecipe(placedFeature, id));
                else if (fc instanceof GeodeConfiguration)
                    consumer.accept(new GeodeGenEmiRecipe(placedFeature, id));
            });
        });
    }

    @Override
    public void initialize(EmiInitRegistry registry) {
        Registry<Biome> biomeRegistry = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.BIOME);

        registry.addRegistryAdapter(EmiRegistryAdapter.simple(Biome.class, biomeRegistry, BiomeEmiStack::of));
        registry.addIngredientSerializer(BiomeEmiStack.class, new BiomeEmiStack.Serializer());
    }
}
