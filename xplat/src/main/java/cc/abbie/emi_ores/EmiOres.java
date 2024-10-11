package cc.abbie.emi_ores;

import net.minecraft.resources.ResourceLocation;

public class EmiOres {
    public static final String MODID = "emi_ores";

    public static void init() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
