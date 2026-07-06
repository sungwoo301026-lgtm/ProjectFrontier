package com.frontier.furniture;

import java.util.UUID;

/**
 * 설치된 가구 하나의 데이터.
 * 가구 자체의 고유 UUID(furnitureId)가 저장 파일명이자 Repository 키다.
 * 위치는 데이터의 속성일 뿐이며, 위치 → 가구 검색은
 * FurnitureManager의 locationIndex가 담당한다.
 */
public final class FurnitureData {

    public static final int CURRENT_DATA_VERSION = 1;

    private final UUID furnitureId;
    private final UUID owner;
    private final String worldName;
    private final int x;
    private final int y;
    private final int z;
    private final FurnitureType type;
    private final long placedAt;
    private final int dataVersion;

    public FurnitureData(UUID furnitureId, UUID owner, String worldName, int x, int y, int z,
                         FurnitureType type, long placedAt, int dataVersion) {
        this.furnitureId = furnitureId;
        this.owner = owner;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.placedAt = placedAt;
        this.dataVersion = dataVersion;
    }

    public UUID getFurnitureId() {
        return furnitureId;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public FurnitureType getType() {
        return type;
    }

    public long getPlacedAt() {
        return placedAt;
    }

    public int getDataVersion() {
        return dataVersion;
    }
}