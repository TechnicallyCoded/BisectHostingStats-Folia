package com.tcoded.bisecthostingfolia.tasks;

import com.google.common.util.concurrent.AtomicDouble;
import com.tcoded.bisecthostingfolia.BisectHostingFolia;
import io.papermc.paper.threadedregions.ThreadedRegionizer;
import io.papermc.paper.threadedregions.TickData;
import io.papermc.paper.threadedregions.TickRegions;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TpsTask implements Runnable {

    private static final AtomicDouble tps = new AtomicDouble(0.0D);

    public static double getTicksPerSeconds() {
        return tps.get();
    }

    @Override
    public void run() {
        try {
            // Config
            FileConfiguration config = BisectHostingFolia.getPlugin().getConfig();

            boolean tpsEnabled = config.getBoolean("stats.tps.enabled", true);
            if (!tpsEnabled) {
                tps.set(0);
                return;
            }

            String onlyCheckWorldName = config.getString("stats.tps.only-show-stats-for-world", "");
            boolean shouldCheckAllWorlds = onlyCheckWorldName.isEmpty();

            // Spark internal
            List<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>> regions = BisectHostingFolia.getPlugin().getAllRegions();

            // Filter region data
            long nanoTime = System.nanoTime();
            regions.stream()
                    .map(ThreadedRegionizer.ThreadedRegion::getData)
                    .filter(data -> shouldCheckAllWorlds || data.world.getWorld().getName().equals(onlyCheckWorldName))
                    .map(data -> data.getRegionSchedulingHandle().getTickReport5s(nanoTime))
                    .filter(Objects::nonNull)
                    .map(data -> data.tpsData().segmentAll().average())
                    .min(Double::compare)
                    .ifPresent(tps::set);

        } catch (IllegalStateException ignored) {}
    }

}
