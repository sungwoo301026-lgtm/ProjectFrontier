package com.frontier.data.player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 플레이어 데이터 모델.
 * Bukkit에 직접 의존하지 않는 순수 데이터 클래스.
 */
public final class PlayerData {

    private static final int CURRENT_DATA_VERSION = 1;

    private final UUID uuid;
    private int dataVersion;
    private String name;
    private final long firstJoin;
    private long lastSeen;
    private int level;
    private long experience;

    public PlayerData(UUID uuid, int dataVersion, String name, long firstJoin, long lastSeen, int level, long experience) {
        this.uuid = uuid;
        this.dataVersion = dataVersion;
        this.name = name;
        this.firstJoin = firstJoin;
        this.lastSeen = lastSeen;
        this.level = level;
        this.experience = experience;
    }

    public static PlayerData createNew(UUID uuid, String name) {
        long now = System.currentTimeMillis();
        return new PlayerData(uuid, CURRENT_DATA_VERSION, name, now, now, 1, 0L);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("data-version", dataVersion);
        map.put("uuid", uuid.toString());
        map.put("name", name);
        map.put("first-join", firstJoin);
        map.put("last-seen", lastSeen);
        map.put("level", level);
        map.put("experience", experience);

        return map;
    }

    public static PlayerData fromMap(UUID uuid, Map<String, Object> map) {
        return new PlayerData(
                uuid,
                toInt(map.get("data-version"), CURRENT_DATA_VERSION),
                (String) map.getOrDefault("name", "Unknown"),
                toLong(map.get("first-join")),
                toLong(map.get("last-seen")),
                toInt(map.get("level"), 1),
                toLong(map.get("experience"))
        );
    }

    private static long toLong(Object value) {
        return (value instanceof Number number) ? number.longValue() : 0L;
    }

    private static int toInt(Object value, int defaultValue) {
        return (value instanceof Number number) ? number.intValue() : defaultValue;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(int dataVersion) {
        this.dataVersion = dataVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFirstJoin() {
        return firstJoin;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }
}