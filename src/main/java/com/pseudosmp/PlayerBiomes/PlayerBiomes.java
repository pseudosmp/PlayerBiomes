package com.pseudosmp.PlayerBiomes;

import com.jeff_media.jefflib.*;
import com.jeff_media.jefflib.pluginhooks.PlaceholderAPIUtils;
import org.bukkit.plugin.java.JavaPlugin;


public class PlayerBiomes extends JavaPlugin {
    @Override
    public void onEnable() {
        System.out.println("Load Successful!");
        JeffLib.init(this);
        PlaceholderAPIUtils.register("biome_raw", player -> {
            if(player.isOnline()) {
                return BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getNamespace() + ":" + BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getKey();
            } else {
                return null;
            }
        });
        PlaceholderAPIUtils.register("biome_namespace", player -> {
            if(player.isOnline()) {
                String ns = BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getNamespace();
                return ns.substring(0, 1).toUpperCase() + ns.substring(1);
            } else {
                return null;
            }
        });
        PlaceholderAPIUtils.register("biome_name", player -> {
            if(player.isOnline()) {
                String n = BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getKey();
                return n.substring(0, 1).toUpperCase() + n.substring(1);
            } else {
                return null;
            }
        });
        // Credit to Si6gma#0828 for teaching me!
        PlaceholderAPIUtils.register("biome_formatted", player -> {
            if (player.isOnline()) {
                String biomeKey = BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getKey();
                String biomeNamespace = BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getNamespace();
                String biome = "";
                String formattedBiome = "";

                biome = biomeKey.replaceAll("_", " ");
                
                int findSlash = biomeKey.indexOf("/");

                if (findSlash != -1) {
                    do {
                        biome = biomeKey.substring(findSlash + 1);
                        findSlash = biomeKey.indexOf("/");
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
