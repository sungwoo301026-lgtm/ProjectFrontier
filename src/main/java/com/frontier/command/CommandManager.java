package com.frontier.command;

import com.frontier.base.command.BaseCommand;
import com.frontier.command.commands.GuiCommand;
import com.frontier.command.commands.HelpCommand;
import com.frontier.command.commands.InfoCommand;
import com.frontier.command.commands.ReloadCommand;
import com.frontier.core.Manager;
import com.frontier.core.ManagerRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CommandManager implements Manager, CommandExecutor, TabCompleter {

    private static final String ROOT_COMMAND = "frontier";

    private final JavaPlugin plugin;
    private final ManagerRegistry registry;
    private final Map<String, SubCommand> subCommands = new LinkedHashMap<>();

    public CommandManager(JavaPlugin plugin, ManagerRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "Command";
    }

    @Override
    public void initialize() {
        registerSubCommand(new HelpCommand(this));
        registerSubCommand(new InfoCommand(plugin, registry));
        registerSubCommand(new ReloadCommand(registry));
        registerSubCommand(new GuiCommand(plugin));
        registerSubCommand(new BaseCommand(registry));

        PluginCommand command = plugin.getCommand(ROOT_COMMAND);
        if (command == null) {
            throw new IllegalStateException("plugin.yml에 '" + ROOT_COMMAND + "' 명령어가 정의되어 있지 않습니다.");
        }

        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public void shutdown() {
        subCommands.clear();
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(Locale.ROOT), subCommand);
    }

    public Collection<SubCommand> getSubCommands() {
        return subCommands.values();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            subCommands.get("help").execute(sender, new String[0]);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase(Locale.ROOT));

        if (subCommand == null) {
            sender.sendMessage("§c알 수 없는 명령어입니다: " + args[0]);
            sender.sendMessage("§7/frontier help 로 사용 가능한 명령어를 확인하세요.");
            return true;
        }

        String permission = subCommand.getPermission();
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage("§c이 명령어를 사용할 권한이 없습니다.");
            return true;
        }

        return subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> result = new ArrayList<>();
            String input = args[0].toLowerCase(Locale.ROOT);

            for (SubCommand subCommand : subCommands.values()) {
                String permission = subCommand.getPermission();
                if (permission != null && !sender.hasPermission(permission)) {
                    continue;
                }

                if (subCommand.getName().startsWith(input)) {
                    result.add(subCommand.getName());
                }
            }

            return result;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase(Locale.ROOT));
        if (subCommand == null) {
            return List.of();
        }

        return subCommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
    }
}