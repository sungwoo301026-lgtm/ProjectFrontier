package com.frontier.event;

import com.frontier.config.ConfigManager;
import com.frontier.core.Manager;
import com.frontier.core.ManagerRegistry;
import com.frontier.event.listeners.BlockInteractionListener;
import com.frontier.event.listeners.PlayerConnectionListener;
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
        Logger logger = plugin.getLogger();

        registerListener(new PlayerConnectionListener(logger, configManager));
        registerListener(new BlockInteractionListener(logger, configManager));
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