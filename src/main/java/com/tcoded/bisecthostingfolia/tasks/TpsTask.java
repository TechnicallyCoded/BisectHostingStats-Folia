package com.tcoded.bisecthostingfolia.tasks;

import com.google.common.util.concurrent.AtomicDouble;
import com.tcoded.bisecthostingfolia.BisectHostingFolia;
import io.papermc.paper.threadedregions.ThreadedRegionizer;
import io.papermc.paper.threadedregions.TickData;
import io.papermc.paper.threadedregions.TickRegions;

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
            List<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>> regions = BisectHostingFolia.getPlugin().getAllRegions();

            AtomicInteger regionCount = new AtomicInteger(0);

            long nanoTime = System.nanoTime();
            regions.stream()
                    .map(region -> region.getData().getRegionSchedulingHandle().getTickReport5s(nanoTime))
                    .filter(Objects::nonNull)
                    .map(data -> {
                        regionCount.addAndGet(1);
                        return data;
                    })
                    .map(data -> data.tpsData().segmentAll().average())
                    .min(Double::compare)
                    .ifPresent(tps::set);

        } catch (IllegalStateException ignored) {}
    }

}
