package com.frontier.furniture.gui;

import com.frontier.blueprint.BlueprintManager;
import com.frontier.gui.BaseGUI;
import com.frontier.gui.button.SimpleButton;
import com.frontier.material.MaterialItemFactory;
import com.frontier.material.MaterialType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

/**
 * 가공 GUI (27칸) — Feature 011.
 * 원목 → 판자(Frontier 재료), 다이아 → 다이아 조각(Frontier 재료). (07_Crafting §5)
 *
 * 가공 비율은 밸런스 미확정 — 아래 상수는 임시값이다.
 */
public final class ProcessingGUI extends BaseGUI {

    private static final int SLOT_PLANKS = 11;
    private static final int SLOT_DIAMOND_FRAGMENT = 15;
    private static final int BACK_SLOT = 22;

    // ── 임시 가공 비율 (밸런스 확정 시 조정) ──
    private static final int PLANKS_PER_LOG = 4;
    private static final int FRAGMENTS_PER_DIAMOND = 1;

    private final BlueprintManager blueprintManager;
    private final MaterialItemFactory materialItemFactory;

    public ProcessingGUI(BlueprintManager blueprintManager, MaterialItemFactory materialItemFactory) {
        super(Component.text("가공"), 27);
        this.blueprintManager = blueprintManager;
        this.materialItemFactory = materialItemFactory;
    }

    @Override
    protected void setup() {
        // 판자: 참나무 원목 1 차감 → Frontier 판자 지급
        addButton(new SimpleButton("processing_planks", SLOT_PLANKS,
                createIcon(Material.OAK_PLANKS, "판자", "원목을 판자로 가공합니다."),
                (player, event) -> processPlanks(player)
        ));

        // 다이아 조각: 다이아 1 차감 → Frontier 다이아 조각 지급
        addButton(new SimpleButton("processing_diamond_fragment", SLOT_DIAMOND_FRAGMENT,
                createIcon(Material.DIAMOND, "다이아 조각", "다이아를 조각으로 가공합니다."),
                (player, event) -> processDiamondFragment(player)
        ));

        // 뒤로가기 → 제작대 카테고리 화면
        addButton(new SimpleButton("processing_back", BACK_SLOT, createBackIcon(),
                (player, event) ->
                        new CraftingTableGUI(blueprintManager, materialItemFactory).open(player)
        ));
    }

    private void processPlanks(Player player) {
        ItemStack cost = new ItemStack(Material.OAK_LOG, 1);
        if (!player.getInventory().containsAtLeast(cost, 1)) {
            player.sendMessage(Component.text("원목이 부족합니다.", NamedTextColor.RED));
            return;
        }
        player.getInventory().removeItem(cost);
        giveOrDrop(player, materialItemFactory.create(MaterialType.PLANK, PLANKS_PER_LOG));
        player.sendMessage(Component.text("판자를 제작했습니다.", NamedTextColor.GREEN));
    }

    private void processDiamondFragment(Player player) {
        ItemStack cost = new ItemStack(Material.DIAMOND, 1);
        if (!player.getInventory().containsAtLeast(cost, 1)) {
            player.sendMessage(Component.text("다이아가 부족합니다.", NamedTextColor.RED));
            return;
        }
        player.getInventory().removeItem(cost);
        giveOrDrop(player, materialItemFactory.create(MaterialType.DIAMOND_FRAGMENT, FRAGMENTS_PER_DIAMOND));
        player.sendMessage(Component.text("다이아 조각을 제작했습니다.", NamedTextColor.GREEN));
    }

    /** 인벤토리 지급, 가득 차면 발밑 드롭 (유실 방지) */
    private void giveOrDrop(Player player, ItemStack item) {
        Map<Integer, ItemStack> overflow = player.getInventory().addItem(item);
        for (ItemStack leftover : overflow.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }

    private ItemStack createIcon(Material material, String name, String description) {
        ItemStack icon = new ItemStack(material);
        ItemMeta meta = icon.getItemMeta();
        meta.displayName(Component.text(name, NamedTextColor.AQUA));
        meta.lore(List.of(Component.text(description, NamedTextColor.GRAY)));
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