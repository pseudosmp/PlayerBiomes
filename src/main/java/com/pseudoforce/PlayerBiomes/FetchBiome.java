package com.pseudoforce.PlayerBiomes;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import net.minecraft.server.v1_16_R2.BiomeBase;
import net.minecraft.server.v1_16_R2.BlockPosition;
import net.minecraft.server.v1_16_R2.Chunk;
import net.minecraft.server.v1_16_R2.DedicatedServer;
import net.minecraft.server.v1_16_R2.IRegistry;
import net.minecraft.server.v1_16_R2.IRegistryWritable;
import net.minecraft.server.v1_16_R2.MinecraftKey;
import net.minecraft.server.v1_16_R2.ResourceKey;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;

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
            return nmsChunk.getBiomeIndex().getBiome(pos.getX(), pos.getY(), pos.getZ());
        }
        return null;
    }
}
