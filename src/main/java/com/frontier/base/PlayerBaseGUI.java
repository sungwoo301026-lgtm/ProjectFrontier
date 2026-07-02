package com.frontier.base;

import com.frontier.gui.BaseGUI;
import com.frontier.gui.button.SimpleButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 플레이어 거점 GUI.
 * Phase 6에서는 정보 표시만 담당한다.
 */
public final class PlayerBaseGUI extends BaseGUI {

    private static final int CENTER_SLOT = 13;

    private final BaseData baseData;

    public PlayerBaseGUI(BaseData baseData) {
        super(Component.text("거점 - " + baseData.getName()), 27);
        this.baseData = baseData;
    }

    @Override
    protected void setup() {
        addButton(new SimpleButton(
                "base_info",
                CENTER_SLOT,
                createInfoIcon(),
                (player, event) -> player.sendMessage(Component.text("Base clicked", NamedTextColor.YELLOW))
        ));
    }

    private ItemStack createInfoIcon() {
        ItemStack icon = new ItemStack(Material.CAMPFIRE);
        ItemMeta meta = icon.getItemMeta();

        meta.displayName(Component.text(baseData.getName(), NamedTextColor.GOLD));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        meta.lore(List.of(
                Component.text("소유자: " + baseData.getOwner().toString().substring(0, 8) + "...", NamedTextColor.GRAY),
                Component.text("생성일: " + format.format(new Date(baseData.getCreatedAt())), NamedTextColor.GRAY),
                Component.text("시설: 아직 없음", NamedTextColor.DARK_GRAY)
        ));

        icon.setItemMeta(meta);
        return icon;
    }
}