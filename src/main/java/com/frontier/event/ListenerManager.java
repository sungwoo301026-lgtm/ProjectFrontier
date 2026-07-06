package com.frontier.event;

import com.frontier.blueprint.BlueprintManager;
import com.frontier.blueprint.listener.BlueprintPlaceListener;
import com.frontier.config.ConfigManager;
import com.frontier.core.Manager;
import com.frontier.core.ManagerRegistry;
import com.frontier.data.DataManager;
import com.frontier.event.listeners.BlockInteractionListener;
import com.frontier.event.listeners.PlayerConnectionListener;
import com.frontier.furniture.FurnitureManager;
import com.frontier.furniture.listener.FurnitureInteractListener;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class ListenerManager implements Manager {

    private final JavaPlugin plugin;
    private final ManagerRegistry registry;
    private final List<Listener> listeners = new ArrayList<>();

    public ListenerManager(JavaPlugin plugin, ManagerRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "Listener";
    }

    @Override
    public void initialize() {
        ConfigManager configManager = registry.get(ConfigManager.class);
        DataManager dataManager = registry.get(DataManager.class);
        BlueprintManager blueprintManager = registry.get(BlueprintManager.class);
        FurnitureManager furnitureManager = registry.get(FurnitureManager.class);
        Logger logger = plugin.getLogger();

        registerListener(new PlayerConnectionListener(dataManager));
        registerListener(new BlockInteractionListener(logger, configManager));
        registerListener(new BlueprintPlaceListener(blueprintManager, furnitureManager));
        registerListener(new FurnitureInteractListener(furnitureManager, blueprintManager));
    }

    @Override
    public void shutdown() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
        listeners.clear();
    }

    public <T extends Listener> T registerListener(T listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        listeners.add(listener);
        return listener;
    }

    public int getListenerCount() {
        return listeners.size();
    }

    public List<Listener> getListeners() {
        return List.copyOf(listeners);
    }
}