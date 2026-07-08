package com.frontier.furniture.listener;

import com.frontier.blueprint.BlueprintManager;
import com.frontier.furniture.FurnitureData;
import com.frontier.furniture.FurnitureManager;
import com.frontier.furniture.FurnitureType;
import com.frontier.furniture.gui.CraftingTableGUI;
import com.frontier.material.MaterialItemFactory;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class FurnitureInteractListener implements Listener {

    private final FurnitureManager furnitureManager;
    private final BlueprintManager blueprintManager;
    private final MaterialItemFactory materialItemFactory;

    public FurnitureInteractListener(FurnitureManager furnitureManager,
                                     BlueprintManager blueprintManager,
                                     MaterialItemFactory materialItemFactory) {
        this.furnitureManager = furnitureManager;
        this.blueprintManager = blueprintManager;
        this.materialItemFactory = materialItemFactory;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }

        ItemStack item = event.getItem();
        if (item != null && blueprintManager.getItemFactory().getBlueprintType(item) != null) {
            return;
        }

        FurnitureData furniture = furnitureManager.getFurniture(clicked.getLocation());
        if (furniture == null) {
            return;
        }

        if (furniture.getType() != FurnitureType.CRAFTING_TABLE) {
            return;
        }

        event.setCancelled(true);
        new CraftingTableGUI(blueprintManager, materialItemFactory).open(event.getPlayer());
    }
}