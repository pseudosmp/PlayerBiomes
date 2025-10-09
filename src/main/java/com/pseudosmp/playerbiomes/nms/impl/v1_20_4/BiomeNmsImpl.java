package com.pseudosmp.playerbiomes.nms.impl.v1_20_4;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;

import com.pseudosmp.playerbiomes.nms.impl.BiomeNms;

/**
 * 1.20.4+ (R3 mapping) biome lookup using the registry access + noise biome holder.
 */
public final class BiomeNmsImpl implements BiomeNms {

    // Construct the biome registry resource key: "worldgen/biome"
    private static final ResourceKey<Registry<Biome>> BIOME_REGISTRY_KEY =
            ResourceKey.createRegistryKey(new ResourceLocation("worldgen/biome"));

    @Override
    public NamespacedKey getBiomeKey(Location location) {
        if (location == null || location.getWorld() == null) {
            return NamespacedKey.minecraft("unknown");
        }
        try {
            ResourceLocation rl = resolve(location);
            if (rl == null) return NamespacedKey.minecraft("unknown");
            return new NamespacedKey(rl.getNamespace(), rl.getPath());
        } catch (Throwable t) {
            return NamespacedKey.minecraft("unknown");
        }
    }

    private ResourceLocation resolve(Location loc) {
        Holder<Biome> holder = fetchHolder(loc);
        if (holder == null) return null;
        return holder.unwrapKey().map(ResourceKey::location).orElse(null);
    }

    private Holder<Biome> fetchHolder(Location loc) {
        BlockPos pos = new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        LevelChunk chunk = ((CraftWorld) loc.getWorld()).getHandle().getChunkAt(pos);
        if (chunk == null) return null;
        // Noise-biome coordinates: quarter scale in X/Z and quarter of vertical (shift by 2 bits)
        return chunk.getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2);
    }

    private Registry<Biome> biomeRegistry() {
        DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
        return server.registryAccess().registry(BIOME_REGISTRY_KEY).orElseThrow();
    }
}
