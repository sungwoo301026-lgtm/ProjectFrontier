package com.frontier.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Inventory와 BaseGUI를 연결하는 Holder.
 */
public final class GUIHolder implements InventoryHolder {

    private final BaseGUI gui;
    private Inventory inventory;

    public GUIHolder(BaseGUI gui) {
        this.gui = gui;
    }

    public BaseGUI getGui() {
        return gui;
    }

    void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}