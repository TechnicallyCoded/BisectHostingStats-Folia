package com.tcoded.bisecthostingfolia.tasks;

import com.tcoded.bisecthostingfolia.BisectHostingFolia;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.units.qual.A;

import java.util.concurrent.atomic.AtomicInteger;

public class WorldStatsTask implements Runnable {

    private static AtomicInteger loadedChunks = new AtomicInteger(0);
    private static AtomicInteger entities = new AtomicInteger(0);

    public static double getEntities() {
        return entities.get();
    }

    public static double getLoadedChunks() {
        return loadedChunks.get();
    }

    public void run() {
        BisectHostingFolia plugin = BisectHostingFolia.getPlugin();
        Server server = plugin.getServer();

        // Stupid proof
        if (!server.isGlobalTickThread()) {
            plugin.getLogger().severe("Running on the wrong thread! (WorldStatsTask)");
            return;
        }

        // Config
        FileConfiguration config = plugin.getConfig();

        boolean entitiesEnabled = config.getBoolean("stats.entities.enabled", true);
        boolean chunksEnabled = config.getBoolean("stats.chunks.enabled", true);

        if (!entitiesEnabled && !chunksEnabled) {
            entities.set(0);
            loadedChunks.set(0);
            return;
        }

        String entitiesWorld = config.getString("stats.entities.only-show-stats-for-world", "");
        boolean includeAllEntities = entitiesWorld.isEmpty();
        String chunksWorld = config.getString("stats.chunks.only-show-stats-for-world", "");
        boolean includeAllChunks = chunksWorld.isEmpty();


        try {
            int loadedChunksTmp = 0;
            int entitiesTmp = 0;

            for (World world : server.getWorlds()) {
                if (chunksEnabled && (includeAllChunks || world.getName().equals(chunksWorld))) {
                    loadedChunksTmp += world.getLoadedChunks().length;
                }
                if (entitiesEnabled && (includeAllEntities || world.getName().equals(entitiesWorld))) {
                    entitiesTmp += world.getEntities().size();
                }
            }

            loadedChunks.set(loadedChunksTmp);
            entities.set(entitiesTmp);

        } catch (Exception ignored) {}

    }
}