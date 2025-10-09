package com.pseudosmp.playerbiomes;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;

import com.pseudosmp.playerbiomes.nms.Handler;

public class BiomeUtils {

    private static ConfigUtils config = PlayerBiomes.config;

    public static NamespacedKey getPlayerBiomeKey(OfflinePlayer player) {
        return Handler.getPlayerBiomeKey(player);
    }

    public static String getBiomeFormatted(OfflinePlayer player) {
        NamespacedKey key = getPlayerBiomeKey(player);
        String biomeNamespace = key.getNamespace();
        String locale;
        if (config.forceServerLocale) {
            locale = config.serverLocale;
        } else {
            locale = player.getPlayer() != null ? player.getPlayer().getLocale() : "en_us";
        }
        if (config.localeCaseInsensitive) {
            locale = locale.toLowerCase();
        }
        String translation = config.getBiomeTranslation(key, locale);
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

    // Technically the above function falls back to the original method by default, this is here to return the placeholder
    public static String getBiomeNameFallback(OfflinePlayer player) {
        String formattedBiome = getBiomeFormattedFallback(player);
        int nameIndex = formattedBiome.indexOf(":") + 2;
        return formattedBiome.substring(nameIndex);
    }
}
