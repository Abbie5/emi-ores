package cc.abbie.emi_ores;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class Platform {
    @ExpectPlatform
    public static Path getConfigDir() {
        throw new AssertionError();
    }
}
