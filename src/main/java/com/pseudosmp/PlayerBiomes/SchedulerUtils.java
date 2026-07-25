package com.pseudosmp.PlayerBiomes;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.logging.Level;

public class SchedulerUtils {
    private static final boolean IS_FOLIA;
    private static Method getAsyncSchedulerMethod;
    private static Method asyncRunNowMethod;

    static {
        boolean isFolia = false;
        try {
            Class<?> asyncSchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            getAsyncSchedulerMethod = Bukkit.class.getMethod("getAsyncScheduler");
            asyncRunNowMethod = asyncSchedulerClass.getMethod("runNow", Plugin.class, Consumer.class);
            isFolia = true;
        } catch (Throwable ignored) {
            isFolia = false;
        }
        IS_FOLIA = isFolia;
    }

    public static boolean isFolia() {
        return IS_FOLIA;
    }

    public static TaskWrapper runAsync(Plugin plugin, Runnable task) {
        if (IS_FOLIA) {
            try {
                Object asyncScheduler = getAsyncSchedulerMethod.invoke(null);
                Consumer<Object> consumer = s -> task.run();
                Object scheduledTask = asyncRunNowMethod.invoke(asyncScheduler, plugin, consumer);
                return () -> {
                    try {
                        scheduledTask.getClass().getMethod("cancel").invoke(scheduledTask);
                    } catch (Exception ignored) {}
                };
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to run Folia async task", e);
                return () -> {};
            }
        } else {
            BukkitTask bt = Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
            return bt::cancel;
        }
    }

    @FunctionalInterface
    public interface TaskWrapper {
        void cancel();
    }
}
