package com.pseudosmp.playerbiomes.nms.impl.v1_16_R3;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import com.pseudosmp.playerbiomes.nms.impl.BiomeNms;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.BiomeBase;
import net.minecraft.server.v1_16_R3.Chunk;
import net.minecraft.server.v1_16_R3.DedicatedServer;
import net.minecraft.server.v1_16_R3.IRegistry;
import net.minecraft.server.v1_16_R3.IRegistryWritable;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import net.minecraft.server.v1_16_R3.ResourceKey;
import org.bukkit.Bukkit;

/**
 * 1.16_R3 biome lookup: access chunk biome index, shift coordinates, then map
 * to registry key.
 */
public final class BiomeNmsImpl implements BiomeNms {

    private static final ResourceKey<IRegistry<BiomeBase>> BIOME_REGISTRY_KEY = IRegistry.ay; // same constant JeffLib uses

    @Override
    public NamespacedKey getBiomeKey(Location location) {
        if (location == null || location.getWorld() == null) {
            return NamespacedKey.minecraft("unknown");
        }
        try {
            MinecraftKey key = resolveMinecraftKey(location);
            if (key == null) return NamespacedKey.minecraft("unknown");
            return new NamespacedKey(key.getNamespace(), key.getKey());
        } catch (Throwable t) {
            return NamespacedKey.minecraft("unknown");
        }
    }

    private MinecraftKey resolveMinecraftKey(Location loc) {
        BiomeBase biome = fetchBiomeBase(loc);
        if (biome == null) return null;
        IRegistryWritable<BiomeBase> registry = getBiomeRegistry();
        return registry.getKey(biome);
    }

    private IRegistryWritable<BiomeBase> getBiomeRegistry() {
        DedicatedServer server = ((org.bukkit.craftbukkit.v1_16_R3.CraftServer) Bukkit.getServer()).getServer();
        return server.getCustomRegistry().b(BIOME_REGISTRY_KEY);
    }

    private BiomeBase fetchBiomeBase(Location loc) {
        BlockPosition bp = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        Chunk nmsChunk = ((CraftWorld) loc.getWorld()).getHandle().getChunkAtWorldCoords(bp);
        if (nmsChunk == null) return null;
        // >> 2 = section-level (same logic as JeffLib — quarter resolution for noise biome)
        return nmsChunk.getBiomeIndex().getBiome(bp.getX() >> 2, bp.getY() >> 2, bp.getZ() >> 2);
    }
}
