package cc.abbie.emi_ores;

import cc.abbie.emi_ores.networking.FeaturesSender;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.ResourceLocation;

public class EmiOres implements ModInitializer {
    public static final String MODID = "emi_ores";

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(FeaturesSender::onSyncDataPackContents);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }
}
