package com.frontier.furniture;

/**
 * 가구 종류 — Furniture Storage MVP.
 * (19_Item_Database §13: 시설은 거점에 설치되는 실제 구조물)
 */
public enum FurnitureType {

    CRAFTING_TABLE("제작대");

    private final String displayName;

    FurnitureType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}