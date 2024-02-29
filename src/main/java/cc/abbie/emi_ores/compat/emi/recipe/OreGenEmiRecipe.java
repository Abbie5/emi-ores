package cc.abbie.emi_ores.compat.emi.recipe;

import cc.abbie.emi_ores.compat.emi.EmiOresRecipeCategories;
import cc.abbie.emi_ores.compat.emi.stack.BiomeEmiStack;
import cc.abbie.emi_ores.mixin.accessor.*;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ScatteredOreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OreGenEmiRecipe extends AbstractPlacedFeatureEmiRecipe {

    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    private final HeightProvider heightProvider;
    private final EmiIngredient biomes;
    private final float discardChanceOnAirExposure;
    private final int countMin;
    private final int countMax;
    private final int rarityChance;

    public OreGenEmiRecipe(PlacedFeature feature, ResourceLocation id) {
        this.id = id;

        List<EmiIngredient> inputs = new ArrayList<>();
        List<EmiStack> outputs = new ArrayList<>();
        OreConfiguration oreConfig = (OreConfiguration) feature.feature().value().config();
        this.discardChanceOnAirExposure = oreConfig.discardChanceOnAirExposure;

        oreConfig.targetStates.forEach(targetBlockState -> {
            var target = targetBlockState.target;
            if (target instanceof TagMatchTest tagMatchTest) {
                var tag = ((TagMatchTestAccessor) tagMatchTest).getTag();
                List<EmiIngredient> l = BuiltInRegistries.BLOCK.getOrCreateTag(tag).stream()
                        .map(Holder::value)
                        .map(EmiStack::of)
                        .collect(Collectors.toList());
                inputs.add(EmiIngredient.of(l));
            } else if (target instanceof BlockMatchTest blockMatchTest) {
                var block = ((BlockMatchTestAccessor) blockMatchTest).getBlock();
                inputs.add(EmiStack.of(block));
            } else if (target instanceof BlockStateMatchTest blockStateMatchTest) {
                var state = ((BlockStateMatchTestAccessor) blockStateMatchTest).getBlockState();
                inputs.add(EmiStack.of(state.getBlock()));
            } else if (target instanceof RandomBlockMatchTest randomBlockMatchTest) {
                RandomBlockMatchTestAccessor accessor = (RandomBlockMatchTestAccessor) randomBlockMatchTest;
                var block = accessor.getBlock();
                var probability = accessor.getProbability();
                inputs.add(EmiStack.of(block).setChance(probability));
            } else if (target instanceof RandomBlockStateMatchTest randomBlockStateMatchTest) {
                RandomBlockStateMatchTestAccessor accessor = (RandomBlockStateMatchTestAccessor) randomBlockStateMatchTest;
                var block = accessor.getBlockState().getBlock();
                var probability = accessor.getProbability();
                inputs.add(EmiStack.of(block).setChance(probability));
            } else {
                inputs.add(EmiStack.EMPTY);
            }

            outputs.add(EmiStack.of(targetBlockState.state.getBlock()).setAmount(oreConfig.size));
        });
        this.inputs = Collections.unmodifiableList(inputs);
        this.outputs = Collections.unmodifiableList(outputs);

        HeightProvider heightProvider = null;
        List<Biome> biomes = List.of();
        int countMin = -1;
        int countMax = -1;
        int rarityChance = -1;
        for (PlacementModifier modifier : feature.placement()) {
            if (modifier instanceof HeightRangePlacement heightRange) {
                heightProvider = ((HeightRangePlacementAccessor) heightRange).getHeight();
            } else if (modifier instanceof BiomeFilter) {
                biomes = getBiomes(id, feature);
            } else if (modifier instanceof CountPlacement countPlacement) {
                IntProvider countIntProvider = ((CountPlacementAccessor) countPlacement).getCount();
                countMin = countIntProvider.getMinValue();
                countMax = countIntProvider.getMaxValue();
            } else if (modifier instanceof RarityFilter rarityFilter) {
                rarityChance = ((RarityFilterAccessor) rarityFilter).getChance();
            }
        }

        if (feature.feature().value().feature() instanceof ScatteredOreFeature) {
            countMin = countMax = 1; // special handling, only used by ancient debris
        }

        this.countMin = countMin;
        this.countMax = countMax;
        this.rarityChance = rarityChance;
        this.heightProvider = heightProvider;
        this.biomes = EmiIngredient.of(biomes.stream().map(BiomeEmiStack::new).toList());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiOresRecipeCategories.OREGEN;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return Math.max(54, 18 + 18 * inputs.size());
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < inputs.size(); i++) {
            widgets.addSlot(inputs.get(i), 0, 18 + i * 18);
            widgets.addTexture(EmiTexture.EMPTY_ARROW, 20, 18 + i * 18);
            widgets.addSlot(outputs.get(i), 46, 18 + i * 18).recipeContext(this);
        }

        addDistributionGraph(widgets, 64, 0, heightProvider);

        if (!biomes.isEmpty())
            widgets.addSlot(biomes, 96, 18);

        if (discardChanceOnAirExposure > 0)
            widgets.addSlot(EmiStack.of(Items.BARRIER).setChance(discardChanceOnAirExposure), 142, 18)
                    .appendTooltip(Component.translatable("emi_ores.discard_on_air_chance"));

        Component veinFreq = getVeinFreqComponent(countMin, countMax, rarityChance);
        if (veinFreq != null) {
            widgets.addText(veinFreq, 160, 45, 0, false)
                    .horizontalAlign(TextWidget.Alignment.END)
                    .verticalAlign(TextWidget.Alignment.CENTER);
        }
    }

}
