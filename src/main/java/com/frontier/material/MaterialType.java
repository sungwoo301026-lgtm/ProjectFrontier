package com.frontier.material;

/**
 * Frontier 재료 종류.
 * Blueprint도 Furniture도 아닌 일반 재료 아이템.
 * 바닐라 아이템과 구분되는 Frontier 전용 재료만 이 enum + PDC로 식별한다.
 */
public enum MaterialType {

    PLANK("판자"),
    DIAMOND_FRAGMENT("다이아 조각");

    private final String displayName;

    MaterialType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}