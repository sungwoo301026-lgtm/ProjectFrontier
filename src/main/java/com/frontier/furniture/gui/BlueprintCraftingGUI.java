package com.frontier.furniture.gui;

import com.frontier.blueprint.BlueprintManager;
import com.frontier.blueprint.BlueprintType;
import com.frontier.gui.BaseGUI;
import com.frontier.gui.button.SimpleButton;
import com.frontier.material.MaterialItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class BlueprintCraftingGUI extends BaseGUI {

    private static final int CENTER_SLOT = 13;
    private static final int BACK_SLOT = 22;

    private final BlueprintManager blueprintManager;
    private final MaterialItemFactory materialItemFactory;

    public BlueprintCraftingGUI(BlueprintManager blueprintManager, MaterialItemFactory materialItemFactory) {
        super(Component.text("청사진 제작"), 27);
        this.blueprintManager = blueprintManager;
        this.materialItemFactory = materialItemFactory;
    }

    @Override
    protected void setup() {
        addButton(new SimpleButton("craft_blueprint_crafting_table", CENTER_SLOT, createBlueprintIcon(),
                (player, event) -> {
                    boolean crafted = blueprintManager.craftBlueprint(player, BlueprintType.CRAFTING_TABLE);
                    if (crafted) {
                        player.sendMessage(Component.text("제작대 청사진을 제작했습니다.", NamedTextColor.GREEN));
                    } else {
                        player.sendMessage(Component.text("연구가 완료되지 않아 제작할 수 없습니다.", NamedTextColor.RED));
                    }
                }
        ));

        addButton(new SimpleButton("craft_blueprint_back", BACK_SLOT, createBackIcon(),
                (player, event) ->
                        new CraftingTableGUI(blueprintManager, materialItemFactory).open(player)
        ));
    }

    private ItemStack createBlueprintIcon() {
        ItemStack icon = new ItemStack(Material.PAPER);
        ItemMeta meta = icon.getItemMeta();
        meta.displayName(Component.text("제작대 청사진", NamedTextColor.AQUA));
        meta.lore(List.of(
                Component.text("연구 완료 시 제작 가능", NamedTextColor.GRAY),
                Component.text("클릭하여 제작", NamedTextColor.YELLOW)
        ));
        icon.setItemMeta(meta);
        return icon;
    }

    private ItemStack createBackIcon() {
        ItemStack icon = new ItemStack(Material.ARROW);
        ItemMeta meta = icon.getItemMeta();
        meta.displayName(Component.text("뒤로가기", NamedTextColor.WHITE));
        meta.lore(List.of(Component.text("제작대로 돌아갑니다.", NamedTextColor.GRAY)));
        icon.setItemMeta(meta);
        return icon;
    }
}