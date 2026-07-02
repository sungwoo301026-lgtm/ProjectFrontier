package com.frontier.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 모든 GUI 버튼의 부모 클래스.
 */
public abstract class Button {

    /** 버튼 고유 ID (예: research_start, pet_upgrade) */
    private final String id;

    /** 버튼이 위치할 슬롯 */
    private final int slot;

    /** 버튼 아이콘 */
    private final ItemStack icon;

    protected Button(String id, int slot, ItemStack icon) {
        this.id = id;
        this.slot = slot;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getIcon() {
        return icon;
    }

    /**
     * 버튼 표시 여부
     */
    public boolean isVisible() {
        return true;
    }

    /**
     * 버튼 활성화 여부
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * 버튼 클릭 시 실행
     */
    public abstract void onClick(Player player, InventoryClickEvent event);
}