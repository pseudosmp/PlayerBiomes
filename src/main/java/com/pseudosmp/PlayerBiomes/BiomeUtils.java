package com.pseudosmp.PlayerBiomes;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

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
