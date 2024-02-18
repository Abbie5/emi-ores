package cc.abbie.emi_ores.mixin.accessor;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockStateMatchTest.class)
public interface BlockStateMatchTestAccessor {
    @Accessor
    BlockState getBlockState();
}
