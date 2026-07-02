package com.frontier.command.commands;

import com.frontier.command.SubCommand;
import com.frontier.config.ConfigManager;
import com.frontier.core.ManagerRegistry;
import org.bukkit.command.CommandSender;

public final class ReloadCommand implements SubCommand {

    private final ManagerRegistry registry;

    public ReloadCommand(ManagerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "config.yml을 다시 불러옵니다.";
    }

    @Override
    public String getUsage() {
        return "/frontier reload";
    }

    @Override
    public String getPermission() {
        return "frontier.command.reload";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        registry.get(ConfigManager.class).reload();
        sender.sendMessage("§a설정을 다시 불러왔습니다.");
        return true;
    }
}