package com.frontier.command;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * 모든 하위 명령어의 공통 규약.
 */
public interface SubCommand {

    String getName();

    String getDescription();

    String getUsage();

    default String getPermission() {
        return null;
    }

    boolean execute(CommandSender sender, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}