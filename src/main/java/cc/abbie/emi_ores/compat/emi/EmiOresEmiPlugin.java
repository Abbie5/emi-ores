package cc.abbie.emi_ores.compat.emi;

import cc.abbie.emi_ores.client.FeaturesReciever;
import cc.abbie.emi_ores.compat.emi.recipe.PlacedFeatureEmiRecipe;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

public class EmiOresEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(EmiOresRecipeCategories.OREGEN);

        FeaturesReciever.FEATURES.forEach((id, placedFeature) -> registry.addRecipe(new PlacedFeatureEmiRecipe(placedFeature, id)));
    }
}
