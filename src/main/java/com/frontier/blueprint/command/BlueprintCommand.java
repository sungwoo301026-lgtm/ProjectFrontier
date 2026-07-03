package com.frontier.blueprint.command;

import com.frontier.blueprint.BlueprintManager;
import com.frontier.blueprint.BlueprintType;
import com.frontier.command.SubCommand;
import com.frontier.core.ManagerRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

/**
 * /frontier blueprint             — 제작 가능한 청사진 목록
 * /frontier blueprint craft       — 제작대 청사진 제작 (현재 유일 종류라 생략 허용)
 * /frontier blueprint craft crafting_table — 종류 명시 제작
 */
public final class BlueprintCommand implements SubCommand {

    private final ManagerRegistry registry;

    public BlueprintCommand(ManagerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "blueprint";
    }

    @Override
    public String getDescription() {
        return "청사진을 확인하고 제작합니다.";
    }

    @Override
    public String getUsage() {
        return "/frontier blueprint [craft] [종류]";
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

        BlueprintManager blueprintManager = registry.get(BlueprintManager.class);

        // /frontier blueprint — 목록 출력 (Feature 004 동작 유지)
        if (args.length == 0) {
            List<BlueprintType> craftable = blueprintManager.getCraftableBlueprints(player);
            if (craftable.isEmpty()) {
                player.sendMessage(Component.text("제작 가능한 청사진이 없습니다. 연구를 진행해 주세요.", NamedTextColor.GRAY));
                return true;
            }
            player.sendMessage(Component.text("===== 제작 가능한 청사진 =====", NamedTextColor.GOLD));
            for (BlueprintType type : craftable) {
                player.sendMessage(Component.text(" - ", NamedTextColor.GRAY)
                        .append(Component.text(type.getDisplayName(), NamedTextColor.GREEN)));
            }
            player.sendMessage(Component.text("제작: /frontier blueprint craft", NamedTextColor.GRAY));
            return true;
        }

        // /frontier blueprint craft [종류]
        if (args[0].equalsIgnoreCase("craft")) {
            BlueprintType type;
            if (args.length >= 2) {
                type = parseType(args[1]);
                if (type == null) {
                    player.sendMessage(Component.text("알 수 없는 청사진 종류입니다: " + args[1], NamedTextColor.RED));
                    return true;
                }
            } else {
                // 종류 생략 — 현재 유일 종류인 제작대 청사진
                type = BlueprintType.CRAFTING_TABLE;
            }

            boolean crafted = blueprintManager.craftBlueprint(player, type);
            if (crafted) {
                player.sendMessage(Component.text(type.getDisplayName() + "을(를) 제작했습니다.", NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text("연구가 완료되지 않아 제작할 수 없습니다.", NamedTextColor.RED));
            }
            return true;
        }

        player.sendMessage(Component.text("사용법: " + getUsage(), NamedTextColor.RED));
        return true;
    }

    /** "crafting_table" → CRAFTING_TABLE (대소문자 무관) */
    private BlueprintType parseType(String input) {
        try {
            return BlueprintType.valueOf(input.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("craft");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("craft")) {
            return List.of("crafting_table");
        }
        return List.of();
    }
}