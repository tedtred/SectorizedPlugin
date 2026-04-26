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

        Path configDirectory = Paths.get("config/mods/config");
        Path configPath = configDirectory.resolve("config.json");
        Path discordConfigPath = configDirectory.resolve("discordConfig.json");
        Path dbUrlPath = configDirectory.resolve("dbUrl.json");

        ensureTemplateFile(configPath, defaultConfigTemplate(), "main config");
        ensureTemplateFile(discordConfigPath, defaultDiscordConfigTemplate(), "discord config");
        ensureTemplateFile(dbUrlPath, defaultDbUrlTemplate(), "database config");

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

    private static void ensureTemplateFile(Path configPath, String template, String name) {
        if (Files.exists(configPath)) return;

        try {
            Files.createDirectories(configPath.getParent());
            Files.write(configPath, template.getBytes(StandardCharsets.UTF_8));
            Log.info("Sectorized: @ not found - generated default template at @", name, configPath);
        } catch (IOException e) {
            Log.warn("Sectorized: could not write @ template: @", name, e.getMessage());
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

    private static String defaultDiscordConfigTemplate() {
        return "{" +
                "\n  \"_notes\": {" +
                "\n    \"token\": \"Discord bot token. Keep secret and never commit real tokens to git.\"," +
                "\n    \"guildID\": \"Discord server ID where Sectorized bot is installed.\"," +
                "\n    \"logChannelID\": \"Channel ID used for game event logs and restart notifications.\"," +
                "\n    \"hallOfFameChannelID\": \"Channel ID used for leaderboard/hall-of-fame updates.\"" +
                "\n  }," +
                "\n  \"token\": \"REPLACE_WITH_DISCORD_BOT_TOKEN\"," +
                "\n  \"guildID\": 0," +
                "\n  \"logChannelID\": 0," +
                "\n  \"hallOfFameChannelID\": 0" +
                "\n}\n";
    }

    private static String defaultDbUrlTemplate() {
        return "{" +
                "\n  \"_notes\": {" +
                "\n    \"url\": \"MariaDB JDBC URL, for example jdbc:mariadb://127.0.0.1:3306/sectorized\"," +
                "\n    \"user\": \"Database username used by Sectorized ranking persistence.\"," +
                "\n    \"password\": \"Database password for the configured user.\"" +
                "\n  }," +
                "\n  \"url\": \"jdbc:mariadb://127.0.0.1:3306/sectorized\"," +
                "\n  \"user\": \"sectorized\"," +
                "\n  \"password\": \"REPLACE_WITH_DB_PASSWORD\"" +
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
