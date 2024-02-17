package cc.abbie.emi_ores.mixin.accessor;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleStateProvider.class)
public interface SimpleStateProviderAccessor {
    @Accessor
    BlockState getState();
}
