package com.frontier.furniture.gui;

import com.frontier.blueprint.BlueprintManager;
import com.frontier.gui.BaseGUI;
import com.frontier.gui.button.SimpleButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Frontier 제작대 GUI (27칸) — Feature 010: 카테고리 첫 화면.
 *
 * 제작 분류 (07_Crafting §6):
 * - 가공: 목재 → 판자 등 재료 가공 (미구현)
 * - 도구: 몽둥이, 포획틀 등 (미구현)
 * - 청사진: 연구로 해금한 가구 청사진 제작 → BlueprintCraftingGUI
 */
public final class CraftingTableGUI extends BaseGUI {

    private static final int SLOT_PROCESSING = 11;
    private static final int SLOT_TOOLS = 13;
    private static final int SLOT_BLUEPRINTS = 15;

    private final BlueprintManager blueprintManager;

    public CraftingTableGUI(BlueprintManager blueprintManager) {
        super(Component.text("제작대"), 27);
        this.blueprintManager = blueprintManager;
    }

    @Override
    protected void setup() {
        // 가공 — 미구현
        addButton(new SimpleButton("craft_category_processing", SLOT_PROCESSING,
                createCategoryIcon(Material.OAK_PLANKS, "가공", "목재를 판자 등으로 가공합니다."),
                (player, event) -> player.sendMessage(
                        Component.text("가공 제작은 아직 구현되지 않았습니다.", NamedTextColor.YELLOW))
        ));

        // 도구 — 미구현
        addButton(new SimpleButton("craft_category_tools", SLOT_TOOLS,
                createCategoryIcon(Material.STICK, "도구", "몽둥이, 포획틀 등 도구를 제작합니다."),
                (player, event) -> player.sendMessage(
                        Component.text("도구 제작은 아직 구현되지 않았습니다.", NamedTextColor.YELLOW))
        ));

        // 청사진 — 하위 GUI로 진입
        addButton(new SimpleButton("craft_category_blueprints", SLOT_BLUEPRINTS,
                createCategoryIcon(Material.PAPER, "청사진", "연구로 해금한 가구 청사진을 제작합니다."),
                (player, event) ->
                        new BlueprintCraftingGUI(blueprintManager).open(player)
        ));
    }

    private ItemStack createCategoryIcon(Material material, String name, String description) {
        ItemStack icon = new ItemStack(material);
        ItemMeta meta = icon.getItemMeta();
        meta.displayName(Component.text(name, NamedTextColor.AQUA));
        meta.lore(List.of(Component.text(description, NamedTextColor.GRAY)));
        icon.setItemMeta(meta);
        return icon;
    }
}