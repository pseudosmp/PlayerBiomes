package com.pseudosmp.PlayerBiomes;

import de.jeff_media.jefflib.*;
import de.jeff_media.jefflib.pluginhooks.PlaceholderAPIUtils;
import org.bukkit.plugin.java.JavaPlugin;


public class PlayerBiomes extends JavaPlugin  {
    @Override
    public void onEnable() {
        JeffLib.init(this);
        PlaceholderAPIUtils.register("biome", player -> {
            if(player.isOnline()) {
                return BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getKey();
            } else {
                return null;
            }
        });
        PlaceholderAPIUtils.register("biome_capitalized", player -> {
            if (player.isOnline()) {
                String biome = BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getKey();
                int findSlash = biome.indexOf('/');

                // Checks if it has a '/', if yes it removes everything before it.
                // For Terralith reserved biomes, you dont need to worry about this unless any one of your biome names has a forward slash
                if (findSlash != -1) {
                    biome = biome.substring(findSlash + 1);
                    findSlash = biome.indexOf('/');
                    biome = biome.substring(findSlash + 1);
                } else {
                }

                // Capitalizes first letter in both words.
                int findUnderScore = biome.indexOf("_");
                biome = biome.substring(0, 1).toUpperCase() + biome.substring(1, findUnderScore)
                        + biome.substring(findUnderScore, findUnderScore + 2).toUpperCase()
                        + biome.substring(findUnderScore + 2);
                biome = biome.replace("_", " ");

                return biome;
            } else {
                return null;
            }
        });
    }
    @Override
    public void onDisable(){
        System.out.println("Goodbye!");
    }
}
