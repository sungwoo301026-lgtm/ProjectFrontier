package com.frontier.research;

import com.frontier.data.DataStore;
import com.frontier.data.Repository;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ResearchData 저장소.
 * 캐시(Map) + DataStore<ResearchData, UUID> 위임.
 * DataStore의 IOException은 여기서 IllegalStateException으로 감싼다.
 */
public final class ResearchRepository implements Repository<ResearchData, UUID> {

    private final DataStore<ResearchData, UUID> dataStore;
    private final Map<UUID, ResearchData> loaded = new ConcurrentHashMap<>();

    public ResearchRepository(DataStore<ResearchData, UUID> dataStore) {
        this.dataStore = dataStore;
    }

    // ── Repository 구현 ──

    /** 저장소에서 읽는다. 없으면 null. */
    @Override
    public ResearchData load(UUID id) {
        try {
            return dataStore.load(id);
        } catch (IOException e) {
            throw new IllegalStateException("연구 데이터 로드 실패: " + id, e);
        }
    }

    @Override
    public void save(ResearchData data) {
        try {
            dataStore.save(data);
        } catch (IOException e) {
            throw new IllegalStateException("연구 데이터 저장 실패: " + data.getPlayer(), e);
        }
    }

    @Override
    public boolean exists(UUID id) {
        return dataStore.exists(id);
    }

    @Override
    public void delete(UUID id) {
        loaded.remove(id);
        try {
            dataStore.delete(id);
        } catch (IOException e) {
            throw new IllegalStateException("연구 데이터 삭제 실패: " + id, e);
        }
    }

    // ── 캐시 관리 ──

    public ResearchData getLoaded(UUID id) {
        return loaded.get(id);
    }

    /** 저장소에서 로드하거나, 없으면 새로 생성해서 캐시에 올린다. */
    public ResearchData loadOrCreate(UUID player) {
        return loaded.computeIfAbsent(player, key -> {
            ResearchData data = load(key);
            return (data != null) ? data : ResearchData.createNew(key);
        });
    }

    public void saveLoaded(UUID id) {
        ResearchData data = loaded.get(id);
        if (data != null) {
            save(data);
        }
    }

    public void saveAllLoaded() {
        for (ResearchData data : loaded.values()) {
            save(data);
        }
    }

    public void unload(UUID id) {
        loaded.remove(id);
    }

    public int getLoadedCount() {
        return loaded.size();
    }
}