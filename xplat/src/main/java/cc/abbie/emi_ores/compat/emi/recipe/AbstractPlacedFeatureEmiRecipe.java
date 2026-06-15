package cc.abbie.emi_ores.compat.emi.recipe;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.client.FeaturesReciever;
import cc.abbie.emi_ores.client.config.EmiOresClientConfig;
import cc.abbie.emi_ores.mixin.accessor.TrapezoidHeightAccessor;
import cc.abbie.emi_ores.mixin.accessor.UniformHeightAccessor;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
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

    private static Component anchorText(VerticalAnchor anchor) {
        String s;
        if (anchor instanceof VerticalAnchor.Absolute absolute) {
            s = String.valueOf(absolute.y());
        } else if (anchor instanceof VerticalAnchor.AboveBottom aboveBottom) {
            int offset = aboveBottom.offset();
            if (useCurrentDimension()) {
                int height = Minecraft.getInstance().level.getMinBuildHeight() + offset;
                s = String.valueOf(height);
            } else {
                if (offset == 0) {
                    s = "bot";
                } else if (offset > 0) {
                    s = "bot+" + offset;
                } else {
                    s = "bot" + offset;
                }
            }
        } else if (anchor instanceof VerticalAnchor.BelowTop belowTop) {
            int offset = -belowTop.offset();
            if (useCurrentDimension()) {
                int height = Minecraft.getInstance().level.getMaxBuildHeight() + offset;
                s = String.valueOf(height);
            } else {
                if (offset == 0) {
                    s = "top";
                } else if (offset > 0) {
                    s = "top+" + offset;
                } else {
                    s = "top" + offset;
                }
            }
        } else {
            throw new RuntimeException();
        }
        return Component.literal(s);
    }

    private static Component anchorTextLong(VerticalAnchor anchor) {
        return anchorTextLongInner(anchor).withStyle(ChatFormatting.WHITE);
    }

    private static MutableComponent anchorTextLongInner(VerticalAnchor anchor) {
        if (anchor instanceof VerticalAnchor.Absolute absolute) {
            return Component.literal(String.valueOf(absolute.y()));
        } else if (anchor instanceof VerticalAnchor.AboveBottom aboveBottom) {
            int offset = aboveBottom.offset();
            if (useCurrentDimension()) {
                int height = Minecraft.getInstance().level.getMinBuildHeight() + offset;
                return Component.literal(String.valueOf(height));
            } else {
                if (offset == 0) {
                    return Component.translatable("emi_ores.distribution.anchor.bottom");
                } else if (offset > 0) {
                    return Component.translatable("emi_ores.distribution.anchor.above_bottom", offset);
                } else {
                    return Component.translatable("emi_ores.distribution.anchor.below_bottom", -offset);
                }
            }
        } else if (anchor instanceof VerticalAnchor.BelowTop belowTop) {
            int offset = -belowTop.offset();
            if (useCurrentDimension()) {
                int height = Minecraft.getInstance().level.getMaxBuildHeight() + offset;
                return Component.literal(String.valueOf(height));
            } else {
                if (offset == 0) {
                    return Component.translatable("emi_ores.distribution.anchor.top");
                } else if (offset > 0) {
                    return Component.translatable("emi_ores.distribution.anchor.above_top", offset);
                } else {
                    return Component.translatable("emi_ores.distribution.anchor.below_top", -offset);
                }
            }
        } else {
            throw new RuntimeException();
        }
    }

    protected static List<Biome> getBiomes(ResourceLocation id, PlacedFeature feature) {
        Registry<Biome> biomeRegistry = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.BIOME);
        return FeaturesReciever.getBiomes()
                .get(ResourceKey.create(Registries.PLACED_FEATURE, id))
                .stream()
                .map(biomeRegistry::get)
                .toList();
    }
    
    private static void addAnchorText(WidgetHolder widgets, VerticalAnchor anchor, int x, int y, TextWidget.Alignment verticalAlign, TextWidget.Alignment horizontalAlign) {
        Font font = Minecraft.getInstance().font;
        
        widgets.addDrawable(x, y, 0, 0, (gui, mouseX, mouseY, delta) -> {
            Component text = anchorText(anchor);
            int textWidth = font.width(text);
            int textHeight = font.lineHeight;
            int textX = switch (horizontalAlign) {
                case START -> 0;
                case CENTER -> -textWidth/2;
                case END -> -textWidth;
            };
            int textY = switch (verticalAlign) {
                case START -> 0;
                case CENTER -> -textHeight/2;
                case END -> -textHeight;
            };
            gui.drawString(Minecraft.getInstance().font, text, textX, textY, 0, false);
        });
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
            } else {
                type = HeightProviderType.TRAPEZOID;
            }
        } else {
            type = null;
            min = max = midLow = midHigh = null;
        }

        if (type != null && min != null && max != null) {
            widgets.addTexture(DISTRIBUTION, x, y, 32, 16, 0, type.v);
            widgets.addTooltip((mouseX, mouseY) -> getDistributionGraphTooltip(type, min, max, midLow, midHigh), x, y, 32, 16);
            addAnchorText(widgets, min, x, y+8, TextWidget.Alignment.CENTER, TextWidget.Alignment.END);
            addAnchorText(widgets, max, x+32, y+8, TextWidget.Alignment.CENTER, TextWidget.Alignment.START);
            if (type == HeightProviderType.TRIANGULAR && midLow != null) {
                addAnchorText(widgets, midLow, 80, 8, TextWidget.Alignment.CENTER, TextWidget.Alignment.CENTER);
            }
        }
    }

    private static List<ClientTooltipComponent> getDistributionGraphTooltip(HeightProviderType type, VerticalAnchor min, VerticalAnchor max, VerticalAnchor midLow, VerticalAnchor midHigh) {
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
        if (hasAnyRelative(min, max, midLow, midHigh)) {
            if (useCurrentDimension()) {
                tooltip.add(Component.translatable("emi_ores.distribution.dimension", Minecraft.getInstance().level.dimension().location()).withStyle(ChatFormatting.GRAY));
                if (!Screen.hasShiftDown()) tooltip.add(Component.translatable("emi_ores.distribution.shift.relative").withStyle(ChatFormatting.GRAY));
            } else if (!Screen.hasShiftDown()) {
                tooltip.add(Component.translatable("emi_ores.distribution.shift.dimension").withStyle(ChatFormatting.GRAY));
            }
        }
        return tooltip.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).toList();
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

    private static boolean useCurrentDimension() {
        return Screen.hasShiftDown() != EmiOresClientConfig.INSTANCE.showHeightValuesForCurrentDimensionByDefault();
    }
    
    private static boolean hasAnyRelative(VerticalAnchor... anchors) {
        for (VerticalAnchor anchor : anchors) {
            if (anchor instanceof VerticalAnchor.AboveBottom || anchor instanceof VerticalAnchor.BelowTop) {
                return true;
            }
        }
        return false;
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
