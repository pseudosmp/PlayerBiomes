package com.pseudosmp.PlayerBiomes;

import com.jeff_media.jefflib.*;
import com.jeff_media.jefflib.pluginhooks.PlaceholderAPIUtils;
import org.bukkit.plugin.java.JavaPlugin;


public class PlayerBiomes extends JavaPlugin {
    @Override
    public void onEnable() {
        JeffLib.init(this);
        PlaceholderAPIUtils.register("biome", player -> {
            if(player.isOnline()) {
                return BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()); // .getKey();
            } else {
                return null;
            }
        });
        // Credit to Si6gma#0828 for teaching me!
        PlaceholderAPIUtils.register("biome_formatted", player -> {
            if (player.isOnline()) {
                String biome = BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()); // .getKey();
                String capitalizeWord = "";

                biome = biome.replaceAll("_", " "); // Makes _ into spaces
                biome = biome.replaceAll(":", ": "); // Namespace Formatting
                int findSlash = biome.indexOf("/"); // Finds "/" to remove everything before it. (for terralith reserved biomes)

                if (findSlash != -1) {
                    do {
                        biome = biome.substring(findSlash + 1); // Removes "/" and everything before it. (for terralith reserved biomes)
                        findSlash = biome.indexOf("/"); // Finds more "/". (for terralith reserved biomes)
                    } while (findSlash != -1); // Untill no "/" remain this loop will continue. (for terralith reserved biomes)
                }

                String words[] = biome.split("\\s"); // Makes Biome string into arrays spliting betwee ever space ("\\s").

                // Following Loop Capitalizes All Words In Sentence
                for (String w : words) {
                    String first = w.substring(0, 1);
                    String afterfirst = w.substring(1);
                    capitalizeWord += first.toUpperCase() + afterfirst + " ";
                }
                return capitalizeWord.trim();
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
