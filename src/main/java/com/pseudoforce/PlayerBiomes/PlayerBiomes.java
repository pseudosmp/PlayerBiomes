package com.pseudoforce.PlayerBiomes;

import com.jeff_media.jefflib.*;
import com.jeff_media.jefflib.pluginhooks.PlaceholderAPIUtils;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.bstats.bukkit.Metrics;


public class PlayerBiomes extends JavaPlugin {

    public static String getBiomeFormatted(OfflinePlayer player) {
        NamespacedKey namespacedKey = BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation());
        String biomeKey = namespacedKey.getKey();
        String biomeNamespace = namespacedKey.getNamespace();

        String biome = biomeKey.replaceAll("_", " ");

        int findSlash = biome.indexOf("/");
        if (findSlash != -1) {
            biome = biome.substring(biome.lastIndexOf("/") + 1);
        }

        String formattedBiome = Arrays.stream(biome.split("\\s"))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
            .collect(Collectors.joining(" "));

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
        JeffLib.init(this);
        PlaceholderAPIUtils.register("biome_raw", player -> {
            if(player.isOnline()) {
                return BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getNamespace() + ":" + BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getKey();
            } else {
                return null;
            }
        });

        PlaceholderAPIUtils.register("biome_namespace", player -> {
            if (player.isOnline()) {
                String ns = BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getNamespace();
                return ns.substring(0, 1).toUpperCase() + ns.substring(1);
            } else {
                return null;
            }
        });

        PlaceholderAPIUtils.register("biome_name", player -> {
            if(player.isOnline()) {
                String biomeKey = BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation()).getKey();
                String biome = biomeKey.replaceAll("_", " ");
            
                int findSlash = biome.indexOf("/");
                if (findSlash != -1) {
                    biome = biome.substring(biome.lastIndexOf("/") + 1);
                }
            
                String formattedBiomeName = Arrays.stream(biome.split("\\s"))
                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                    .collect(Collectors.joining(" "));
            
                return formattedBiomeName.trim();            
            } else {
                return null;
            }
        });

        PlaceholderAPIUtils.register("biome_formatted", player -> {
            if (player.isOnline()) {
                return getBiomeFormatted(player);
            } else {
                return null;
            }
        });
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

            OfflinePlayer player = (Player) sender;
            String formattedBiome = getBiomeFormatted(player);

            // Get the message from the config and send
            String message = plugin.getConfig().getString("messages.user_whereami");
            if (message == null) {
                getLogger().warning("The user message for /whereami is blank. Check your config!");
                message = "You are currently in the %s biome.";
            }
            player.getPlayer().sendMessage(String.format(message, formattedBiome));
            return true;
        }
    }
}