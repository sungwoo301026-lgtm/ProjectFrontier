package com.frontier.research;

import com.frontier.gui.BaseGUI;
import com.frontier.gui.button.SimpleButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * 연구 GUI.
 * Phase 7에서는 제작대 연구 하나만 표시한다.
 */
public final class PlayerResearchGUI extends BaseGUI {

    private static final int CENTER_SLOT = 13;

    private final ResearchData researchData;

    public PlayerResearchGUI(ResearchData researchData) {
        super(Component.text("연구"), 27);
        this.researchData = researchData;
    }

    @Override
    protected void setup() {
        addButton(new SimpleButton(
                "research_crafting_table",
                CENTER_SLOT,
                createResearchIcon(),
                (player, event) -> player.sendMessage(Component.text("아직 구현되지 않았습니다.", NamedTextColor.YELLOW))
        ));
    }

    private ItemStack createResearchIcon() {
        ResearchState state = researchData.getState(ResearchManager.RESEARCH_CRAFTING_TABLE);

        ItemStack icon = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta meta = icon.getItemMeta();

        meta.displayName(Component.text("제작대 연구", NamedTextColor.AQUA));

        meta.lore(List.of(
                Component.text("Tier 1", NamedTextColor.GRAY),
                Component.text("상태: " + state.name(), getStateColor(state)),
                Component.text("완료 시 제작대 청사진 해금 예정", NamedTextColor.DARK_GRAY)
        ));

        icon.setItemMeta(meta);
        return icon;
    }

    private NamedTextColor getStateColor(ResearchState state) {
        return switch (state) {
            case NOT_STARTED -> NamedTextColor.GRAY;
            case RESEARCHING -> NamedTextColor.YELLOW;
            case COMPLETED -> NamedTextColor.GREEN;
        };
    }
}