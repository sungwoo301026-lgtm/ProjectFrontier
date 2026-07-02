package com.frontier.gui;

import com.frontier.gui.button.SimpleButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class TestGUI extends BaseGUI {

    private final JavaPlugin plugin;

    public TestGUI(JavaPlugin plugin) {
        super(Component.text("Frontier Test GUI"), 9);
        this.plugin = plugin;
    }

    @Override
    protected void setup() {
        ItemStack stone = new ItemStack(Material.STONE);

        ItemMeta meta = stone.getItemMeta();
        meta.displayName(Component.text("테스트 버튼", NamedTextColor.YELLOW));
        stone.setItemMeta(meta);

        addButton(new SimpleButton(
                "test_button",
                4,
                stone,
                (player, event) -> plugin.getLogger().info("Button Clicked: " + player.getName())
        ));
    }
}