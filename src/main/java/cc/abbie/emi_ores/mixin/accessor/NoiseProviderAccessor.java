package cc.abbie.emi_ores.mixin.accessor;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.NoiseProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(NoiseProvider.class)
public interface NoiseProviderAccessor {
    @Accessor
    List<BlockState> getStates();
}
