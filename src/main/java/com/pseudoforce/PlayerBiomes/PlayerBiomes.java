package com.pseudoforce.PlayerBiomes;

import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;


public class PlayerBiomes extends JavaPlugin {

    @Override
    public void onEnable() {
        // bstats
        int pluginId = 17782;
        Metrics metrics = new Metrics(this, pluginId);
        
        // placeholders
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PlaceholderAPIUtils.register("biome_raw", player -> {
                if(player.isOnline()) {
                    FetchBiome.Pair biome = FetchBiome.getBiomeName(player.getPlayer().getLocation());
                    return biome.namespace + ":" + biome.key;
                } else {
                    return null;
                }
            });

            PlaceholderAPIUtils.register("biome_namespace", player -> {
                if (player.isOnline()) {
                    String ns = FetchBiome.getBiomeName(player.getPlayer().getLocation()).namespace;
                    return ns.substring(0, 1).toUpperCase() + ns.substring(1);
                } else {
                    return null;
                }
            });

            PlaceholderAPIUtils.register("biome_name", player -> {
                if(player.isOnline()) {
                    String biome = FetchBiome.getBiomeName(player.getPlayer().getLocation()).key;
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
                    FetchBiome.Pair biomePair = FetchBiome.getBiomeName(player.getPlayer().getLocation());
                    String biomeKey = biomePair.key;
                    String biomeNamespace = biomePair.namespace;
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
        } else {
            getLogger().warning("PlaceholderAPI is not available and thus placeholders will not be registered");
        }

        // Register the /whereami command
        this.getCommand("whereami").setExecutor(new WhereAmICommand());
    }
    public class WhereAmICommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be run by a player.");
                return true;
            }

            Player player = (Player) sender;
            FetchBiome.Pair biome = FetchBiome.getBiomeName(player.getLocation());
            player.sendMessage("You are currently in the " + biome.key + " biome.");
            return true;
        }
    }
}