package com.pseudosmp.playerbiomes.nms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;

import com.pseudosmp.playerbiomes.nms.impl.BiomeNms;

/**
 * Internal helper that decides between modern biome API and legacy versioned impl
 * loaded via reflection using a version token.
 *
 * Not a public API.
 */
public final class Handler {

    private Handler() {}

    // Reflector config (as requested)
    private static final String BASE_IMPL_PACKAGE = "com.pseudosmp.playerbiomes.nms.impl";
    private static final String IMPL_CLASS_SIMPLE = "BiomeNmsImpl";
    private static final String LATEST_SUPPORTED_TOKEN = "v1_21_3";

    // Cache
    private static volatile Boolean MODERN_BIOME_API = null;
    private static volatile BiomeNms CACHED_IMPL = null;
    private static volatile String CACHED_TOKEN = null;

    public static NamespacedKey getPlayerBiomeKey(OfflinePlayer player) {
        try {
            if (player == null || player.getPlayer() == null) {
                return NamespacedKey.minecraft("unknown");
            }
            final Location loc = player.getPlayer().getLocation();
            if (loc == null || loc.getWorld() == null) {
                return NamespacedKey.minecraft("unknown");
            }

            if (isModernBiomeAPI() || isAboveLatestSupported()) {
                // Modern path: block.getBiome().getKey()
                return getModernBiomeKey(loc);
            }

            // Legacy path: reflectively load versioned implementation and delegate
            final BiomeNms impl = resolveImpl();
            if (impl != null) {
                return impl.getBiomeKey(loc);
            }

            // Final fallback: modern
            return getModernBiomeKey(loc);

        } catch (Throwable t) {
            return NamespacedKey.minecraft("unknown");
        }
    }

    /* ------------------------ Modern API ------------------------ */

    private static NamespacedKey getModernBiomeKey(Location loc) {
        try {
            Object block = loc.getBlock();
            Object biome = block.getClass().getMethod("getBiome").invoke(block);
            // TODO: check fallback
            try {
                Object keyObj = biome.getClass().getMethod("getKey").invoke(biome);
                if (keyObj instanceof NamespacedKey) {
                    return (NamespacedKey) keyObj;
                }
            } catch (NoSuchMethodException ignored) {
                // Fallback: older “modern-ish” API; use toString
                String raw = String.valueOf(biome).toLowerCase();
                return NamespacedKey.minecraft(raw);
            }
        } catch (Throwable ignored) {
        }
        return NamespacedKey.minecraft("unknown");
    }

    private static boolean isModernBiomeAPI() {
        Boolean cached = MODERN_BIOME_API;
        if (cached != null) return cached;
        synchronized (Handler.class) {
            if (MODERN_BIOME_API != null) return MODERN_BIOME_API;
            try {
                Class<?> biomeClass = Class.forName("org.bukkit.block.Biome");
                MODERN_BIOME_API = biomeClass.isInterface();
            } catch (ClassNotFoundException e) {
                MODERN_BIOME_API = false;
            }
            return MODERN_BIOME_API;
        }
    }

    /* ------------------------ Legacy loader ------------------------ */

    private static BiomeNms resolveImpl() {
        BiomeNms local = CACHED_IMPL;
        if (local != null) return local;

        synchronized (Handler.class) {
            if (CACHED_IMPL != null) return CACHED_IMPL;

            String token = token();
            // Try exact token
            BiomeNms loaded = tryLoad(token);
            if (loaded != null) {
                CACHED_IMPL = loaded;
                CACHED_TOKEN = token;
                return loaded;
            }

            // Fallback to latest supported
            if (!LATEST_SUPPORTED_TOKEN.equals(token)) {
                loaded = tryLoad(LATEST_SUPPORTED_TOKEN);
                if (loaded != null) {
                    CACHED_IMPL = loaded;
                    CACHED_TOKEN = LATEST_SUPPORTED_TOKEN;
                    return loaded;
                }
            }

            // Give up; caller will fall back to modern
            return null;
        }
    }

    private static BiomeNms tryLoad(String token) {
        final String fqcn = BASE_IMPL_PACKAGE + "." + token + "." + IMPL_CLASS_SIMPLE;
        try {
            Class<?> clazz = Class.forName(fqcn);
            Object o = clazz.getDeclaredConstructor().newInstance();
            if (o instanceof BiomeNms) {
                return (BiomeNms) o;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    /* ------------------------ Version token ------------------------ */

    private static String token() {
        McVersion v = normalized();
        if (v.isAtLeast(1, 19, 0)) {
            StringBuilder sb = new StringBuilder("v")
                    .append(v.major()).append("_")
                    .append(v.minor());
            if (v.patch() > 0) sb.append("_").append(v.patch());
            return sb.toString();
        }
        // Legacy: CraftBukkit package last segment (e.g. v1_16_R3)
        String cb = Bukkit.getServer().getClass().getPackage().getName();
        return cb.substring(cb.lastIndexOf('.') + 1);
    }

    private static boolean isAboveLatestSupported() {
        McVersion cur = normalized();
        return cur.compareTo(new McVersion(1, 21, 3)) > 0;
    }

    private static McVersion normalized() {
        McVersion raw = parseBukkitVersion();
        // Take care of "skip" builds
        if (raw.equals(1,21,1)) return new McVersion(1,21,0);
        if (raw.equals(1,20,6)) return new McVersion(1,20,5);
        if (raw.equals(1,20,3)) return new McVersion(1,20,4);
        if (raw.equals(1,20,0)) return new McVersion(1,20,1);
        return raw;
    }

    private static McVersion parseBukkitVersion() {
        // e.g. "1.20.4-R0.1-SNAPSHOT" -> take the "1.20.4" part
        String core = Bukkit.getBukkitVersion().split("-")[0];
        String[] parts = core.split("\\.");
        int maj = parts.length > 0 ? tryInt(parts[0]) : 0;
        int min = parts.length > 1 ? tryInt(parts[1]) : 0;
        int pat = parts.length > 2 ? tryInt(parts[2]) : 0;
        return new McVersion(maj, min, pat);
    }

    private static int tryInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }

    /* ------------------------ Minecraft Version Structure ------------------------ */

    private static final class McVersion implements Comparable<McVersion> {
        private final int major, minor, patch;
        McVersion(int major, int minor, int patch) {
            this.major = major; this.minor = minor; this.patch = patch;
        }
        boolean isAtLeast(int maj, int min, int pat) {
            return compareTo(new McVersion(maj, min, pat)) >= 0;
        }
        boolean equals(int maj, int min, int pat) {
            return major == maj && minor == min && patch == pat;
        }
        int major() { return major; }
        int minor() { return minor; }
        int patch() { return patch; }
        @Override public int compareTo(McVersion o) {
            int c = Integer.compare(major, o.major);
            if (c != 0) return c;
            c = Integer.compare(minor, o.minor);
            if (c != 0) return c;
            return Integer.compare(patch, o.patch);
        }
        @Override public String toString() {
            return patch > 0 ? major + "." + minor + "." + patch : major + "." + minor;
        }
        @Override public boolean equals(Object obj) {
            if (!(obj instanceof McVersion)) return false;
            McVersion v = (McVersion) obj;
            return v.major == major && v.minor == minor && v.patch == patch;
        }
        @Override public int hashCode() { return (major * 10000) + (minor * 100) + patch; }
    }
}
