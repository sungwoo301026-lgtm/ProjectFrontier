package com.frontier.event.listeners;

import com.frontier.config.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.logging.Logger;

public final class BlockInteractionListener implements Listener {

    private final Logger logger;
    private final ConfigManager configManager;

    public BlockInteractionListener(Logger logger, ConfigManager configManager) {
        this.logger = logger;
        this.configManager = configManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (configManager.isDebug()) {
            logger.info("[Debug] Block broken: "
                    + event.getBlock().getType()
                    + " by "
                    + event.getPlayer().getName());
        }
    }
}