package com.frontier.gui;

import com.frontier.core.Manager;
import com.frontier.core.ManagerRegistry;
import com.frontier.event.ListenerManager;
import com.frontier.gui.button.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public final class GUIManager implements Manager, Listener {

    private final JavaPlugin plugin;
    private final ManagerRegistry registry;

    public GUIManager(JavaPlugin plugin, ManagerRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "GUI";
    }

    @Override
    public void initialize() {
        registry.get(ListenerManager.class).registerListener(this);
    }

    @Override
    public void shutdown() {
        // ListenerManager가 일괄 해제
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        InventoryHolder holder = event.getInventory().getHolder();

        if (!(holder instanceof GUIHolder guiHolder)) {
            return;
        }

        // GUI에서는 아이템 이동 금지
        event.setCancelled(true);

        // 플레이어 인벤토리 클릭은 무시
        if (event.getClickedInventory() != event.getInventory()) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Button button = guiHolder.getGui().getButton(event.getSlot());

        if (button != null) {
            button.onClick(player, event);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof GUIHolder) {
            event.setCancelled(true);
        }
    }
}