package com.frontier.base;

import com.frontier.config.ConfigManager;
import com.frontier.core.Manager;
import com.frontier.core.ManagerRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 거점 시스템 Manager.
 */
public final class BaseManager implements Manager {

    private final JavaPlugin plugin;
    private final ManagerRegistry registry;

    private ConfigManager configManager;
    private BaseRepository baseRepository;

    public BaseManager(JavaPlugin plugin, ManagerRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "Base";
    }

    @Override
    public void initialize() {
        this.configManager = registry.get(ConfigManager.class);

        BaseYamlDataStore dataStore = new BaseYamlDataStore(plugin);
        this.baseRepository = new BaseRepository(dataStore);
    }

    @Override
    public void shutdown() {
        baseRepository.saveAllLoaded();

        if (configManager != null && configManager.isDebug()) {
            plugin.getLogger().info("[Debug] Base 데이터 저장 완료");
        }
    }

    /**
     * 임시: 플레이어의 거점을 반환한다. 없으면 생성.
     * 추후 거점 지정권/NPC/빈 거점 선택 시스템으로 교체 예정.
     */
    public BaseData getOrCreateBase(Player player) {
        BaseData base = baseRepository.loadOrCreate(player.getUniqueId(), player.getName());
        base.setLastOpened(System.currentTimeMillis());

        if (configManager.isDebug()) {
            plugin.getLogger().info("[Debug] Base 로드: " + player.getName());
        }

        return base;
    }

    public void saveBase(BaseData base) {
        baseRepository.save(base);
    }

    public BaseRepository getBaseRepository() {
        return baseRepository;
    }

    public int getLoadedBaseCount() {
        return baseRepository.getLoadedCount();
    }
}