package cc.abbie.emi_ores.fabric.platform;

import cc.abbie.emi_ores.platform.services.Platform;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatform implements Platform {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public String getModName(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).orElseThrow().getMetadata().getName();
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
