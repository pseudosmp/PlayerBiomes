package com.pseudosmp.playerbiomes;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bstats.bukkit.Metrics;


public class PlayerBiomes extends JavaPlugin {
    public static ConfigUtils config;

    public static PlayerBiomes getInstance() {
        return JavaPlugin.getPlugin(PlayerBiomes.class);
    }

    @Override
    public void onEnable() {
        config = new ConfigUtils(this);
        config.load();
        if (config.bstatsConsent) {
            int pluginId = 17782;
            @SuppressWarnings("unused")
            Metrics metrics = new Metrics(this, pluginId);
            getLogger().info("bstats for PlayerBiomes has been enabled. You can opt-out by disabling bstats in the plugin config.");
        }
        
        if (config.placeholderApiLoaded) {
            new PlaceholderAPIHandler(this).register();
        } else {
            getLogger().warning("PlaceholderAPI is not available and thus placeholders will not be registered");
        }

        this.saveDefaultConfig();
        this.getCommand("whatbiome").setExecutor(new WhatBiomeCommand(this));
        this.getCommand("playerbiomes").setExecutor(new PlayerBiomesCommand(this));
        getLogger().info("/whatbiome is now a valid question! (PlayerBiomes has been enabled)");
    }

    private void sendUsage(CommandSender sender) {
        boolean canReload = sender.hasPermission("playerbiomes.command.reload");
        String usage = canReload
            ? "[PlayerBiomes] Usage: /whatbiome | /playerbiomes reload"
            : "[PlayerBiomes] Usage: /whatbiome";
        sender.sendMessage(usage);
    }

    
    public class WhatBiomeCommand implements CommandExecutor {
        public WhatBiomeCommand(JavaPlugin plugin) {}

        private String parseDefaultPlaceholders(String message, OfflinePlayer player) {
            return message
                .replace("{biome_formatted}", BiomeUtils.getBiomeFormatted(player))
                .replace("{biome_name}", BiomeUtils.getBiomeName(player))
                .replace("{biome_namespace}", BiomeUtils.getBiomeNamespace(player))
                .replace("{biome_raw}", BiomeUtils.getPlayerBiomeKey(player).toString());
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                String message = config.getMessage("console_whatbiome");
                sender.sendMessage(message);
                return true;
            }

            if (args.length == 0) {
                Player player = (Player) sender;
                String message = config.getMessage("user_whatbiome");

                message = parseDefaultPlaceholders(message, player);

                // Parse PAPI placeholders
                if (config.placeholderApiLoaded) {
                    message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
                } 

                player.sendMessage(message);
                return true;
            }

            // Any other argument: show usage
            sendUsage(sender);
            return true;
        }
    }


    public class PlayerBiomesCommand implements CommandExecutor {
        public PlayerBiomesCommand(JavaPlugin plugin) {}

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("playerbiomes.command.reload")) {
                    try {
                        config.load();
                        if (sender instanceof Player) sender.sendMessage("[PlayerBiomes] PlayerBiomes configuration reloaded.");
                        getLogger().info("PlayerBiomes configuration reloaded.");
                    } catch (Exception e) {
                        if (sender instanceof Player) sender.sendMessage("[PlayerBiomes] Failed to reload configuration.");
                        getLogger().severe("Failed to reload PlayerBiomes configuration: " + e.getMessage());
                    }
                } else {
                    sendUsage(sender);
                }
                return true;
            }

            if ((args.length == 0) && (sender instanceof Player)) {
                Bukkit.dispatchCommand(sender, "whatbiome");
                return true;
            }

            // Any other argument: show usage
            sendUsage(sender);
            return true;
        }
    }
}