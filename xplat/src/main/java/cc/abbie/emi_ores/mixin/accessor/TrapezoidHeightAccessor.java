package cc.abbie.emi_ores.mixin.accessor;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrapezoidHeight.class)
public interface TrapezoidHeightAccessor {
    @Accessor("minInclusive")
    VerticalAnchor emi_ores$getMinInclusive();

    @Accessor("maxInclusive")
    VerticalAnchor emi_ores$getMaxInclusive();

    @Accessor("plateau")
    int emi_ores$getPlateau();
}
