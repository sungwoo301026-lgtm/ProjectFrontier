package com.frontier.research.command;

import com.frontier.command.SubCommand;
import com.frontier.core.ManagerRegistry;
import com.frontier.research.ResearchData;
import com.frontier.research.ResearchLabGUI;
import com.frontier.research.ResearchManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /frontier research — 개발용 테스트 명령.
 * 연구소 GUI(첫 화면)를 연다. GUI 로직은 명령에 의존하지 않는다.
 */
public final class ResearchCommand implements SubCommand {

    private final ManagerRegistry registry;

    public ResearchCommand(ManagerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "research";
    }

    @Override
    public String getDescription() {
        return "연구소를 엽니다. (개발용)";
    }

    @Override
    public String getUsage() {
        return "/frontier research";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("이 명령어는 게임 내에서만 사용할 수 있습니다.", NamedTextColor.RED));
            return true;
        }

        ResearchManager researchManager = registry.get(ResearchManager.class);
        ResearchData data = researchManager.getOrCreateResearch(player);

        // 서버 재시작 등으로 태스크가 사라진 경우를 대비해 열람 시점에 완료 판정
        researchManager.refreshCompletion(data);

        data.setLastOpened(System.currentTimeMillis());
        researchManager.saveResearch(data);

        new ResearchLabGUI(data, researchManager).open(player);
        return true;
    }
}