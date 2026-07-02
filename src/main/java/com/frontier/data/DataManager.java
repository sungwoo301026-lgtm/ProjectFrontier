package com.frontier.data;

import com.frontier.config.ConfigManager;
import com.frontier.core.Manager;
import com.frontier.core.ManagerRegistry;
import com.frontier.data.player.PlayerData;
import com.frontier.data.player.PlayerDataRepository;
import com.frontier.data.yaml.YamlDataStore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class DataManager implements Manager {

    private final JavaPlugin plugin;
    private final ManagerRegistry registry;

    private ConfigManager configManager;
    private PlayerDataRepository playerDataRepository;
    private BukkitTask autoSaveTask;
    private int autoSaveIntervalSeconds;

    public DataManager(JavaPlugin plugin, ManagerRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "Data";
    }

    @Override
    public void initialize() {
        configManager = registry.get(ConfigManager.class);

        YamlDataStore dataStore = new YamlDataStore(plugin);
        playerDataRepository = new PlayerDataRepository(dataStore);

        startAutoSaveTask();
    }

    @Override
    public void shutdown() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
            autoSaveTask = null;
        }

        saveAllOnlinePlayers();

        if (configManager != null && configManager.isDebug()) {
            plugin.getLogger().info("[Debug] DataManager 종료 저장 완료");
        }
    }

    private void startAutoSaveTask() {
        autoSaveIntervalSeconds = configManager.getAutoSaveIntervalSeconds();

        long intervalTicks = Math.max(20L, autoSaveIntervalSeconds * 20L);

        autoSaveTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            saveAllOnlinePlayers();

            if (configManager.isDebug()) {
                plugin.getLogger().info("[Debug] Auto-save 완료: "
                        + playerDataRepository.getLoadedCount() + "명");
            }

        }, intervalTicks, intervalTicks);
    }

    public void loadPlayer(Player player) {
        PlayerData data = playerDataRepository.loadOrCreate(player);

        if (configManager.isDebug()) {
            plugin.getLogger().info("[Debug] PlayerData 로드: "
                    + data.getName()
                    + " level="
                    + data.getLevel());
        }
    }

    public void savePlayer(Player player) {
        PlayerData data = playerDataRepository.getLoaded(player.getUniqueId());

        if (data == null) {
            return;
        }

        data.setLastSeen(System.currentTimeMillis());
        playerDataRepository.saveLoaded(player.getUniqueId());
        playerDataRepository.unload(player.getUniqueId());

        if (configManager.isDebug()) {
            plugin.getLogger().info("[Debug] PlayerData 저장: " + player.getName());
        }
    }

    public void saveAllOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = playerDataRepository.getLoaded(player.getUniqueId());

            if (data != null) {
                data.setLastSeen(System.currentTimeMillis());
            }
        }

        playerDataRepository.saveAllLoaded();
    }

    public PlayerDataRepository getPlayerDataRepository() {
        return playerDataRepository;
    }

    public int getLoadedPlayerCount() {
        return playerDataRepository.getLoadedCount();
    }

    public int getAutoSaveIntervalSeconds() {
        return autoSaveIntervalSeconds;
    }
}