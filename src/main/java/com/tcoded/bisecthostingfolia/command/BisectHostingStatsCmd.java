package com.tcoded.bisecthostingfolia.command;

import com.tcoded.bisecthostingfolia.BisectHostingFolia;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BisectHostingStatsCmd implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String arg0Lower = args[0].toLowerCase();
        switch (arg0Lower) {
            case "reload":
                BisectHostingFolia.getPlugin().reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Done!");
                break;
            default:
                showHelp(sender);
                break;
        }

        return true;
    }

    private static void showHelp(@NotNull CommandSender sender) {
        sender.sendMessage("§6BisectHostingFolia §7v1.0.0");
        sender.sendMessage("§7Usage: /bisecthostingstats reload");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of("reload");
    }
}
