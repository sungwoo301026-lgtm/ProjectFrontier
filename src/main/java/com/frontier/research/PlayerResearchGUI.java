package com.frontier.research;

import com.frontier.gui.BaseGUI;
import com.frontier.gui.button.SimpleButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Tier 1 연구 목록 GUI (27칸).
 * 현재 목록: crafting_table 하나.
 * - NOT_STARTED: 클릭 시 연구 시작 (30초)
 * - RESEARCHING: 남은 시간 표시
 * - COMPLETED: 완료 메시지
 * Feature 003B: 뒤로가기(슬롯 22) → 연구소(ResearchLabGUI) 복귀.
 */
public final class PlayerResearchGUI extends BaseGUI {

    private static final int CENTER_SLOT = 13;
    private static final int BACK_SLOT = 22; // 하단 중앙 (연구 아이콘과 같은 열)

    private final ResearchData researchData;
    private final ResearchManager researchManager;

    public PlayerResearchGUI(ResearchData researchData, ResearchManager researchManager) {
        super(Component.text("연구"), 27);
        this.researchData = researchData;
        this.researchManager = researchManager;
    }

    @Override
    protected void setup() {
        String id = ResearchManager.RESEARCH_CRAFTING_TABLE;

        addButton(new SimpleButton("research_" + id, CENTER_SLOT, createResearchIcon(id), (player, event) -> {
            switch (researchData.getState(id)) {
                case NOT_STARTED -> {
                    boolean started = researchManager.startResearch(player, id);
                    if (started) {
                        player.sendMessage(Component.text("연구를 시작했습니다: 제작대 (30초)", NamedTextColor.GREEN));
                        new PlayerResearchGUI(researchData, researchManager).open(player);
                    } else {
                        player.sendMessage(Component.text("연구를 시작할 수 없습니다.", NamedTextColor.RED));
                    }
                }
                case RESEARCHING -> {
                    long remaining = researchManager.getRemainingSeconds(researchData, id);
                    if (remaining <= 0) {
                        researchManager.refreshCompletion(researchData);
                        player.sendMessage(Component.text("연구 완료: 제작대", NamedTextColor.GREEN));
                        new PlayerResearchGUI(researchData, researchManager).open(player);
                    } else {
                        player.sendMessage(Component.text("연구 중... 남은 시간: " + remaining + "초", NamedTextColor.YELLOW));
                    }
                }
                case COMPLETED ->
                        player.sendMessage(Component.text("이미 완료된 연구입니다.", NamedTextColor.GRAY));
            }
        }));

        // Feature 003B: 뒤로가기 → 연구소 첫 화면
        addButton(new SimpleButton("research_back", BACK_SLOT, createBackIcon(), (player, event) ->
                new ResearchLabGUI(researchData, researchManager).open(player)
        ));
    }

    private ItemStack createResearchIcon(String researchId) {
        ResearchState state = researchData.getState(researchId);

        ItemStack icon = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta meta = icon.getItemMeta();

        meta.displayName(Component.text("제작대 연구", NamedTextColor.AQUA));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Tier 1", NamedTextColor.GRAY));
        lore.add(Component.text("상태: " + state.name(), stateColor(state)));

        switch (state) {
            case NOT_STARTED -> lore.add(Component.text("클릭하여 연구 시작 (30초)", NamedTextColor.YELLOW));
            case RESEARCHING -> lore.add(Component.text(
                    "남은 시간: " + researchManager.getRemainingSeconds(researchData, researchId) + "초",
                    NamedTextColor.YELLOW));
            case COMPLETED -> lore.add(Component.text("완료 시 제작대 청사진 해금 (예정)", NamedTextColor.DARK_GRAY));
        }

        meta.lore(lore);
        icon.setItemMeta(meta);
        return icon;
    }

    private ItemStack createBackIcon() {
        ItemStack icon = new ItemStack(Material.ARROW);
        ItemMeta meta = icon.getItemMeta();
        meta.displayName(Component.text("뒤로가기", NamedTextColor.WHITE));
        meta.lore(List.of(Component.text("연구소로 돌아갑니다.", NamedTextColor.GRAY)));
        icon.setItemMeta(meta);
        return icon;
    }

    /** 15_UI_UX.md §7 색상 규칙: 회색=기본, 노랑=진행, 초록=완료 */
    private NamedTextColor stateColor(ResearchState state) {
        return switch (state) {
            case NOT_STARTED -> NamedTextColor.GRAY;
            case RESEARCHING -> NamedTextColor.YELLOW;
            case COMPLETED -> NamedTextColor.GREEN;
        };
    }
}