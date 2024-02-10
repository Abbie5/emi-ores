package cc.abbie.emi_ores.compat.emi.recipe;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.compat.emi.EmiOresRecipeCategories;
import cc.abbie.emi_ores.compat.emi.stack.BiomeEmiStack;
import cc.abbie.emi_ores.mixin.accessor.*;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlacedFeatureEmiRecipe implements EmiRecipe {
    private static final ResourceLocation DISTRIBUTION = EmiOres.id("textures/gui/distribution.png");

    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    private final HeightProvider heightProvider;
    private final EmiIngredient biomes;
    private final float discardChanceOnAirExposure;
    private final int countMin;
    private final int countMax;
    private final int rarityChance;

    public PlacedFeatureEmiRecipe(PlacedFeature feature, ResourceLocation id) {
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
                biomes = Minecraft.getInstance().level.registryAccess()
                        .registryOrThrow(Registries.BIOME)
                        .stream()
                        .filter(biome -> {
                            var features = biome.getGenerationSettings().features();
                            for (HolderSet<PlacedFeature> holderSet : features) {
                                for (Holder<PlacedFeature> holder : holderSet) {
                                    if (holder.unwrap().map(
                                            resourceKey -> resourceKey.location().equals(id),
                                            placedFeature -> placedFeature.equals(feature)
                                    )) return true;
                                }
                            }
                            return false;
                        }).toList();
            } else if (modifier instanceof CountPlacement countPlacement) {
                IntProvider countIntProvider = ((CountPlacementAccessor) countPlacement).getCount();
                countMin = countIntProvider.getMinValue();
                countMax = countIntProvider.getMaxValue();
            } else if (modifier instanceof RarityFilter rarityFilter) {
                rarityChance = ((RarityFilterAccessor) rarityFilter).getChance();
            }
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

        if (heightProvider != null) {
            if (heightProvider instanceof UniformHeight uniform) {
                UniformHeightAccessor accessor = (UniformHeightAccessor) uniform;
                widgets.addTexture(DISTRIBUTION, 64, 0, 32, 16, 0, 0);
                widgets.addText(anchorText(accessor.getMinInclusive()), 64, 8, 0, false)
                        .verticalAlign(TextWidget.Alignment.CENTER)
                        .horizontalAlign(TextWidget.Alignment.END);
                widgets.addText(anchorText(accessor.getMaxInclusive()), 96, 8, 0, false)
                        .verticalAlign(TextWidget.Alignment.CENTER)
                        .horizontalAlign(TextWidget.Alignment.START);
            } else if (heightProvider instanceof TrapezoidHeight trapezoid) {
                TrapezoidHeightAccessor accessor = (TrapezoidHeightAccessor) trapezoid;
                if (accessor.getPlateau() == 0) {
                    widgets.addTexture(DISTRIBUTION, 64, 0, 32, 16, 0, 16);
                } else {
                    widgets.addTexture(DISTRIBUTION, 64, 0, 32, 16, 0, 32);
                    widgets.addText(Component.literal("<"+accessor.getPlateau()+">"), 80, 0, 0, false)
                            .verticalAlign(TextWidget.Alignment.CENTER)
                            .horizontalAlign(TextWidget.Alignment.CENTER);
                }
                widgets.addText(anchorText(accessor.getMinInclusive()), 64, 8, 0, false)
                        .verticalAlign(TextWidget.Alignment.CENTER)
                        .horizontalAlign(TextWidget.Alignment.END);
                widgets.addText(anchorText(accessor.getMaxInclusive()), 96, 8, 0, false)
                        .verticalAlign(TextWidget.Alignment.CENTER)
                        .horizontalAlign(TextWidget.Alignment.START);
            }
        }

        if (!biomes.isEmpty())
            widgets.addSlot(biomes, 96, 18);

        widgets.addSlot(EmiStack.of(Items.BARRIER).setChance(discardChanceOnAirExposure), 142, 18);

        if (countMin != -1 && countMax != -1) {
            if (countMin == countMax) {
                widgets.addText(Component.translatable("emi_ores.veins_per_chunk", countMin), 160, 45, 0, false)
                        .horizontalAlign(TextWidget.Alignment.END)
                        .verticalAlign(TextWidget.Alignment.CENTER);
            } else {
                widgets.addText(Component.translatable("emi_ores.veins_per_chunk_range", countMin, countMax), 160, 45, 0, false)
                        .horizontalAlign(TextWidget.Alignment.END)
                        .verticalAlign(TextWidget.Alignment.CENTER);
            }
        } else if (rarityChance != -1) {
            widgets.addText(Component.translatable("emi_ores.rarity_chance", rarityChance), 160, 45, 0, false)
                    .horizontalAlign(TextWidget.Alignment.END)
                        .verticalAlign(TextWidget.Alignment.CENTER);
        }
    }

    private static Component anchorText(VerticalAnchor anchor) {
        String s;
        if (anchor instanceof VerticalAnchor.Absolute absolute) {
            s = String.valueOf(absolute.y());
        } else if (anchor instanceof VerticalAnchor.AboveBottom aboveBottom) {
            int offset = aboveBottom.offset();
            if (offset == 0) {
                s = "bot";
            } else if (offset > 0) {
                s = "bot+" + offset;
            } else {
                s = "bot" + offset;
            }
        } else if (anchor instanceof VerticalAnchor.BelowTop belowTop) {
            int offset = -1 * belowTop.offset();
            if (offset == 0) {
                s = "top";
            } else if (offset > 0) {
                s = "top+" + offset;
            } else {
                s = "top" + offset;
            }
        } else {
            throw new RuntimeException();
        }
        return Component.literal(s);
    }
}
