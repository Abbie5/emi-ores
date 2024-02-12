package cc.abbie.emi_ores.compat.emi;

import cc.abbie.emi_ores.client.FeaturesReciever;
import cc.abbie.emi_ores.compat.emi.recipe.PlacedFeatureEmiRecipe;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Map;

public class EmiOresEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        Map<ResourceLocation, PlacedFeature> features = FeaturesReciever.FEATURES;
        if (features != null) {
            registry.addCategory(EmiOresRecipeCategories.OREGEN);

            features.forEach((id, placedFeature) -> registry.addRecipe(new PlacedFeatureEmiRecipe(placedFeature, id)));
        }
    }
}
