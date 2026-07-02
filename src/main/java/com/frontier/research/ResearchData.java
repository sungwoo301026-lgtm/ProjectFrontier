package com.frontier.research;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 플레이어별 연구 데이터.
 * 실제 연구 정의는 나중에 별도 Registry/YAML로 분리하고,
 * 이 클래스는 "플레이어가 어떤 연구를 어떤 상태로 갖고 있는지"만 저장한다.
 */
public final class ResearchData {

    private static final int CURRENT_DATA_VERSION = 1;

    private final UUID player;
    private int dataVersion;
    private final Map<String, ResearchState> states;
    private final long createdAt;
    private long lastOpened;

    public ResearchData(
            UUID player,
            int dataVersion,
            Map<String, ResearchState> states,
            long createdAt,
            long lastOpened
    ) {
        this.player = player;
        this.dataVersion = dataVersion;
        this.states = new ConcurrentHashMap<>(states);
        this.createdAt = createdAt;
        this.lastOpened = lastOpened;
    }

    public static ResearchData createNew(UUID player) {
        long now = System.currentTimeMillis();

        return new ResearchData(
                player,
                CURRENT_DATA_VERSION,
                Map.of(),
                now,
                now
        );
    }

    public ResearchState getState(String researchId) {
        return states.getOrDefault(researchId, ResearchState.NOT_STARTED);
    }

    public void setState(String researchId, ResearchState state) {
        states.put(researchId, state);
    }

    public int getTrackedCount() {
        return states.size();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("data-version", dataVersion);
        map.put("created-at", createdAt);
        map.put("last-opened", lastOpened);

        Map<String, Object> stateMap = new LinkedHashMap<>();
        for (Map.Entry<String, ResearchState> entry : states.entrySet()) {
            stateMap.put(entry.getKey(), entry.getValue().name());
        }

        map.put("states", stateMap);

        return map;
    }

    @SuppressWarnings("unchecked")
    public static ResearchData fromMap(UUID player, Map<String, Object> map) {
        Map<String, ResearchState> loadedStates = new LinkedHashMap<>();

        Object rawStates = map.get("states");
        if (rawStates instanceof Map<?, ?> stateMap) {
            for (Map.Entry<?, ?> entry : stateMap.entrySet()) {
                ResearchState state = parseState(String.valueOf(entry.getValue()));
                if (state != null) {
                    loadedStates.put(String.valueOf(entry.getKey()), state);
                }
            }
        }

        return new ResearchData(
                player,
                toInt(map.get("data-version"), CURRENT_DATA_VERSION),
                loadedStates,
                toLong(map.get("created-at")),
                toLong(map.get("last-opened"))
        );
    }

    private static ResearchState parseState(String name) {
        try {
            return ResearchState.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static long toLong(Object value) {
        return (value instanceof Number number) ? number.longValue() : 0L;
    }

    private static int toInt(Object value, int defaultValue) {
        return (value instanceof Number number) ? number.intValue() : defaultValue;
    }

    public UUID getPlayer() {
        return player;
    }

    public int getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(int dataVersion) {
        this.dataVersion = dataVersion;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastOpened() {
        return lastOpened;
    }

    public void setLastOpened(long lastOpened) {
        this.lastOpened = lastOpened;
    }
}