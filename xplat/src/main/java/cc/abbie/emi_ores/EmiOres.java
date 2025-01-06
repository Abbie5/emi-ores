package cc.abbie.emi_ores;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmiOres {
    public static final String MODID = "emi_ores";
    public static final Logger LOG = LoggerFactory.getLogger("EMI Ores");

    public static void init() {
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }
}
