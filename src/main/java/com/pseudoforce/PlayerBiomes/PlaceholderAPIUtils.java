package com.pseudoforce.PlayerBiomes;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@UtilityClass
public class PlaceholderAPIUtils {

    private static AnonymousPlaceholderExpansion expansion;

    public static boolean register(@NotNull final BiFunction<OfflinePlayer, String, String> function) {
        init();
        expansion.biFunctions.add(function);
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    private static void init() {
        if (expansion == null) {
            expansion = new AnonymousPlaceholderExpansion();
            expansion.register();
        }
    }

    public static boolean register(@NotNull final String placeholder, @NotNull final Function<OfflinePlayer, String> function) {
        init();
        expansion.functions.put(placeholder, function);
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    private static final class AnonymousPlaceholderExpansion extends PlaceholderExpansion {

        private final Map<String, Function<OfflinePlayer, String>> functions = new HashMap<>();
        private final Collection<BiFunction<OfflinePlayer, String, String>> biFunctions = new ArrayList<>();

        @Override
        @NotNull
        public String getIdentifier() {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("PlayerBiomes");
            return plugin.getName().toLowerCase(Locale.ROOT);
        }

        @Override
        @NotNull
        public String getAuthor() {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("PlayerBiomes");
            return plugin.getDescription().getAuthors().isEmpty() ? "" : plugin.getDescription().getAuthors().get(0);
        }

        @Override
        @NotNull
        public String getVersion() {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("PlayerBiomes");
            return plugin.getDescription().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onRequest(final OfflinePlayer player, @NotNull final String params) {
            final Function<OfflinePlayer, String> function = functions.get(params);
            if (function != null) {
                return function.apply(player);
            }
            for (final BiFunction<OfflinePlayer, String, String> biFunction : biFunctions) {
                final String result = biFunction.apply(player, params);
                if (result != null) return result;
            }
            return null;
        }
    }
}
