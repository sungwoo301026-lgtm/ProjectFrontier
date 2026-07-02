package com.frontier.gui;

import com.frontier.gui.button.Button;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * 모든 GUI의 부모 클래스.
 * ResearchGUI, PetGUI, RegionGUI 등이 이 클래스를 상속한다.
 */
public abstract class BaseGUI {

    private final Component title;
    private final int size;

    private final Map<Integer, Button> buttons = new HashMap<>();

    protected BaseGUI(Component title, int size) {
        this.title = title;
        this.size = size;
    }

    /**
     * 하위 GUI에서 버튼을 배치하는 곳.
     */
    protected abstract void setup();

    protected void addButton(Button button) {
        if (button.isVisible()) {
            buttons.put(button.getSlot(), button);
        }
    }

    public Button getButton(int slot) {
        return buttons.get(slot);
    }

    /**
     * GUI를 플레이어에게 연다.
     */
    public void open(Player player) {

        buttons.clear();

        setup();

        GUIHolder holder = new GUIHolder(this);

        Inventory inventory = Bukkit.createInventory(holder, size, title);

        holder.setInventory(inventory);

        for (Button button : buttons.values()) {
            inventory.setItem(button.getSlot(), button.getIcon());
        }

        player.openInventory(inventory);
    }

    /**
     * GUI를 다시 그린다.
     * 앞으로 Research, Pet, Base GUI에서 자주 사용된다.
     */
    public void refresh(Player player) {
        open(player);
    }
}