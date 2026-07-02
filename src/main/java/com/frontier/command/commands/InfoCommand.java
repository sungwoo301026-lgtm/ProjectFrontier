package com.frontier.command.commands;

import com.frontier.base.BaseManager;
import com.frontier.command.SubCommand;
import com.frontier.config.ConfigManager;
import com.frontier.core.ManagerRegistry;
import com.frontier.data.DataManager;
import com.frontier.event.ListenerManager;
import com.frontier.research.ResearchManager;
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
    public String getPermission() {
        return null;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        ConfigManager configManager = registry.get(ConfigManager.class);
        DataManager dataManager = registry.get(DataManager.class);
        BaseManager baseManager = registry.get(BaseManager.class);
        ResearchManager researchManager = registry.get(ResearchManager.class);
        ListenerManager listenerManager = registry.get(ListenerManager.class);

        sender.sendMessage("§6========== Project Frontier ==========");
        sender.sendMessage("§ePlugin Version §7: " + plugin.getPluginMeta().getVersion());
        sender.sendMessage("§eServer Version §7: " + Bukkit.getVersion());
        sender.sendMessage("§eJava Version §7: " + System.getProperty("java.version"));
        sender.sendMessage("§eDebug Mode §7: " + configManager.isDebug());

        sender.sendMessage("");

        sender.sendMessage("§6=== Framework ===");
        sender.sendMessage("§eManagers §7: " + registry.size());
        sender.sendMessage("§eListeners §7: " + listenerManager.getListenerCount());

        sender.sendMessage("");

        sender.sendMessage("§6=== Data ===");
        sender.sendMessage("§eLoaded Players §7: " + dataManager.getLoadedPlayerCount());
        sender.sendMessage("§eLoaded Bases §7: " + baseManager.getLoadedBaseCount());
        sender.sendMessage("§eLoaded Research §7: " + researchManager.getLoadedResearchCount());
        sender.sendMessage("§eAuto Save §7: " + dataManager.getAutoSaveIntervalSeconds() + "s");

        sender.sendMessage("§6======================================");

        return true;
    }
}