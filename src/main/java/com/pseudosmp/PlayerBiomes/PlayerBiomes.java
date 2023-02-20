package com.pseudosmp.PlayerBiomes;

import com.jeff_media.jefflib.*;
import com.jeff_media.jefflib.pluginhooks.PlaceholderAPIUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;


public class PlayerBiomes extends JavaPlugin {
    @Override
    public void onEnable() {
        System.out.println("Load Successful!");
        // bstats
        int pluginId = 17782;
        Metrics metrics = new Metrics(this, pluginId);
        
        // placeholders
        JeffLib.init(this);
        PlaceholderAPIUtils.register("biome_raw", player -> {
            if(player.isOnline()) {
                return BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getNamespace() + ":" + BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getKey();
            } else {
                return null;
            }
        });

        PlaceholderAPIUtils.register("biome_namespace", player -> {
            if (player.isOnline()) {
                String ns = BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getNamespace();
                return ns.substring(0, 1).toUpperCase() + ns.substring(1);
            } else {
                return null;
            }
        });

        PlaceholderAPIUtils.register("biome_name", player -> {
            if(player.isOnline()) {
                String biome = BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getKey();
                String n = "";

                biome = biome.replaceAll("_", " ");

                int findSlash = biome.indexOf("/");
                if (findSlash != -1) {
                    do {
                        biome = biome.substring(findSlash + 1);
                        findSlash = biome.indexOf("/");
                    } while (findSlash != -1);
                }

                String words[] = biome.split("\\s");
                for (String w : words) {
                    n += w.substring(0, 1).toUpperCase() + w.substring(1) + " ";
                }

                return n.trim();
            } else {
                return null;
            }
        });

        PlaceholderAPIUtils.register("biome_formatted", player -> {
            if (player.isOnline()) {
                String biomeKey = BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getKey();
                String biomeNamespace = BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getNamespace();
                String biome = "";
                String formattedBiome = "";

                biome = biomeKey.replaceAll("_", " ");
                
                int findSlash = biome.indexOf("/");

                if (findSlash != -1) {
                    do {
                        biome = biome.substring(findSlash + 1);
                        findSlash = biome.indexOf("/");
                    } while (findSlash != -1);
                }

                String words[] = biome.split("\\s");
                for (String w : words) {
                    formattedBiome += w.substring(0, 1).toUpperCase() + w.substring(1) + " ";
                }
                formattedBiome = biomeNamespace.substring(0, 1).toUpperCase() + biomeNamespace.substring(1) + ": " + formattedBiome;
                return formattedBiome.trim();
            } else {
                return null;
            }
        });
    }
    @Override
    public void onDisable() {
        System.out.println("Goodbye!");
    }
}
