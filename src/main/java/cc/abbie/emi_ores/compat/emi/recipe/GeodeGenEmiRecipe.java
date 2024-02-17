package cc.abbie.emi_ores.compat.emi.recipe;

import cc.abbie.emi_ores.compat.emi.EmiOresRecipeCategories;
import cc.abbie.emi_ores.compat.emi.stack.BiomeEmiStack;
import cc.abbie.emi_ores.mixin.accessor.HeightRangePlacementAccessor;
import cc.abbie.emi_ores.mixin.accessor.RarityFilterAccessor;
import cc.abbie.emi_ores.mixin.accessor.SimpleStateProviderAccessor;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.placement.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class GeodeGenEmiRecipe extends AbstractPlacedFeatureEmiRecipe {
    private final HeightProvider heightProvider;
    private final int rarityChance;
    private final EmiIngredient biomes;
    private final EmiStack alternateInner;
    private final EmiStack inner;
    private final EmiStack middle;
    private final EmiStack outer;
    private final EmiIngredient innerPlacements;
    private final ResourceLocation id;

    public GeodeGenEmiRecipe(PlacedFeature feature, ResourceLocation id) {
        this.id = id;
        HeightProvider heightProvider = null;
        int rarityChance = -1;
        List<Biome> biomes = List.of();
        for (PlacementModifier modifier : feature.placement()) {
            if (modifier instanceof HeightRangePlacement heightRangePlacement) {
                heightProvider = ((HeightRangePlacementAccessor) heightRangePlacement).getHeight();
            } else if (modifier instanceof RarityFilter rarityFilter) {
                rarityChance = ((RarityFilterAccessor) rarityFilter).getChance();
            } else if (modifier instanceof BiomeFilter) {
                biomes = getBiomes(id, feature);
            }
        }
        this.heightProvider = heightProvider;
        this.rarityChance = rarityChance;
        this.biomes = EmiIngredient.of(biomes.stream().map(BiomeEmiStack::new).collect(Collectors.toList()));

        GeodeConfiguration config = (GeodeConfiguration) feature.feature().value().config();

        GeodeBlockSettings geodeBlockSettings = config.geodeBlockSettings;

        if (geodeBlockSettings.alternateInnerLayerProvider instanceof SimpleStateProvider simple) {
            this.alternateInner = EmiStack.of(((SimpleStateProviderAccessor) simple).getState().getBlock()).setChance((float) config.useAlternateLayer0Chance);
        } else {
            this.alternateInner = EmiStack.EMPTY;
        }

        if (geodeBlockSettings.innerLayerProvider instanceof SimpleStateProvider simple) {
            this.inner = EmiStack.of(((SimpleStateProviderAccessor) simple).getState().getBlock());
        } else {
            this.inner = EmiStack.EMPTY;
        }

        if (geodeBlockSettings.middleLayerProvider instanceof SimpleStateProvider simple) {
            this.middle = EmiStack.of(((SimpleStateProviderAccessor) simple).getState().getBlock());
        } else {
            this.middle = EmiStack.EMPTY;
        }

        if (geodeBlockSettings.outerLayerProvider instanceof SimpleStateProvider simple) {
            this.outer = EmiStack.of(((SimpleStateProviderAccessor) simple).getState().getBlock());
        } else {
            this.outer = EmiStack.EMPTY;
        }

        this.innerPlacements = EmiIngredient.of(geodeBlockSettings.innerPlacements.stream().map(BlockState::getBlock).map(EmiStack::of).collect(Collectors.toList()));

    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiOresRecipeCategories.GEODE;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of();
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(alternateInner, inner, middle, outer);
    }

    @Override
    public int getDisplayWidth() {
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return 60;
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(inner, 0, 18)
                .recipeContext(this);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 20, 18);
        widgets.addSlot(alternateInner, 46, 18)
                .recipeContext(this);
        widgets.addSlot(middle, 18, 36)
                .recipeContext(this);
        widgets.addSlot(outer, 36, 36)
                .recipeContext(this);
        widgets.addSlot(innerPlacements, 0, 36)
                .recipeContext(this);

        addDistributionGraph(widgets, 64, 0, heightProvider);

        if (!biomes.isEmpty())
            widgets.addSlot(biomes, 96, 18);

        Component veinFreq = getVeinFreqComponent(-1, -1, rarityChance);
        if (veinFreq != null) {
            widgets.addText(veinFreq, 160, 45, 0, false)
                    .horizontalAlign(TextWidget.Alignment.END)
                    .verticalAlign(TextWidget.Alignment.CENTER);
        }

    }

}
