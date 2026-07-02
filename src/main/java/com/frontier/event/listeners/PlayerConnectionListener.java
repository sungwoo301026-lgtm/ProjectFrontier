package com.frontier.event.listeners;

import com.frontier.data.DataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * 플레이어 접속/퇴장 시 데이터 로드/저장을 트리거한다.
 */
public final class PlayerConnectionListener implements Listener {

    private final DataManager dataManager;

    public PlayerConnectionListener(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        dataManager.loadPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        dataManager.savePlayer(event.getPlayer());
    }
}