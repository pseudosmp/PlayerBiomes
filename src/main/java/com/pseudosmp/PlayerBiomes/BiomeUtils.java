package com.pseudosmp.PlayerBiomes;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;

public class BiomeUtils {
    private static Boolean biomeInterfaceCache = null;

    private static ConfigUtils config = PlayerBiomes.config;

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
}
