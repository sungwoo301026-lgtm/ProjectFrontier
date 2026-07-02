package com.frontier.command.commands;

import com.frontier.command.CommandManager;
import com.frontier.command.SubCommand;
import org.bukkit.command.CommandSender;

public final class HelpCommand implements SubCommand {

    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "사용 가능한 명령어 목록을 보여줍니다.";
    }

    @Override
    public String getUsage() {
        return "/frontier help";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("§6========== Project Frontier ==========");

        for (SubCommand subCommand : commandManager.getSubCommands()) {
            String permission = subCommand.getPermission();
            if (permission != null && !sender.hasPermission(permission)) {
                continue;
            }

            sender.sendMessage("§e" + subCommand.getUsage() + " §7- " + subCommand.getDescription());
        }

        sender.sendMessage("§6======================================");
        return true;
    }
}