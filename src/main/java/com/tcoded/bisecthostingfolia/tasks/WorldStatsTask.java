package com.tcoded.bisecthostingfolia.tasks;

import com.tcoded.bisecthostingfolia.BisectHostingFolia;
import me.lucko.spark.common.platform.world.WorldInfoProvider;
import org.bukkit.World;

import java.util.List;

public class WorldStatsTask implements Runnable {
    private static int loadedChunks = 0;
    private static int entities = 0;

    public static double getEntities() {
        return (double)entities;
    }

    public static double getLoadedChunks() {
        return (double)loadedChunks;
    }

    public void run() {
        try {
            WorldInfoProvider worldInfoProvider = BisectHostingFolia.getPlugin().getWorldInfoProvider();
            WorldInfoProvider.CountsResult counts = worldInfoProvider.pollCounts();

            entities = counts.entities();
            loadedChunks = counts.chunks();

        } catch (Exception ignored) {}

    }
}