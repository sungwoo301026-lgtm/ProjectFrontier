package com.frontier.blueprint;

/**
 * 청사진 종류 — Blueprint MVP.
 *
 * 각 청사진은 해금 조건이 되는 연구 ID와 표시 이름을 가진다.
 * (19_Item_Database §12: 청사진은 연구 완료 후 제작 가능)
 *
 * 이후 Tier 1~6 청사진이 늘어나면 이 enum에 상수만 추가하면 된다.
 */
public enum BlueprintType {

    CRAFTING_TABLE("crafting_table", "제작대 청사진");

    /** 해금 조건 연구 ID (ResearchManager의 연구 ID와 일치해야 한다) */
    private final String requiredResearchId;

    /** 채팅/GUI 표시 이름 */
    private final String displayName;

    BlueprintType(String requiredResearchId, String displayName) {
        this.requiredResearchId = requiredResearchId;
        this.displayName = displayName;
    }

    public String getRequiredResearchId() {
        return requiredResearchId;
    }

    public String getDisplayName() {
        return displayName;
    }
}