package cc.abbie.emi_ores.mixin.accessor;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagMatchTest.class)
public interface TagMatchTestAccessor {
    @Accessor
    TagKey<Block> getTag();
}
