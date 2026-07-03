package com.frontier.research;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 플레이어별 연구 데이터 모델 (순수 데이터, 직렬화는 ResearchYamlDataStore 담당).
 *
 * dataVersion 2:
 * - endTimes 추가 (RESEARCHING 상태 연구의 완료 예정 시각, epoch ms)
 */
public final class ResearchData {

    public static final int CURRENT_DATA_VERSION = 2;

    private final UUID player;
    private final Map<String, ResearchState> states;
    private final Map<String, Long> endTimes;
    private final long createdAt;
    private long lastOpened;
    private final int dataVersion;

    public ResearchData(UUID player, Map<String, ResearchState> states, Map<String, Long> endTimes,
                        long createdAt, long lastOpened, int dataVersion) {
        this.player = player;
        this.states = new ConcurrentHashMap<>(states);
        this.endTimes = new ConcurrentHashMap<>(endTimes);
        this.createdAt = createdAt;
        this.lastOpened = lastOpened;
        this.dataVersion = dataVersion;
    }

    /** 신규 플레이어용 초기 데이터 생성 */
    public static ResearchData createNew(UUID player) {
        long now = System.currentTimeMillis();
        return new ResearchData(player, Map.of(), Map.of(), now, now, CURRENT_DATA_VERSION);
    }

    // ── 상태 조회/변경 ──

    /** 해당 연구의 상태. 기록이 없으면 NOT_STARTED. */
    public ResearchState getState(String researchId) {
        return states.getOrDefault(researchId, ResearchState.NOT_STARTED);
    }

    public void setState(String researchId, ResearchState state) {
        states.put(researchId, state);
    }

    /** 완료 예정 시각(epoch ms). 없으면 0. */
    public long getEndTime(String researchId) {
        return endTimes.getOrDefault(researchId, 0L);
    }

    public void setEndTime(String researchId, long endTime) {
        endTimes.put(researchId, endTime);
    }

    public void clearEndTime(String researchId) {
        endTimes.remove(researchId);
    }

    /** RESEARCHING 상태인 연구의 (ID → 완료 예정 시각) 복사본. 완료 판정용. */
    public Map<String, Long> getResearchingEndTimes() {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Map.Entry<String, ResearchState> entry : states.entrySet()) {
            if (entry.getValue() == ResearchState.RESEARCHING) {
                result.put(entry.getKey(), getEndTime(entry.getKey()));
            }
        }
        return result;
    }

    /** 직렬화용 상태 복사본 (ResearchYamlDataStore가 사용) */
    public Map<String, ResearchState> getAllStates() {
        return new LinkedHashMap<>(states);
    }

    /** 직렬화용 종료시각 복사본 (ResearchYamlDataStore가 사용) */
    public Map<String, Long> getAllEndTimes() {
        return new LinkedHashMap<>(endTimes);
    }

    public int getTrackedCount() {
        return states.size();
    }

    // ── Getter / Setter ──

    public UUID getPlayer() {
        return player;
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

    public int getDataVersion() {
        return dataVersion;
    }
}