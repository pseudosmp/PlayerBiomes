package com.pseudosmp.PlayerBiomes;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bstats.bukkit.Metrics;


public class PlayerBiomes extends JavaPlugin {

    private static boolean placeholderApiLoaded = false;
    public static boolean forceServerLocale = false;

    @Override
    public void onEnable() {
        if (getConfig().getBoolean("bstats_consent", true)) {
            int pluginId = 17782;
            @SuppressWarnings("unused")
            Metrics metrics = new Metrics(this, pluginId);
            getLogger().info("bstats for PlayerBiomes has been enabled. You can opt-out by disabling bstats in the plugin config.");
        }

        forceServerLocale = getConfig().getBoolean("force_server_locale", false);
        placeholderApiLoaded = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        
        if (placeholderApiLoaded) {
            new PlaceholderAPIHandler(this).register();
        } else {
            getLogger().warning("PlaceholderAPI is not available and thus placeholders will not be registered");
        }

        this.saveDefaultConfig();
        this.getCommand("whatbiome").setExecutor(new PlayerBiomesCommand(this));
        getLogger().info("/whatbiome is now a valid question! (PlayerBiomes has been enabled)");
    }

    public class PlayerBiomesCommand implements CommandExecutor {
        private final JavaPlugin plugin;
        public PlayerBiomesCommand(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        private String parseDefaultPlaceholders(String message, OfflinePlayer player) {
            return message
                .replace("{biome_formatted}", BiomeUtils.getBiomeFormatted(player))
                .replace("{biome_name}", BiomeUtils.getBiomeName(player))
                .replace("{biome_namespace}", BiomeUtils.getBiomeNamespace(player))
                .replace("{biome_raw}", BiomeUtils.getPlayerBiomeKey(player).toString());
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player) && args.length == 0) {
                String message = plugin.getConfig().getString("messages.console_whatbiome", "This command can only be executed by a player.");
                sender.sendMessage(message);
                return true;
            }

            Player player = (Player) sender;

            if (args.length == 0) {
                String defaultMessage = "[PlayerBiomes] You are currently in the biome - {biome_formatted}.";

                String message = plugin.getConfig().getString("messages.user_whatbiome", defaultMessage);

                if (
                    !message.contains("{biome_formatted}") &&
                    !message.contains("{biome_name}") &&
                    !message.contains("{biome_namespace}") &&
                    !message.contains("{biome_raw}")
                ) {
                    getLogger().warning("No biome placeholder found in the player message. Please read instructions in the config properly!");
                    message = defaultMessage;
                }

                message = parseDefaultPlaceholders(message, player);

                if (placeholderApiLoaded) {
                    message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player.getPlayer(), message);
                }

                player.getPlayer().sendMessage(message);
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission("playerbiomes.command.reload")) {
                    plugin.reloadConfig();
                    forceServerLocale = plugin.getConfig().getBoolean("force_server_locale", false);
                    player.sendMessage("PlayerBiomes configuration reloaded.");
                    getLogger().info("PlayerBiomes configuration reloaded.");
                } else {
                    player.sendMessage(command.getUsage());
                }
                return true;
            }

            // Usage message
            boolean canReload = player.hasPermission("playerbiomes.command.reload");
            String usage = canReload
                ? command.getUsage() + " | /playerbiomes reload"
                : command.getUsage();
            player.sendMessage(usage);
            return true;
        }
    }
}