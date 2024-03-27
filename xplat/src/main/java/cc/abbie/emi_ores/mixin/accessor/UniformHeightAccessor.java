package cc.abbie.emi_ores.mixin.accessor;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(UniformHeight.class)
public interface UniformHeightAccessor {
    @Accessor
    VerticalAnchor getMaxInclusive();

    @Accessor
    VerticalAnchor getMinInclusive();
}
