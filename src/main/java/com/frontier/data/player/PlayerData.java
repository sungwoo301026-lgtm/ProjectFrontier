package com.frontier.data.player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 플레이어 데이터 모델.
 *
 * dataVersion 2 (MD 정합화):
 * - level/experience 제거 — 15_UI_UX §8 "플레이어 레벨 시스템은 존재하지 않는다"
 * - gold 추가 — 14_Economy (연구/제작/강화/인챈트/직업 변경의 핵심 재화)
 * - tutorial-completed 추가 — 튜토리얼 완료 → 거점 선택권 흐름의 선행 조건
 *
 * v1 파일 마이그레이션: fromMap이 구필드(level, experience)를 무시하고
 * 다음 저장 시 자연 소멸한다. 별도 마이그레이션 코드 불필요.
 */
public final class PlayerData {

    public static final int CURRENT_DATA_VERSION = 2;

    private final UUID uuid;
    private String name;
    private final long firstJoin;
    private long lastSeen;
    private long gold;
    private boolean tutorialCompleted;
    private final int dataVersion;

    public PlayerData(UUID uuid, String name, long firstJoin, long lastSeen,
                      long gold, boolean tutorialCompleted, int dataVersion) {
        this.uuid = uuid;
        this.name = name;
        this.firstJoin = firstJoin;
        this.lastSeen = lastSeen;
        this.gold = gold;
        this.tutorialCompleted = tutorialCompleted;
        this.dataVersion = dataVersion;
    }

    /** 신규 플레이어용 초기 데이터 생성 */
    public static PlayerData createNew(UUID uuid, String name) {
        long now = System.currentTimeMillis();
        return new PlayerData(uuid, name, now, now, 0L, false, CURRENT_DATA_VERSION);
    }

    // ── 골드 (14_Economy) ──

    public long getGold() {
        return gold;
    }

    public void setGold(long gold) {
        this.gold = Math.max(0L, gold);
    }

    public void addGold(long amount) {
        if (amount > 0) {
            this.gold += amount;
        }
    }

    /**
     * 골드를 차감한다. 잔액이 부족하면 차감하지 않는다.
     * @return 차감 성공 여부
     */
    public boolean takeGold(long amount) {
        if (amount <= 0 || gold < amount) {
            return false;
        }
        this.gold -= amount;
        return true;
    }

    // ── 직렬화 ──

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("data-version", dataVersion);
        map.put("name", name);
        map.put("first-join", firstJoin);
        map.put("last-seen", lastSeen);
        map.put("gold", gold);
        map.put("tutorial-completed", tutorialCompleted);
        return map;
    }

    public static PlayerData fromMap(UUID uuid, Map<String, Object> map) {
        return new PlayerData(
                uuid,
                (String) map.getOrDefault("name", "Unknown"),
                toLong(map.get("first-join")),
                toLong(map.get("last-seen")),
                toLong(map.get("gold")),
                toBoolean(map.get("tutorial-completed")),
                toInt(map.get("data-version"), 1)
        );
    }

    private static long toLong(Object value) {
        return (value instanceof Number number) ? number.longValue() : 0L;
    }

    private static int toInt(Object value, int def) {
        return (value instanceof Number number) ? number.intValue() : def;
    }

    private static boolean toBoolean(Object value) {
        return (value instanceof Boolean bool) && bool;
    }

    // ── Getter / Setter ──

    public UUID getUuid() {
        return uuid;
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

    public boolean isTutorialCompleted() {
        return tutorialCompleted;
    }

    public void setTutorialCompleted(boolean tutorialCompleted) {
        this.tutorialCompleted = tutorialCompleted;
    }

    public int getDataVersion() {
        return dataVersion;
    }
}