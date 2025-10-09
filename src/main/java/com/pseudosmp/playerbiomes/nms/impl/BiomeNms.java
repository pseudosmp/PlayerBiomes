package com.pseudosmp.playerbiomes.nms.impl;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;

/**
 * Abstraction over version-specific biome lookups.
 * Implementations live in: com.pseudosmp.playerbiomes.nms.impl.<token>.BiomeNmsImpl
 */
public interface BiomeNms {

    /**
     * Resolve the biome key (namespace:path) at the given location.
     *
     * @param location Bukkit location (world must be non-null).
     * @return NamespacedKey (never null; may return minecraft:unknown if something failed internally).
     */
    NamespacedKey getBiomeKey(Location location);
}
