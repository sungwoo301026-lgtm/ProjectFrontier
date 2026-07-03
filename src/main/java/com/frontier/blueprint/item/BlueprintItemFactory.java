package com.frontier.blueprint.item;

import com.frontier.blueprint.BlueprintType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * 청사진 ItemStack 생성 팩토리 — Feature 005.
 *
 * PDC 키 "frontier_blueprint"에 BlueprintType 이름을 저장한다.
 * 이후 설치 Feature에서 이 PDC로 "우클릭한 아이템이 어떤 청사진인지" 판별한다.
 * (19_Item_Database §12: 청사진은 실제 아이템)
 */
public final class BlueprintItemFactory {

    private static final String PDC_KEY = "frontier_blueprint";

    private final NamespacedKey blueprintKey;

    public BlueprintItemFactory(JavaPlugin plugin) {
        this.blueprintKey = new NamespacedKey(plugin, PDC_KEY);
    }

    /** 청사진 아이템을 생성한다. */
    public ItemStack create(BlueprintType type) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        // §b = AQUA. Adventure 방식으로 동일 색 지정 (기울임 자동 적용 방지 포함)
        meta.displayName(Component.text(type.getDisplayName(), NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(List.of(
                Component.text("청사진", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("연구 완료", NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("거점에서 설치 가능(예정)", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        ));

        // 청사진 식별 정보 저장
        meta.getPersistentDataContainer()
                .set(blueprintKey, PersistentDataType.STRING, type.name());

        item.setItemMeta(meta);
        return item;
    }

    /**
     * 아이템이 청사진인지 판별하고 종류를 반환한다. 아니면 null.
     * (이후 설치 Feature에서 사용 예정)
     */
    public BlueprintType getBlueprintType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        String stored = item.getItemMeta().getPersistentDataContainer()
                .get(blueprintKey, PersistentDataType.STRING);
        if (stored == null) {
            return null;
        }
        try {
            return BlueprintType.valueOf(stored);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}