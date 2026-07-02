package com.frontier.event.listeners;

import com.frontier.config.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

public final class PlayerConnectionListener implements Listener {

    private final Logger logger;
    private final ConfigManager configManager;

    public PlayerConnectionListener(Logger logger, ConfigManager configManager) {
        this.logger = logger;
        this.configManager = configManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (configManager.isDebug()) {
            logger.info("[Debug] Player joined: " + event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (configManager.isDebug()) {
            logger.info("[Debug] Player quit: " + event.getPlayer().getName());
        }
    }
}