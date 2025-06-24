package com.pseudosmp.PlayerBiomes;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHandler extends PlaceholderExpansion {

    private final PlayerBiomes plugin;

    public PlaceholderAPIHandler(PlayerBiomes plugin) {
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
                return BiomeUtils.getPlayerBiomeKey(player).toString();
            case "biome_namespace":
                return BiomeUtils.getBiomeNamespace(player);
            case "biome_name":
                return BiomeUtils.getBiomeName(player);
            case "biome_formatted":
                return BiomeUtils.getBiomeFormatted(player);
            // forcefully get from fallback method
            case "biome_name_english":
                return BiomeUtils.getBiomeNameFallback(player);
            case "biome_formatted_english":
                return BiomeUtils.getBiomeFormattedFallback(player);
            default:
                return null;
        }
    }
}
