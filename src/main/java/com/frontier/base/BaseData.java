package com.frontier.base;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 플레이어 거점 데이터.
 * 거점은 '레벨'이 아니라 시설과 연구를 통해 성장한다.
 */
public final class BaseData {

    private static final int CURRENT_DATA_VERSION = 1;

    private final UUID owner;
    private int dataVersion;
    private String name;
    private final long createdAt;
    private long lastOpened;

    public BaseData(
            UUID owner,
            int dataVersion,
            String name,
            long createdAt,
            long lastOpened
    ) {
        this.owner = owner;
        this.dataVersion = dataVersion;
        this.name = name;
        this.createdAt = createdAt;
        this.lastOpened = lastOpened;
    }

    public static BaseData createNew(UUID owner, String ownerName) {
        long now = System.currentTimeMillis();

        return new BaseData(
                owner,
                CURRENT_DATA_VERSION,
                ownerName + "의 거점",
                now,
                now
        );
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("data-version", dataVersion);
        map.put("name", name);
        map.put("created-at", createdAt);
        map.put("last-opened", lastOpened);

        return map;
    }

    public UUID getOwner() {
        return owner;
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