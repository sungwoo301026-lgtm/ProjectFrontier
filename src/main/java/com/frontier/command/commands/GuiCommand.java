package com.frontier.command.commands;

import com.frontier.command.SubCommand;
import com.frontier.gui.TestGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class GuiCommand implements SubCommand {

    private final JavaPlugin plugin;

    public GuiCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "gui";
    }

    @Override
    public String getDescription() {
        return "테스트 GUI를 엽니다.";
    }

    @Override
    public String getUsage() {
        return "/frontier gui";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c이 명령어는 게임 안에서만 사용할 수 있습니다.");
            return true;
        }

        new TestGUI(plugin).open(player);
        return true;
    }
}