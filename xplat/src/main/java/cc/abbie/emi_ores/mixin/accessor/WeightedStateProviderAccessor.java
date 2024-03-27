package cc.abbie.emi_ores.mixin.accessor;

import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WeightedStateProvider.class)
public interface WeightedStateProviderAccessor {
    @Accessor
    SimpleWeightedRandomList<BlockState> getWeightedList();
}
