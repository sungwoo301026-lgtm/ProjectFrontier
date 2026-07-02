package com.frontier.command.commands;

import com.frontier.command.SubCommand;
import com.frontier.config.ConfigManager;
import com.frontier.core.ManagerRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class InfoCommand implements SubCommand {

    private final JavaPlugin plugin;
    private final ManagerRegistry registry;

    public InfoCommand(JavaPlugin plugin, ManagerRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "플러그인 정보를 보여줍니다.";
    }

    @Override
    public String getUsage() {
        return "/frontier info";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        ConfigManager config = registry.get(ConfigManager.class);

        sender.sendMessage("§6========== Project Frontier ==========");
        sender.sendMessage("§ePlugin Version §7: " + plugin.getPluginMeta().getVersion());
        sender.sendMessage("§eServer Version §7: " + Bukkit.getVersion());
        sender.sendMessage("§eJava Version §7: " + System.getProperty("java.version"));
        sender.sendMessage("§eDebug Mode §7: " + config.isDebug());
        sender.sendMessage("§6======================================");

        return true;
    }
}