package com.pseudosmp.PlayerBiomes;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ConfigUtils {
    private final PlayerBiomes plugin;
    private final Logger logger;

    // Persistent config values
    public String downloadUrl;
    public String serverLocale;
    public boolean bstatsConsent; 
    public boolean forceServerLocale;
    public boolean localeCaseInsensitive;
    public boolean autoDownloadLocale;

    private Map<String, Object> messages = Collections.emptyMap();
    private static final Map<String, Boolean> localeDownloadInProgress = new HashMap<>();
    private static final Map<String, Map<String, String>> localeCache = new HashMap<>();

    public ConfigUtils(PlayerBiomes plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        checkAndUpdateConfig();
    }

    public Boolean load() {
        try {
            plugin.reloadConfig();
            FileConfiguration config = plugin.getConfig();

            downloadUrl = config.getString("locale_download_url", "");
            serverLocale = config.getString("server_locale", "en_us");
            bstatsConsent = config.getBoolean("bstats_consent", true); // Default to true, can be disabled in config.yml
            forceServerLocale = config.getBoolean("force_server_locale", true);
            localeCaseInsensitive = config.getBoolean("locale_case_insensitive", true);
            autoDownloadLocale = config.getBoolean("auto_download_locale", false);
            messages = config.getConfigurationSection("messages").getValues(true);
            localeCache.clear(); // Clear cache on reload

            String user_whatbiome = getMessage("user_whatbiome");
            String defaultMessage = "[PlayerBiomes] You are currently in the biome - {biome_formatted}.";
            // Warn if no placeholders found
            if (
                !user_whatbiome.contains("{biome_formatted}") &&
                !user_whatbiome.contains("{biome_name}") &&
                !user_whatbiome.contains("{biome_namespace}") &&
                !user_whatbiome.contains("{biome_raw}")
            ) {
                plugin.getLogger().warning("No biome placeholder found in the player message. Please read instructions in the config properly!");
                // change messages.user_whatbiome to default
                messages.put("user_whatbiome", defaultMessage);
            }

            return true;
        } catch (Exception e) {
            logger.severe("Failed to load config.yml: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void checkAndUpdateConfig() {
        plugin.saveDefaultConfig();

        if (isOlderConfigVersion()) {
            logger.warning("Your config.yml is outdated! Please update it to the latest version.");
            try {
                File newConfigFile = new File(plugin.getDataFolder(), "config.new.yml");
                if (newConfigFile.exists()) {
                    newConfigFile.delete();
                }
                InputStream in = plugin.getResource("config.yml");
                if (in != null) {
                    java.nio.file.Files.copy(
                        in,
                        newConfigFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                    in.close();
                    logger.warning("You can find the latest config.yml in the plugin's folder as \"config.new.yml\".");
                } else {
                    logger.warning("Resource config.yml not found in jar.");
                }
            } catch (Exception e) {
                logger.warning("Failed to save config.new.yml: " + e.getMessage());
                logger.warning("Manually update by checking https://github.com/pseudosmp/PlayerBiomes/blob/main/src/main/resources/config.yml");
            }
        }
    }

    private boolean isOlderConfigVersion() {
        String configVersion = plugin.getConfig().getString("configVersion", "0.0.0");
        InputStream inputStream = plugin.getResource("config.yml");
        YamlConfiguration resourceConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
        String pluginVersion = resourceConfig.getString("configVersion", "0.0.0");

        String[] curr = configVersion.split("\\.");
        String[] target = pluginVersion.split("\\.");

        int len = Math.max(curr.length, target.length);
        for (int i = 0; i < len; i++) {
            int currPart = i < curr.length ? Integer.parseInt(curr[i]) : 0;
            int targetPart = i < target.length ? Integer.parseInt(target[i]) : 0;
            if (currPart < targetPart) return true;
            if (currPart > targetPart) return false;
        }
        return false;
    }

    public void downloadLocaleFile(String locale) {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) langDir.mkdirs();
        File localeFile = new File(langDir, locale + ".json");
        File tmpFile = new File(langDir, locale + ".json.tmp");
        if (localeFile.exists()) {
            return;
        }

        localeDownloadInProgress.put(locale, true);
        Bukkit.getScheduler().runTaskAsynchronously((Plugin) plugin, () -> {
            String urlTemplate = PlayerBiomes.config.downloadUrl;
            String version = plugin.getServer().getBukkitVersion().split("-")[0];
            if (version.endsWith(".0")) version = version.substring(0, version.length() - 2);
            String urlString = urlTemplate
                    .replace("{version}", version)
                    .replace("{locale}", locale);
            try {
                plugin.getLogger().info("Downloading locale file: " + urlString);
                try (BufferedInputStream in = new BufferedInputStream(new URL(urlString).openStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(tmpFile)) {
                    byte[] dataBuffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                    // Move .tmp to .json
                    if (localeFile.exists()) localeFile.delete();
                    if (!tmpFile.renameTo(localeFile)) {
                        plugin.getLogger().warning("Failed to rename " + tmpFile.getName() + " to " + localeFile.getName());
                        plugin.getLogger().warning("Please rename it manually!");
                        tmpFile.delete();
                    } else {
                        plugin.getLogger().info("Download complete! " + localeFile.getAbsolutePath());
                        localeCache.remove(locale); // Clear cache for this locale
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to download locale file: " + urlString);
                e.printStackTrace();
                plugin.getLogger().warning("Place the file in plugins/PlayerBiomes/lang/ manually!");
                if (tmpFile.exists()) tmpFile.delete();
            } finally {
                localeDownloadInProgress.remove(locale);
            }
        });
    }

    public String getBiomeTranslation(NamespacedKey key, String locale) {
        if (autoDownloadLocale) {
            downloadLocaleFile(locale);
            // Download in progress, check back later
            if (localeDownloadInProgress.getOrDefault(locale, false)) {
                return null;
            }
        }
        Map<String, String> translations = localeCache.computeIfAbsent(locale, l -> {
            File file = new File(plugin.getDataFolder(), "lang/" + l + ".json");
            if (!file.exists()) return null;
            try (FileReader reader = new FileReader(file)) {
                return new Gson().fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
        if (translations == null) {
            return null;
        }
        String translationKey = "biome." + key.getNamespace() + "." + key.getKey();
        return translations.get(translationKey);
    }

    public String getMessage(String key) {
        Object value = messages.get(key);
        return value != null ? value.toString() : null;
    }
}
