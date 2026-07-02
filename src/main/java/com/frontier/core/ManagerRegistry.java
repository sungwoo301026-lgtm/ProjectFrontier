package com.frontier.core;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager들의 등록/초기화/종료를 담당한다.
 * - 초기화: 등록 순서대로
 * - 종료: 등록 역순으로 (의존성 안전)
 */
public final class ManagerRegistry {

    private final JavaPlugin plugin;
    private final Map<Class<? extends Manager>, Manager> managers = new LinkedHashMap<>();

    public ManagerRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /** Manager를 등록한다. 초기화는 initializeAll()에서 일괄 수행한다. */
    public <T extends Manager> void register(T manager) {
        managers.put(manager.getClass(), manager);
    }

    /** 등록된 다른 Manager를 조회한다. (Manager 간 의존성 해결용) */
    @SuppressWarnings("unchecked")
    public <T extends Manager> T get(Class<T> type) {
        Manager manager = managers.get(type);
        if (manager == null) {
            throw new IllegalStateException("등록되지 않은 Manager: " + type.getSimpleName());
        }
        return (T) manager;
    }

    /**
     * 모든 Manager를 등록 순서대로 초기화한다.
     * @return 전부 성공하면 true, 하나라도 실패하면 false
     */
    public boolean initializeAll() {
        for (Manager manager : managers.values()) {
            try {
                manager.initialize();
                plugin.getLogger().info("[" + manager.getName() + "] 초기화 완료");
            } catch (Exception e) {
                plugin.getLogger().severe("[" + manager.getName() + "] 초기화 실패: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /** 모든 Manager를 등록 역순으로 종료한다. */
    public void shutdownAll() {
        List<Manager> reversed = new ArrayList<>(managers.values());
        for (int i = reversed.size() - 1; i >= 0; i--) {
            Manager manager = reversed.get(i);
            try {
                manager.shutdown();
                plugin.getLogger().info("[" + manager.getName() + "] 종료 완료");
            } catch (Exception e) {
                plugin.getLogger().warning("[" + manager.getName() + "] 종료 중 오류: " + e.getMessage());
            }
        }
        managers.clear();
    }
}