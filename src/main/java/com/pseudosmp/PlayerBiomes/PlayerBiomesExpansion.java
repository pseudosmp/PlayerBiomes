package com.pseudosmp.PlayerBiomes;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PlayerBiomesExpansion extends PlaceholderExpansion {

    private final PlayerBiomes plugin;

    public PlayerBiomesExpansion(PlayerBiomes plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "playerbiomes";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player == null || !player.isOnline()) return null;

        switch (identifier.toLowerCase()) {
            case "biome_raw":
                return PlayerBiomes.getPlayerBiomeKey(player).toString();
            case "biome_namespace": {
                String ns = PlayerBiomes.getPlayerBiomeKey(player).getNamespace();
                return ns.substring(0, 1).toUpperCase() + ns.substring(1);
            }
            case "biome_name": {
                String formattedBiome = PlayerBiomes.getBiomeFormatted(player);
                int nameIndex = formattedBiome.indexOf(":") + 2;
                return formattedBiome.substring(nameIndex);
            }
            case "biome_formatted":
                return PlayerBiomes.getBiomeFormatted(player);
            default:
                return null;
        }
    }
}
