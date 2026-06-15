package cc.abbie.emi_ores.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.Platform;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public record EmiOresClientConfig(
        boolean addBiomesToIndex,
        boolean showHeightValuesForCurrentDimensionByDefault
) {
    public static Codec<EmiOresClientConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("add_biomes_to_index").forGetter(EmiOresClientConfig::addBiomesToIndex),
            Codec.BOOL.fieldOf("show_height_values_for_current_dimension_by_default").forGetter(EmiOresClientConfig::showHeightValuesForCurrentDimensionByDefault)
    ).apply(instance, EmiOresClientConfig::new));

    private static final EmiOresClientConfig DEFAULT = new EmiOresClientConfig(
            true,
            false
    );
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static EmiOresClientConfig INSTANCE;

    private static File getConfigFile() {
        return Platform.getConfigDir().resolve(EmiOres.MODID + "-client.json").toFile();
    }

    private static EmiOresClientConfig loadInner() {
        try (FileReader fileReader = new FileReader(getConfigFile())) {
            return CODEC.parse(JsonOps.INSTANCE, GSON.fromJson(fileReader, JsonElement.class))
                    .resultOrPartial(s -> {})
                    .orElse(DEFAULT);
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            return DEFAULT;
        }
    }

    public static void load() {
        INSTANCE = loadInner();
        save();
    }

    public static void save() {
        try (FileWriter fileWriter = new FileWriter(getConfigFile())) {
            getConfigFile().getParentFile().mkdirs();
            JsonElement jsonElement = CODEC.encodeStart(JsonOps.INSTANCE, INSTANCE).getOrThrow(false, s -> {});
            GSON.toJson(jsonElement, fileWriter);
        } catch (IOException | RuntimeException e) {
            EmiOres.LOG.error("Failed to save config", e);
        }
    }
}
