package com.frontier.research;

import com.frontier.config.ConfigManager;
import com.frontier.core.Manager;
import com.frontier.core.ManagerRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

/**
 * 연구 시스템 Manager.
 *
 * Feature 002: 연구 시작 → 시간 경과 → 완료 (05_Research §20: 제작대 30초)
 */
public final class ResearchManager implements Manager {

    /** 샘플 연구 ID */
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
        this.researchRepository = new ResearchRepository(new ResearchYamlDataStore(plugin));
    }

    @Override
    public void shutdown() {
        researchRepository.saveAllLoaded();
        plugin.getLogger().info("[Research] 연구 데이터 저장 완료");
    }

    /**
     * 연구 소요 시간
     */
    public long getResearchDurationMillis(String researchId) {
        return switch (researchId) {
            case RESEARCH_CRAFTING_TABLE -> 30_000L;
            default -> 30_000L;
        };
    }

    /**
     * 연구 시작
     */
    public boolean startResearch(Player player, String researchId) {
        ResearchData data = getOrCreateResearch(player);

        if (data.getState(researchId) != ResearchState.NOT_STARTED) {
            return false;
        }

        long duration = getResearchDurationMillis(researchId);
        long endTime = System.currentTimeMillis() + duration;

        data.setState(researchId, ResearchState.RESEARCHING);
        data.setEndTime(researchId, endTime);

        researchRepository.save(data);

        if (configManager.isDebug()) {
            plugin.getLogger().info("[Debug] 연구 시작: "
                    + player.getName()
                    + " -> "
                    + researchId);
        }

        UUID uuid = player.getUniqueId();

        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> completeIfDue(uuid),
                duration / 50L
        );

        return true;
    }

    /**
     * 예약 완료 처리
     */
    private void completeIfDue(UUID uuid) {
        ResearchData data = researchRepository.getLoaded(uuid);

        if (data == null) {
            return;
        }

        boolean changed = refreshCompletion(data);

        if (changed) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null && player.isOnline()) {
                player.sendMessage(
                        Component.text("연구 완료: ", NamedTextColor.GREEN)
                                .append(Component.text("제작대", NamedTextColor.WHITE))
                );
            }
        }
    }

    /**
     * 완료 판정
     */
    public boolean refreshCompletion(ResearchData data) {

        long now = System.currentTimeMillis();
        boolean changed = false;

        for (Map.Entry<String, Long> entry : data.getResearchingEndTimes().entrySet()) {

            if (entry.getValue() <= now) {
                data.setState(entry.getKey(), ResearchState.COMPLETED);
                data.clearEndTime(entry.getKey());
                changed = true;
            }
        }

        if (changed) {
            researchRepository.save(data);
        }

        return changed;
    }

    /**
     * 남은 시간
     */
    public long getRemainingSeconds(ResearchData data, String researchId) {

        if (data.getState(researchId) != ResearchState.RESEARCHING) {
            return 0;
        }

        long remain = data.getEndTime(researchId) - System.currentTimeMillis();

        return remain <= 0 ? 0 : (remain + 999) / 1000;
    }

    /**
     * 연구 데이터 조회
     */
    public ResearchData getOrCreateResearch(Player player) {

        ResearchData data = researchRepository.loadOrCreate(player.getUniqueId());

        if (configManager.isDebug()) {
            plugin.getLogger().info(
                    "[Debug] Research 로드: "
                            + player.getName()
                            + " (기록된 연구="
                            + data.getTrackedCount()
                            + ")"
            );
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