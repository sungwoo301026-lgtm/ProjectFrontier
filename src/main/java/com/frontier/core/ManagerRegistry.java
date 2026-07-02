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

    /** Manager를 등록한다. */
    public <T extends Manager> void register(T manager) {
        managers.put(manager.getClass(), manager);
    }

    /** Manager 조회 */
    @SuppressWarnings("unchecked")
    public <T extends Manager> T get(Class<T> type) {
        Manager manager = managers.get(type);

        if (manager == null) {
            throw new IllegalStateException("등록되지 않은 Manager : " + type.getSimpleName());
        }

        return (T) manager;
    }

    /** 등록된 Manager 수 */
    public int size() {
        return managers.size();
    }

    /** 등록된 Manager 초기화 */
    public boolean initializeAll() {

        for (Manager manager : managers.values()) {

            try {

                manager.initialize();

                plugin.getLogger().info("[" + manager.getName() + "] 초기화 완료");

            } catch (Exception e) {

                plugin.getLogger().severe("[" + manager.getName() + "] 초기화 실패");

                e.printStackTrace();

                return false;
            }

        }

        return true;
    }

    /** 등록 역순 종료 */
    public void shutdownAll() {

        List<Manager> reversed = new ArrayList<>(managers.values());

        for (int i = reversed.size() - 1; i >= 0; i--) {

            Manager manager = reversed.get(i);

            try {

                manager.shutdown();

                plugin.getLogger().info("[" + manager.getName() + "] 종료 완료");

            } catch (Exception e) {

                plugin.getLogger().warning("[" + manager.getName() + "] 종료 오류");

            }

        }

        managers.clear();
    }
}