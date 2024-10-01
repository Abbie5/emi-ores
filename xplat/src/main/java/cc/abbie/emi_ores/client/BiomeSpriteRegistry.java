package cc.abbie.emi_ores.client;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class BiomeSpriteRegistry {
    public static void registerSprites(Consumer<ResourceLocation> registerer) {
        BuiltinRegistries.BIOME.keySet()
                .stream()
                .map(id -> new ResourceLocation(id.getNamespace(), "emi_ores/biome_icon/" + id.getPath()))
                .forEach(registerer);
    }
}
