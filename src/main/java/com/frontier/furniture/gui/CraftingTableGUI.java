package com.frontier.furniture.gui;

import com.frontier.blueprint.BlueprintManager;
import com.frontier.gui.BaseGUI;
import com.frontier.gui.button.SimpleButton;
import com.frontier.material.MaterialItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class CraftingTableGUI extends BaseGUI {

    private static final int SLOT_PROCESSING = 11;
    private static final int SLOT_TOOLS = 13;
    private static final int SLOT_BLUEPRINTS = 15;

    private final BlueprintManager blueprintManager;
    private final MaterialItemFactory materialItemFactory;

    public CraftingTableGUI(BlueprintManager blueprintManager, MaterialItemFactory materialItemFactory) {
        super(Component.text("제작대"), 27);
        this.blueprintManager = blueprintManager;
        this.materialItemFactory = materialItemFactory;
    }

    @Override
    protected void setup() {
        addButton(new SimpleButton("craft_category_processing", SLOT_PROCESSING,
                createCategoryIcon(Material.BRICK, "가공", "목재를 판자 등으로 가공합니다."),
                (player, event) ->
                        new ProcessingGUI(blueprintManager, materialItemFactory).open(player)
        ));

        addButton(new SimpleButton("craft_category_tools", SLOT_TOOLS,
                createCategoryIcon(Material.STICK, "도구", "몽둥이, 포획틀 등 도구를 제작합니다."),
                (player, event) -> player.sendMessage(
                        Component.text("도구 제작은 아직 구현되지 않았습니다.", NamedTextColor.YELLOW))
        ));

        addButton(new SimpleButton("craft_category_blueprints", SLOT_BLUEPRINTS,
                createCategoryIcon(Material.PAPER, "청사진", "연구로 해금한 가구 청사진을 제작합니다."),
                (player, event) ->
                        new BlueprintCraftingGUI(blueprintManager, materialItemFactory).open(player)
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