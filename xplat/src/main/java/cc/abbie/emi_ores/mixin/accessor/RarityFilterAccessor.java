package cc.abbie.emi_ores.mixin.accessor;

import net.minecraft.world.level.levelgen.placement.RarityFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RarityFilter.class)
public interface RarityFilterAccessor {
    @Accessor
    int getChance();
}
