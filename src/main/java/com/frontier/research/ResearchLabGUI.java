package com.frontier.research;

import com.frontier.gui.BaseGUI;
import com.frontier.gui.button.SimpleButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * 연구소 첫 화면 GUI (27칸) — Feature 003A.
 * Tier 1 Resource만 진입 가능, Tier 2~6은 미구현 안내.
 * 명령어에 의존하지 않으며, 추후 연구소 가구 우클릭도 이 GUI를 연다.
 */
public final class ResearchLabGUI extends BaseGUI {

    // 27칸 중단 행에 6버튼: 중앙(13)을 비워 좌3/우3 대칭
    private static final int SLOT_TIER_1 = 10;
    private static final int SLOT_TIER_2 = 11;
    private static final int SLOT_TIER_3 = 12;
    private static final int SLOT_TIER_4 = 14;
    private static final int SLOT_TIER_5 = 15;
    private static final int SLOT_TIER_6 = 16;

    private final ResearchData researchData;
    private final ResearchManager researchManager;

    public ResearchLabGUI(ResearchData researchData, ResearchManager researchManager) {
        super(Component.text("연구소"), 27);
        this.researchData = researchData;
        this.researchManager = researchManager;
    }

    @Override
    protected void setup() {
        addButton(new SimpleButton("lab_tier_1", SLOT_TIER_1,
                createTierIcon(Material.CRAFTING_TABLE, 1, "Resource", true),
                (player, event) -> openTier1(player)
        ));
        addButton(new SimpleButton("lab_tier_2", SLOT_TIER_2,
                createTierIcon(Material.GRASS_BLOCK, 2, "Grassland", false),
                (player, event) -> sendNotImplemented(player)
        ));
        addButton(new SimpleButton("lab_tier_3", SLOT_TIER_3,
                createTierIcon(Material.SAND, 3, "Desert", false),
                (player, event) -> sendNotImplemented(player)
        ));
        addButton(new SimpleButton("lab_tier_4", SLOT_TIER_4,
                createTierIcon(Material.SNOW_BLOCK, 4, "Snow", false),
                (player, event) -> sendNotImplemented(player)
        ));
        addButton(new SimpleButton("lab_tier_5", SLOT_TIER_5,
                createTierIcon(Material.MAGMA_BLOCK, 5, "Lava", false),
                (player, event) -> sendNotImplemented(player)
        ));
        addButton(new SimpleButton("lab_tier_6", SLOT_TIER_6,
                createTierIcon(Material.END_STONE, 6, "Dimension", false),
                (player, event) -> sendNotImplemented(player)
        ));
    }

    private void openTier1(Player player) {
        new PlayerResearchGUI(researchData, researchManager).open(player);
    }

    private void sendNotImplemented(Player player) {
        player.sendMessage(Component.text("아직 구현되지 않았습니다.", NamedTextColor.YELLOW));
    }

    private ItemStack createTierIcon(Material material, int tier, String tierName, boolean available) {
        ItemStack icon = new ItemStack(material);
        ItemMeta meta = icon.getItemMeta();
        meta.displayName(Component.text("Tier " + tier + " — " + tierName,
                available ? NamedTextColor.AQUA : NamedTextColor.GRAY));
        meta.lore(List.of(
                available
                        ? Component.text("클릭하여 연구 목록 열기", NamedTextColor.YELLOW)
                        : Component.text("아직 구현되지 않았습니다.", NamedTextColor.DARK_GRAY)
        ));
        icon.setItemMeta(meta);
        return icon;
    }
}