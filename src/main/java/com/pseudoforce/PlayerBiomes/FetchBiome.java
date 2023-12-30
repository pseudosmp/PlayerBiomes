package com.pseudoforce.PlayerBiomes;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_16_R3.BiomeBase;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.Chunk;
import net.minecraft.server.v1_16_R3.DedicatedServer;
import net.minecraft.server.v1_16_R3.IRegistry;
import net.minecraft.server.v1_16_R3.IRegistryWritable;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import net.minecraft.server.v1_16_R3.ResourceKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

@UtilityClass
public class FetchBiome {

    private final ResourceKey<IRegistry<BiomeBase>> BIOME_REGISTRY_RESOURCE_KEY = IRegistry.ay;

    class Pair {
        String namespace;
        String key;

        Pair(String namespace, String key) {
            this.namespace = namespace;
            this.key = key;
        }
    }

    Pair getBiomeName(final Location location) {
        final MinecraftKey key = getBiomeKey(location);
        return new Pair(key.getNamespace(), key.getKey());
    }

    MinecraftKey getBiomeKey(final Location location) {
        final IRegistryWritable<BiomeBase> registry = getBiomeRegistry();
        return registry.getKey(getBiomeBase(location));
    }

    IRegistryWritable<BiomeBase> getBiomeRegistry() {
        final DedicatedServer dedicatedServer = ((CraftServer) Bukkit.getServer()).getServer();
        return dedicatedServer.getCustomRegistry().b(BIOME_REGISTRY_RESOURCE_KEY);
    }

    BiomeBase getBiomeBase(final Location location) {
        final BlockPosition pos = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        final Chunk nmsChunk = ((CraftWorld) location.getWorld()).getHandle().getChunkAtWorldCoords(pos);
        if (nmsChunk != null) {
            return nmsChunk.getBiomeIndex().getBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2);
        }
        return null;
    }
}
