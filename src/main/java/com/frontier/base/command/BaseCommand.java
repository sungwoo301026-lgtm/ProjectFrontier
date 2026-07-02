package com.frontier.base.command;

import com.frontier.base.BaseData;
import com.frontier.base.BaseManager;
import com.frontier.base.PlayerBaseGUI;
import com.frontier.command.SubCommand;
import com.frontier.core.ManagerRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /frontier base
 * 자신의 거점 GUI를 연다.
 */
public final class BaseCommand implements SubCommand {

    private final ManagerRegistry registry;

    public BaseCommand(ManagerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "base";
    }

    @Override
    public String getDescription() {
        return "자신의 거점을 엽니다.";
    }

    @Override
    public String getUsage() {
        return "/frontier base";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("이 명령어는 게임 안에서만 사용할 수 있습니다.", NamedTextColor.RED));
            return true;
        }

        BaseManager baseManager = registry.get(BaseManager.class);
        BaseData baseData = baseManager.getOrCreateBase(player);

        baseManager.saveBase(baseData);

        new PlayerBaseGUI(baseData).open(player);
        return true;
    }
}