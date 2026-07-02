package com.frontier.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

/**
 * 클릭 동작을 람다로 받는 단순 버튼.
 */
public final class SimpleButton extends Button {

    private final BiConsumer<Player, InventoryClickEvent> onClick;

    public SimpleButton(String id, int slot, ItemStack icon, BiConsumer<Player, InventoryClickEvent> onClick) {
        super(id, slot, icon);
        this.onClick = onClick;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        if (!isEnabled()) {
            return;
        }

        onClick.accept(player, event);
    }
}