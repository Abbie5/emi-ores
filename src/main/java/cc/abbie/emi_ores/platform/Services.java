package cc.abbie.emi_ores.platform;

import cc.abbie.emi_ores.platform.services.Platform;
import cc.abbie.emi_ores.platform.services.Server;

import java.util.ServiceLoader;

public class Services {
    public static final Platform PLATFORM = load(Platform.class);
    public static final Server SERVER = load(Server.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz).findFirst().orElseThrow();
    }
}
