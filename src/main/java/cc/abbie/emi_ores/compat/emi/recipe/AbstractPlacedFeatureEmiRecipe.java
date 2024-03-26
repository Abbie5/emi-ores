package cc.abbie.emi_ores.compat.emi.recipe;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.mixin.accessor.TrapezoidHeightAccessor;
import cc.abbie.emi_ores.mixin.accessor.UniformHeightAccessor;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.ArrayList;
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
            int offset = -belowTop.offset();
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

    protected static Component anchorTextLong(VerticalAnchor anchor) {
        return anchorTextLongInner(anchor).withStyle(ChatFormatting.WHITE);
    }

    private static MutableComponent anchorTextLongInner(VerticalAnchor anchor) {
        if (anchor instanceof VerticalAnchor.Absolute absolute) {
            return Component.literal(String.valueOf(absolute.y()));
        } else if (anchor instanceof VerticalAnchor.AboveBottom aboveBottom) {
            int offset = aboveBottom.offset();
            if (offset == 0) {
                return Component.translatable("emi_ores.distribution.anchor.bottom");
            } else if (offset > 0) {
                return Component.translatable("emi_ores.distribution.anchor.above_bottom", offset);
            } else {
                return Component.translatable("emi_ores.distribution.anchor.below_bottom", -offset);
            }
        } else if (anchor instanceof VerticalAnchor.BelowTop belowTop) {
            int offset = -belowTop.offset();
            if (offset == 0) {
                return Component.translatable("emi_ores.distribution.anchor.top");
            } else if (offset > 0) {
                return Component.translatable("emi_ores.distribution.anchor.above_top", offset);
            } else {
                return Component.translatable("emi_ores.distribution.anchor.below_top", -offset);
            }
        } else {
            throw new RuntimeException();
        }
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
        if (heightProvider == null) return;

        HeightProviderType type;
        VerticalAnchor min, max, midLow, midHigh;

        if (heightProvider instanceof UniformHeight uniform) {
            type = HeightProviderType.UNIFORM;
            UniformHeightAccessor accessor = (UniformHeightAccessor) uniform;
            min = accessor.getMinInclusive();
            max = accessor.getMaxInclusive();
            midLow = midHigh = null;
        } else if (heightProvider instanceof TrapezoidHeight trapezoid) {
            TrapezoidHeightAccessor accessor = (TrapezoidHeightAccessor) trapezoid;
            min = accessor.getMinInclusive();
            max = accessor.getMaxInclusive();

            int plateau = accessor.getPlateau();

            // if the min and max are the same type, we can calculate the y-level with the highest frequency
            if (min instanceof VerticalAnchor.Absolute minAbs && max instanceof VerticalAnchor.Absolute maxAbs) {
                midLow = VerticalAnchor.absolute((minAbs.y() + maxAbs.y() - plateau) / 2);
                midHigh = VerticalAnchor.absolute((minAbs.y() + maxAbs.y() + plateau) / 2);
            } else if (min instanceof VerticalAnchor.AboveBottom minBot && max instanceof VerticalAnchor.AboveBottom maxBot) {
                midLow = VerticalAnchor.aboveBottom((minBot.offset() + maxBot.offset() - plateau) / 2);
                midHigh = VerticalAnchor.aboveBottom((minBot.offset() + maxBot.offset() + plateau) / 2);
            } else if (min instanceof VerticalAnchor.BelowTop minTop && max instanceof VerticalAnchor.BelowTop maxTop) {
                midLow = VerticalAnchor.belowTop((minTop.offset() + maxTop.offset() - plateau) / 2);
                midHigh = VerticalAnchor.belowTop((minTop.offset() + maxTop.offset() + plateau) / 2);
            } else {
                midLow = midHigh = null;
            }

            if (plateau == 0) {
                type = HeightProviderType.TRIANGULAR;

                if (midLow != null) {
                    widgets.addText(anchorText(midLow), 80, 8, 0, false)
                            .verticalAlign(TextWidget.Alignment.CENTER)
                            .horizontalAlign(TextWidget.Alignment.CENTER);
                }
            } else {
                type = HeightProviderType.TRAPEZOID;
            }
        } else {
            type = null;
            min = max = midLow = midHigh = null;
        }

        if (type != null && min != null && max != null) {
            widgets.addTexture(DISTRIBUTION, x, y, 32, 16, 0, type.v)
                    .tooltipText(getDistributionGraphTooltip(type, min, max, midLow, midHigh));
            widgets.addText(anchorText(min), x, y+8, 0, false)
                    .verticalAlign(TextWidget.Alignment.CENTER)
                    .horizontalAlign(TextWidget.Alignment.END);
            widgets.addText(anchorText(max), x+32, y+8, 0, false)
                    .verticalAlign(TextWidget.Alignment.CENTER)
                    .horizontalAlign(TextWidget.Alignment.START);
        }
    }

    protected static List<Component> getDistributionGraphTooltip(HeightProviderType type, VerticalAnchor min, VerticalAnchor max, VerticalAnchor midLow, VerticalAnchor midHigh) {
        List<Component> tooltip = new ArrayList<>();

        tooltip.add(type.name);
        tooltip.add(Component.translatable("emi_ores.distribution.range", anchorTextLong(min), anchorTextLong(max)).withStyle(ChatFormatting.GRAY));
        if (midLow != null && midHigh != null) {
            if (midLow.equals(midHigh)) {
                tooltip.add(Component.translatable("emi_ores.distribution.middle", anchorTextLong(midLow)).withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("emi_ores.distribution.middle_range", anchorTextLong(midLow), anchorTextLong(midHigh)).withStyle(ChatFormatting.GRAY));
            }
        }
        return tooltip;
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

    protected enum HeightProviderType {
        UNIFORM(0, Component.translatable("emi_ores.distribution.uniform").withStyle(ChatFormatting.BLUE)),
        TRIANGULAR(16, Component.translatable("emi_ores.distribution.triangle").withStyle(ChatFormatting.GREEN)),
        TRAPEZOID(32, Component.translatable("emi_ores.distribution.trapezoid").withStyle(ChatFormatting.RED));

        public final int v;
        public final Component name;

        HeightProviderType(int v, Component name) {
            this.v = v;
            this.name = name;
        }
    }
}
