package com.frontier.material;

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
 * Frontier 재료 ItemStack 생성 팩토리.
 * PDC 키 "frontier_material"에 MaterialType 이름을 저장한다.
 */
public final class MaterialItemFactory {

    private static final String PDC_KEY = "frontier_material";

    private final NamespacedKey materialKey;

    public MaterialItemFactory(JavaPlugin plugin) {
        this.materialKey = new NamespacedKey(plugin, PDC_KEY);
    }

    /** 재료 아이템을 생성한다. */
    public ItemStack create(MaterialType type, int amount) {
        Material base = switch (type) {
            // 외형은 임시 — 전용 리소스팩/모델 도입 시 교체
            case PLANK -> Material.BRICK;
            case DIAMOND_FRAGMENT -> Material.AMETHYST_SHARD;
        };

        ItemStack item = new ItemStack(base, amount);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(type.getDisplayName(), NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
                Component.text("제작 재료", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        ));

        meta.getPersistentDataContainer()
                .set(materialKey, PersistentDataType.STRING, type.name());

        item.setItemMeta(meta);
        return item;
    }

    /** 아이템이 Frontier 재료인지 판별하고 종류를 반환한다. 아니면 null. */
    public MaterialType getMaterialType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        String stored = item.getItemMeta().getPersistentDataContainer()
                .get(materialKey, PersistentDataType.STRING);
        if (stored == null) {
            return null;
        }
        try {
            return MaterialType.valueOf(stored);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}