package cc.abbie.emi_ores.compat.emi;

import cc.abbie.emi_ores.client.FeaturesReciever;
import cc.abbie.emi_ores.compat.emi.recipe.GeodeGenEmiRecipe;
import cc.abbie.emi_ores.compat.emi.recipe.OreGenEmiRecipe;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Map;

@EmiEntrypoint
public class EmiOresEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        Map<ResourceLocation, PlacedFeature> features = FeaturesReciever.getFeatures();
        if (features.isEmpty()) return;

        registry.addCategory(EmiOresRecipeCategories.OREGEN);
        registry.addCategory(EmiOresRecipeCategories.GEODE);

        features.forEach((id, placedFeature) -> {
            FeatureConfiguration fc = placedFeature.feature().value().config();
            if (fc instanceof OreConfiguration)
                registry.addRecipe(new OreGenEmiRecipe(placedFeature, id));
            else if (fc instanceof GeodeConfiguration)
                registry.addRecipe(new GeodeGenEmiRecipe(placedFeature, id));
        });
    }
}
