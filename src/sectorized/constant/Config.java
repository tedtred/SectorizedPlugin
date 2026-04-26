package sectorized.constant;

import arc.util.Log;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
    public static Config c;

    public final boolean databaseEnabled;
    public final boolean updateScoreDecay;
    public final boolean discordEnabled;
    public final boolean infiniteResources;
    public final boolean experimentalMapsEnabled;

    public Config(boolean databaseEnabled, boolean updateScoreDecay, boolean discordEnabled, boolean infiniteResources, boolean experimentalMapsEnabled) {
        this.databaseEnabled = databaseEnabled;
        this.updateScoreDecay = updateScoreDecay;
        this.discordEnabled = discordEnabled;
        this.infiniteResources = infiniteResources;
        this.experimentalMapsEnabled = experimentalMapsEnabled;
    }

    static {
        Config.c = new Config(false, false, false, false, false);

        Path configPath = Paths.get("config/mods/config/config.json");

        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(configPath.getParent());
                Files.write(configPath, defaultConfigTemplate().getBytes(StandardCharsets.UTF_8));
                Log.info("Sectorized: config file not found - generated default config at @", configPath);
            } catch (IOException e) {
                Log.warn("Sectorized: could not write default config file: @", e.getMessage());
            }
        } else {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                Config loadedConfig = new Gson().fromJson(reader, Config.class);
                if (loadedConfig != null) {
                    Config.c = loadedConfig;
                } else {
                    Log.warn("Sectorized config file was empty; using safe local defaults.");
                }
            } catch (IOException e) {
                Log.warn("Sectorized config file not found or unreadable; using safe local defaults.");
            }
        }
    }

    private static String defaultConfigTemplate() {
        return "{" +
                "\n  \"_notes\": {" +
                "\n    \"databaseEnabled\": \"Enable MariaDB-based ranking persistence. Keep false unless db config is set up.\"," +
                "\n    \"updateScoreDecay\": \"Enable periodic score decay during update cycles.\"," +
                "\n    \"discordEnabled\": \"Enable Discord bot integration. Requires additional Discord config and bot token.\"," +
                "\n    \"infiniteResources\": \"Enable infinite building resources; intended for local testing/admin scenarios only.\"," +
                "\n    \"experimentalMapsEnabled\": \"Allow Erekir maps in game rotation and biome vote. Keep false until Erekir support is fully verified.\"" +
                "\n  }," +
                "\n  \"databaseEnabled\": false," +
                "\n  \"updateScoreDecay\": false," +
                "\n  \"discordEnabled\": false," +
                "\n  \"infiniteResources\": false," +
                "\n  \"experimentalMapsEnabled\": false" +
                "\n}\n";
    }

    @Override
    public String toString() {
        return "Config{" +
                "databaseEnabled=" + databaseEnabled +
                ", updateScoreDecay=" + updateScoreDecay +
                ", discordEnabled=" + discordEnabled +
                ", infiniteResources=" + infiniteResources +
                ", experimentalMapsEnabled=" + experimentalMapsEnabled +
                '}';
    }
}
