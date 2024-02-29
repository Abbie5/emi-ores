package cc.abbie.emi_ores.mixin.accessor;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RandomBlockMatchTest.class)
public interface RandomBlockMatchTestAccessor {
    @Accessor
    Block getBlock();

    @Accessor
    float getProbability();
}
