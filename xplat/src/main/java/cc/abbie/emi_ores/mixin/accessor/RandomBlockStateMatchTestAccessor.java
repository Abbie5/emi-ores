package cc.abbie.emi_ores.mixin.accessor;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockStateMatchTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RandomBlockStateMatchTest.class)
public interface RandomBlockStateMatchTestAccessor {
    @Accessor
    BlockState getBlockState();

    @Accessor
    float getProbability();
}
