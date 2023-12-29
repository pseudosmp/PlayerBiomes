package com.pseudoforce.PlayerBiomes;

import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;


public class PlayerBiomes extends JavaPlugin {

    public static String formatBiome(FetchBiome.Pair biomePair) {
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
    }

    @Override
    public void onEnable() {
        
        // bstats, enabled by default 
        if (getConfig().getBoolean("bstats_consent", true)) {
            int pluginId = 17782;
            Metrics metrics = new Metrics(this, pluginId);
        }

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
                    return formatBiome(biomePair);
                } else {
                    return null;
                }
            });
        } else {
            getLogger().warning("PlaceholderAPI is not available and thus placeholders will not be registered");
        }

        // Save the default config if it doesn't exist
        this.saveDefaultConfig();

        // Register the /whereami command
        this.getCommand("whereami").setExecutor(new WhereAmICommand(this));
        getLogger().info("/whereami is now a valid question! (PlayerBiomes has been enabled)");
    }
    public class WhereAmICommand implements CommandExecutor {
        private final JavaPlugin plugin;
        public WhereAmICommand(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                String message = plugin.getConfig().getString("messages.console_whereami");
                if (message == null) {
                    getLogger().warning("The console message for /whereami is blank. Check your config!");
                    message = "This command can only be executed by a player.";
                }
                sender.sendMessage(message);
                return true;
            }

            Player player = (Player) sender;
            FetchBiome.Pair biomePair = FetchBiome.getBiomeName(player.getPlayer().getLocation());
            String formattedBiome = formatBiome(biomePair);

            // Get the message from the config and send
            String message = plugin.getConfig().getString("messages.user_whereami");
            if (message == null) {
                getLogger().warning("The user message for /whereami is blank. Check your config!");
                message = "You are currently in the %s biome.";
            }
            player.sendMessage(String.format(message, formattedBiome));
            return true;
        }
    }
}