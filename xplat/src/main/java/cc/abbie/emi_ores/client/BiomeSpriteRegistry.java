package cc.abbie.emi_ores.client;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import cc.abbie.emi_ores.EmiOres;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public class BiomeSpriteRegistry {
    private static Set<ResourceLocation> biomeSprites = Set.of();

    public static void registerSprites(Consumer<ResourceLocation> registerer) {
        biomeSprites
                .stream()
                .map(id -> new ResourceLocation(id.getNamespace(), id.getPath().substring(9, id.getPath().length() - 4))) // remove leading 'textures/' and trailing '.png'
                .forEach(registerer);
    }

    public static class ReloadListener implements PreparableReloadListener {
        public ResourceLocation getId() {
            return EmiOres.id("scan_biome_icons");
        }

        @Override
        public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            return CompletableFuture.runAsync(() -> {
                biomeSprites = resourceManager.listResources("textures/emi_ores/biome_icon", i -> i.getPath().endsWith(".png")).keySet();
            }, backgroundExecutor).thenCompose(preparationBarrier::wait);
        }
    }
}
