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

import java.io.File;

public final class DataManager implements Manager {

    private final JavaPlugin plugin;
    private final ManagerRegistry registry;

    private ConfigManager configManager;
    private DataStore dataStore;
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
        this.configManager = registry.get(ConfigManager.class);

        File playersFolder = new File(plugin.getDataFolder(), "data/players");
        if (!playersFolder.exists() && !playersFolder.mkdirs()) {
            throw new IllegalStateException("데이터 폴더 생성 실패: " + playersFolder.getPath());
        }

        this.dataStore = new YamlDataStore(plugin);
        this.playerDataRepository = new PlayerDataRepository(dataStore);

        startAutoSaveTask();
    }

    @Override
    public void shutdown() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
            autoSaveTask = null;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = playerDataRepository.getLoaded(player.getUniqueId());
            if (data != null) {
                data.setLastSeen(System.currentTimeMillis());
            }
        }

        playerDataRepository.saveAllLoaded();
        plugin.getLogger().info("[Data] 온라인 플레이어 데이터 저장 완료");
    }

    private void startAutoSaveTask() {
        this.autoSaveIntervalSeconds = configManager.getAutoSaveIntervalSeconds();
        long intervalTicks = autoSaveIntervalSeconds * 20L;

        this.autoSaveTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            playerDataRepository.saveAllLoaded();

            if (configManager.isDebug()) {
                plugin.getLogger().info("[Debug] Auto-save 완료: "
                        + playerDataRepository.getLoadedCount() + "명");
            }
        }, intervalTicks, intervalTicks);
    }

    public void loadPlayer(Player player) {
        PlayerData data = playerDataRepository.loadOrCreate(player);
        data.setName(player.getName());

        if (configManager.isDebug()) {
            plugin.getLogger().info("[Debug] PlayerData 로드: " + player.getName()
                    + " (gold=" + data.getGold()
                    + ", tutorial=" + data.isTutorialCompleted() + ")");
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
        playerDataRepository.saveAllLoaded();
    }

    public PlayerDataRepository getPlayerDataRepository() {
        return playerDataRepository;
    }

    public DataStore getDataStore() {
        return dataStore;
    }

    public int getAutoSaveIntervalSeconds() {
        return autoSaveIntervalSeconds;
    }

    public int getLoadedPlayerCount() {
        return playerDataRepository.getLoadedCount();
    }
}