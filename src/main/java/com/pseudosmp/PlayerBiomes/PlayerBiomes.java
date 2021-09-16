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
    }
    @Override
    public void onDisable(){
        System.out.println("Disabling PlayerBiomes...");
    }
}
