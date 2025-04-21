package com.pseudosmp.PlayerBiomes;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bstats.bukkit.Metrics;
import com.jeff_media.jefflib.BiomeUtils;

public class PlayerBiomes extends JavaPlugin {

    private static Boolean biomeInterfaceCache = null;

    public static boolean isModernBiomeAPI() {
        if (biomeInterfaceCache != null) return biomeInterfaceCache;

        try {
            Class<?> biomeClass = Class.forName("org.bukkit.block.Biome");
            biomeInterfaceCache = biomeClass.isInterface();
            return biomeInterfaceCache;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static NamespacedKey getPlayerBiomeKey(OfflinePlayer player) {
        if (isModernBiomeAPI()) {
            try {
                Object biome = player.getPlayer().getLocation().getBlock().getClass()
                        .getMethod("getBiome")
                        .invoke(player.getPlayer().getLocation().getBlock());

                Object namespacedKeyObj = biome.getClass().getMethod("getKeyOrThrow").invoke(biome);
                return (NamespacedKey) namespacedKeyObj;
            } catch (Throwable t) {
                t.printStackTrace();
                return new NamespacedKey("minecraft", "unknown");
            }
        } else {
            return BiomeUtils.getBiomeNamespacedKey(player.getPlayer().getLocation());
        }
    }

    public static String getBiomeFormatted(OfflinePlayer player) {
        NamespacedKey namespacedKey = getPlayerBiomeKey(player);

        String biomeNamespace = namespacedKey.getNamespace();
        String biomeKey = namespacedKey.getKey();

        String biome = biomeKey.replaceAll("[_.]", " ");
        StringBuilder formattedBiome = new StringBuilder();

        int findSlash = biome.lastIndexOf("/");
        biome = biome.substring(findSlash + 1);

        String[] words = biome.split("\\s");
        for (String w : words) {
            formattedBiome.append(w.substring(0, 1).toUpperCase()).append(w.substring(1)).append(" ");
        }
        formattedBiome.insert(0, biomeNamespace.substring(0, 1).toUpperCase() + biomeNamespace.substring(1) + ": ");
        return formattedBiome.toString().trim();
    }

    @Override
    public void onEnable() {
        if (getConfig().getBoolean("bstats_consent", true)) {
            int pluginId = 17782;
            Metrics metrics = new Metrics(this, pluginId);
            getLogger().info("bstats for PlayerBiomes has been enabled. You can opt-out by disabling bstats in the plugin config.");
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlayerBiomesExpansion(this).register();
        } else {
            getLogger().warning("PlaceholderAPI is not available and thus placeholders will not be registered");
        }

        this.saveDefaultConfig();
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

            String message = plugin.getConfig().getString("messages.user_whereami");
            if (message == null) {
                getLogger().warning("The user message for /whereami is blank. Check your config!");
                message = "You are currently in the %s biome.";
            } else if (!message.contains("%s")) {
                getLogger().warning("Format specifier '%s' not available in the player message. Please read instructions in the config properly!");
                getLogger().warning("The player who executed the command will be shown the default message.");
                message = "You are currently in the %s biome.";
            }
            player.getPlayer().sendMessage(String.format(message, formattedBiome));
            return true;
        }
    }
}
