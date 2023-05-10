package com.tcoded.bisecthostingfolia.tasks;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.Files;

public class WriteStatsAsyncTask implements Runnable {

    public void run() {
        long mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double tps = TpsTask.getTicksPerSeconds();

        JsonObject result = new JsonObject();
        result.addProperty("version", 1);
        result.addProperty("type", "bukkit");
        result.addProperty("time", System.currentTimeMillis() / 1000L);

        JsonObject data = new JsonObject();
        data.addProperty("memory", mem);
        data.addProperty("tps", tps);
        data.addProperty("entities", WorldStatsTask.getEntities());
        data.addProperty("chunks", WorldStatsTask.getLoadedChunks());

        result.add("data", data);

        try {
            Files.writeString(Paths.get(".socket"), result.toString());
        } catch (IOException var8) {
            var8.printStackTrace();
        }
    }

}