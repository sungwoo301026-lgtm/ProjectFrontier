package com.frontier.research;

import com.frontier.config.ConfigManager;
import com.frontier.core.Manager;
import com.frontier.core.ManagerRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 연구 시스템 Manager.
 */
public final class ResearchManager implements Manager {

    public static final String RESEARCH_CRAFTING_TABLE = "crafting_table";

    private final JavaPlugin plugin;
    private final ManagerRegistry registry;

    private ConfigManager configManager;
    private ResearchRepository researchRepository;

    public ResearchManager(JavaPlugin plugin, ManagerRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "Research";
    }

    @Override
    public void initialize() {
        this.configManager = registry.get(ConfigManager.class);

        ResearchYamlDataStore dataStore = new ResearchYamlDataStore(plugin);
        this.researchRepository = new ResearchRepository(dataStore);
    }

    @Override
    public void shutdown() {
        researchRepository.saveAllLoaded();

        if (configManager != null && configManager.isDebug()) {
            plugin.getLogger().info("[Debug] Research 데이터 저장 완료");
        }
    }

    public ResearchData getOrCreateResearch(Player player) {
        ResearchData data = researchRepository.loadOrCreate(player.getUniqueId());

        if (configManager.isDebug()) {
            plugin.getLogger().info("[Debug] Research 로드: "
                    + player.getName()
                    + " tracked="
                    + data.getTrackedCount());
        }

        return data;
    }

    public void saveResearch(ResearchData data) {
        researchRepository.save(data);
    }

    public ResearchRepository getResearchRepository() {
        return researchRepository;
    }

    public int getLoadedResearchCount() {
        return researchRepository.getLoadedCount();
    }
}