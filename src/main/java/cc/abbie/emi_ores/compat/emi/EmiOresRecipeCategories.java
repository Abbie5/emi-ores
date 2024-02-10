package cc.abbie.emi_ores.compat.emi;

import cc.abbie.emi_ores.EmiOres;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.level.block.Blocks;

public class EmiOresRecipeCategories {
    public static final EmiRecipeCategory OREGEN = new EmiRecipeCategory(EmiOres.id("oregen"), EmiStack.of(Blocks.DEEPSLATE_DIAMOND_ORE));
}
