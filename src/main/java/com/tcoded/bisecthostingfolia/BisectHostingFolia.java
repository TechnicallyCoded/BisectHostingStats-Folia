package com.tcoded.bisecthostingfolia;

import com.tcoded.bisecthostingfolia.command.BisectHostingStatsCmd;
import com.tcoded.bisecthostingfolia.tasks.TpsTask;
import com.tcoded.bisecthostingfolia.tasks.WorldStatsTask;
import com.tcoded.bisecthostingfolia.tasks.WriteStatsAsyncTask;
import io.papermc.paper.threadedregions.ThreadedRegionizer;
import io.papermc.paper.threadedregions.TickRegions;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.bukkit.folia.FoliaTickStatistics;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.api.SparkApi;
import me.lucko.spark.common.monitor.tick.TickStatistics;
import me.lucko.spark.common.platform.PlatformStatisticsProvider;
import me.lucko.spark.common.platform.world.WorldInfoProvider;
import me.lucko.spark.common.platform.world.WorldStatisticsProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class BisectHostingFolia extends JavaPlugin {
    private static final String NAME = "BisectHosting";
    private static BisectHostingFolia plugin;

    // Spark stuff
    private SparkPlatform sparkPlatform;
    private Field regionSupplierField;
    private FoliaTickStatistics foliaTickStats;
    private WorldInfoProvider worldInfoProvider;

    public void onEnable() {
        // Init
        setPlugin(this);

        // Config
        saveDefaultConfig();

        // Spark integration
        init();

        // Commands
        BisectHostingStatsCmd bisectHostingStatsCmd = new BisectHostingStatsCmd();
        PluginCommand bisectHostingStatsPlCmd = getCommand("bisecthostingstats");
        if (bisectHostingStatsPlCmd != null) {
            bisectHostingStatsPlCmd.setExecutor(bisectHostingStatsCmd);
            bisectHostingStatsPlCmd.setTabCompleter(bisectHostingStatsCmd);
        } else {
            getLogger().warning("Could not register command BisectHostingStatsCmd");
        }

        // Tasks
        this.registerScheduledTasks();
    }

    public void onDisable() {
    }

    private void registerScheduledTasks() {
        AsyncScheduler asyncScheduler = this.getServer().getAsyncScheduler();
        GlobalRegionScheduler globalRegionScheduler = this.getServer().getGlobalRegionScheduler();

        TpsTask tpsTask = new TpsTask();
        asyncScheduler.runAtFixedRate(this, (task) -> tpsTask.run(), 5 * 20 * 50, 50, TimeUnit.MILLISECONDS);

        WorldStatsTask worldStatsTask = new WorldStatsTask();
        globalRegionScheduler.runAtFixedRate(this, (task) -> worldStatsTask.run(), 8 * 20, 2 * 20);

        WriteStatsAsyncTask writeStatsAsyncTask = new WriteStatsAsyncTask();
        asyncScheduler.runAtFixedRate(this, (task) -> writeStatsAsyncTask.run(), 10 * 20 * 50, 2 * 20 * 50, TimeUnit.MILLISECONDS);
    }

    public static void setPlugin(BisectHostingFolia plugin) {
        BisectHostingFolia.plugin = plugin;
    }

    public static BisectHostingFolia getPlugin() {
        return plugin;
    }

    public static String getPluginName() {
        return "BisectHosting";
    }

    // Init spark stuff
    public void init() {
        try {
            SparkApi spark = (SparkApi) SparkProvider.get();

            Class<? extends SparkApi> sparkClass = spark.getClass();
            Field platformField = sparkClass.getDeclaredField("platform");
            platformField.setAccessible(true);

            sparkPlatform = (SparkPlatform) platformField.get(spark);
            TickStatistics tickStats = sparkPlatform.getTickStatistics();
            worldInfoProvider = sparkPlatform.getPlugin().createWorldInfoProvider();

            if (!(tickStats instanceof FoliaTickStatistics)) {
                Bukkit.getLogger().warning("Folia is not installed, TPS will not be recorded.");
                return;
            }

            foliaTickStats = (FoliaTickStatistics) tickStats;
            Class<? extends FoliaTickStatistics> tickStatsClass = foliaTickStats.getClass();
            regionSupplierField = tickStatsClass.getDeclaredField("regionSupplier");
            regionSupplierField.setAccessible(true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public List<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>> getAllRegions() {
        List<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>> regions;

        try {
            // noinspection unchecked
            Supplier<List<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>>> supplier =
                    (Supplier<List<ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>>>) regionSupplierField.get(foliaTickStats);

            regions = supplier.get();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        return regions;
    }

    public Field getRegionSupplierField() {
        return regionSupplierField;
    }

    public FoliaTickStatistics getFoliaTickStats() {
        return foliaTickStats;
    }

    public SparkPlatform getSparkPlatform() {
        return sparkPlatform;
    }

    public WorldInfoProvider getWorldInfoProvider() {
        return worldInfoProvider;
    }

}
