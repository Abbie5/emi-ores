package cc.abbie.emi_ores.platform.services;

public interface Platform {
    String getPlatformName();
    boolean isModLoaded(String modId);
    String getModName(String modId);
    boolean isDevelopmentEnvironment();
}
