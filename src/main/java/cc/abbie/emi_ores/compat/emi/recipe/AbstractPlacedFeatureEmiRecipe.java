package cc.abbie.emi_ores.compat.emi.recipe;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.mixin.accessor.TrapezoidHeightAccessor;
import cc.abbie.emi_ores.mixin.accessor.UniformHeightAccessor;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public abstract class AbstractPlacedFeatureEmiRecipe implements EmiRecipe {

    private static final ResourceLocation DISTRIBUTION = EmiOres.id("textures/gui/distribution.png");

    protected static Component anchorText(VerticalAnchor anchor) {
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

    protected static List<Biome> getBiomes(ResourceLocation id, PlacedFeature feature) {
        return Minecraft.getInstance().level.registryAccess()
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
    }

    protected static void addDistributionGraph(WidgetHolder widgets, int x, int y, HeightProvider heightProvider) {
        if (heightProvider != null) {
            int v;
            VerticalAnchor min;
            VerticalAnchor max;

            if (heightProvider instanceof UniformHeight uniform) {
                v = 0;
                UniformHeightAccessor accessor = (UniformHeightAccessor) uniform;
                min = accessor.getMinInclusive();
                max = accessor.getMaxInclusive();
            } else if (heightProvider instanceof TrapezoidHeight trapezoid) {
                TrapezoidHeightAccessor accessor = (TrapezoidHeightAccessor) trapezoid;
                min = accessor.getMinInclusive();
                max = accessor.getMaxInclusive();

                if (accessor.getPlateau() == 0) {
                    v = 16;

                    // if the min and max are the same type, we can calculate the y-level with the highest frequency
                    VerticalAnchor mid;
                    if (min instanceof VerticalAnchor.Absolute minAbs && max instanceof VerticalAnchor.Absolute maxAbs) {
                        mid = VerticalAnchor.absolute((minAbs.y() + maxAbs.y()) / 2);
                    } else if (min instanceof VerticalAnchor.AboveBottom minBot && max instanceof VerticalAnchor.AboveBottom maxBot) {
                        mid = VerticalAnchor.aboveBottom((minBot.offset() + maxBot.offset()) / 2);
                    } else if (min instanceof VerticalAnchor.BelowTop minTop && max instanceof VerticalAnchor.BelowTop maxTop) {
                        mid = VerticalAnchor.belowTop((minTop.offset() + maxTop.offset()) / 2);
                    } else {
                        mid = null;
                    }

                    if (mid != null) {
                        widgets.addText(anchorText(mid), 80, 8, 0, false)
                                .verticalAlign(TextWidget.Alignment.CENTER)
                                .horizontalAlign(TextWidget.Alignment.CENTER);
                    }
                } else {
                    v = 32;
                }
            } else {
                v = -1;
                min = null;
                max = null;
            }

            if (v != -1 && min != null && max != null) {
                widgets.addTexture(DISTRIBUTION, x, y, 32, 16, 0, v);
                widgets.addText(anchorText(min), x, y+8, 0, false)
                        .verticalAlign(TextWidget.Alignment.CENTER)
                        .horizontalAlign(TextWidget.Alignment.END);
                widgets.addText(anchorText(max), x+32, y+8, 0, false)
                        .verticalAlign(TextWidget.Alignment.CENTER)
                        .horizontalAlign(TextWidget.Alignment.START);
            }
        }
    }

    protected static Component getVeinFreqComponent(int countMin, int countMax, int rarityChance) {
        Component veinFreq;
        if (countMin != -1 && countMax != -1) {
            if (countMin == countMax) {
                veinFreq = Component.translatable("emi_ores.veins_per_chunk", countMin);
            } else {
                veinFreq = Component.translatable("emi_ores.veins_per_chunk_range", countMin, countMax);
            }
        } else if (rarityChance != -1) {
            veinFreq = Component.translatable("emi_ores.rarity_chance", rarityChance);
        } else {
            veinFreq = null;
        }
        return veinFreq;
    }
}
