package com.pseudosmp.PlayerBiomes;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BiomeUtils {
    private static final Map<String, Map<String, String>> localeCache = new HashMap<>();

    private static Boolean biomeInterfaceCache = null;

    private static final JavaPlugin plugin = PlayerBiomes.getInstance();

    public static boolean isModernBiomeAPI() {
        if (biomeInterfaceCache != null) return biomeInterfaceCache;

        try {
            Class<?> biomeClass = Class.forName("org.bukkit.block.Biome");
            biomeInterfaceCache = biomeClass.isInterface();
            return biomeInterfaceCache;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean ensureLocaleFile(String locale, JavaPlugin plugin) {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) langDir.mkdirs();
        File localeFile = new File(langDir, locale + ".json");
        if (localeFile.exists()) return true;

        if (!plugin.getConfig().getBoolean("auto_download_locale", false)) {
            String urlTemplate = plugin.getConfig().getString("locale_download_url");
            String version = plugin.getServer().getBukkitVersion().split("-")[0]; // e.g., "1.20.4"
            String urlString = urlTemplate
                    .replace("{version}", version)
                    .replace("{locale}", locale);

            try (BufferedInputStream in = new BufferedInputStream(new URL(urlString).openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(localeFile)) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                plugin.getLogger().info("Downloading locale file: " + urlString);
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                plugin.getLogger().info("Download complete! " + localeFile.getAbsolutePath());
                localeCache.remove(locale); // Clear cache for this locale
                return true;
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to download locale file: " + urlString);
                e.printStackTrace();
                return false;
            }
        } else return false;
    }

    public static NamespacedKey getPlayerBiomeKey(OfflinePlayer player) {
        if (isModernBiomeAPI()) {
            try {
                Object biome = player.getPlayer().getLocation().getBlock().getClass()
                        .getMethod("getBiome")
                        .invoke(player.getPlayer().getLocation().getBlock());

                Object namespacedKeyObj = biome.getClass().getMethod("getKey").invoke(biome);
                return (NamespacedKey) namespacedKeyObj;
            } catch (Throwable t) {
                t.printStackTrace();
                return NamespacedKey.minecraft("unknown");
            }
        } else {
            // Use the original jefflib BiomeUtils for legacy
            return com.jeff_media.jefflib.BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation());
        }
    }

    public static String getBiomeTranslation(NamespacedKey key, String locale) {
        if (!ensureLocaleFile(locale, plugin)) return null;
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
        if (translations == null) return null;
        String translationKey = "biome." + key.getNamespace() + "." + key.getKey();
        return translations.get(translationKey);
    }

    public static String getBiomeFormatted(OfflinePlayer player) {
        NamespacedKey key = getPlayerBiomeKey(player);
        String biomeNamespace = key.getNamespace();
        String locale;
        if (PlayerBiomes.forceServerLocale) {
            locale = plugin.getConfig().getString("server_locale", "en_us");
        } else {
            locale = player.getPlayer() != null ? player.getPlayer().getLocale() : "en_us";
        }
        String translation = getBiomeTranslation(key, locale);
        if (translation != null) {
            return biomeNamespace.substring(0, 1).toUpperCase() + biomeNamespace.substring(1) + ": " + translation;
        } else {
            // fallback to getting from NamespacedKey
            return getBiomeFormattedFallback(player);
        }
    }

    public static String getBiomeFormattedFallback(OfflinePlayer player) {
        NamespacedKey namespacedKey = getPlayerBiomeKey(player);

        String biomeNamespace = namespacedKey.getNamespace();
        String biomeKey = namespacedKey.getKey();

        String biome = biomeKey.replaceAll("[_.]", " ");
        StringBuilder formattedBiome = new StringBuilder();

        int findSlash = biome.lastIndexOf("/");
        biome = biome.substring(findSlash + 1);

        String[] words = biome.split("\\s");
        for (String w : words) {
            formattedBiome.append(w.substring(0, 1).toUpperCase()).append(w.substring(1)).append(" ");
        }
        formattedBiome.insert(0, biomeNamespace.substring(0, 1).toUpperCase() + biomeNamespace.substring(1) + ": ");
        return formattedBiome.toString().trim();
    }

    public static String getBiomeNamespace(OfflinePlayer player) {
        String ns = getPlayerBiomeKey(player).getNamespace();
        return ns.substring(0, 1).toUpperCase() + ns.substring(1);
    }

    public static String getBiomeName(OfflinePlayer player) {
        String formattedBiome = getBiomeFormatted(player);
        int nameIndex = formattedBiome.indexOf(":") + 2;
        return formattedBiome.substring(nameIndex);
    }
}
