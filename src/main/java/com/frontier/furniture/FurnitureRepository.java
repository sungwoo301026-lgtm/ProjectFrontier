package com.frontier.furniture;

import com.frontier.data.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FurnitureData 저장소.
 * 캐시(furnitureId → 가구) + FurnitureYamlDataStore 위임.
 * 폴더 전체 스캔/일괄 로드는 이 클래스의 책임이다.
 */
public final class FurnitureRepository implements Repository<FurnitureData, UUID> {

    private final FurnitureYamlDataStore dataStore;
    private final Map<UUID, FurnitureData> loaded = new ConcurrentHashMap<>();

    public FurnitureRepository(FurnitureYamlDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public FurnitureData load(UUID id) {
        try {
            return dataStore.load(id);
        } catch (IOException e) {
            throw new IllegalStateException("가구 데이터 로드 실패: " + id, e);
        }
    }

    @Override
    public void save(FurnitureData data) {
        try {
            dataStore.save(data);
        } catch (IOException e) {
            throw new IllegalStateException("가구 데이터 저장 실패: " + data.getFurnitureId(), e);
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
            throw new IllegalStateException("가구 데이터 삭제 실패: " + id, e);
        }
    }

    public int loadAllIntoCache() {
        for (UUID id : dataStore.listIds()) {
            FurnitureData data = load(id);
            if (data != null) {
                loaded.put(data.getFurnitureId(), data);
            }
        }
        return loaded.size();
    }

    public void register(FurnitureData data) {
        loaded.put(data.getFurnitureId(), data);
        save(data);
    }

    public FurnitureData getLoaded(UUID id) {
        return loaded.get(id);
    }

    public Collection<FurnitureData> getAllLoaded() {
        return new ArrayList<>(loaded.values());
    }

    public void saveAllLoaded() {
        for (FurnitureData data : loaded.values()) {
            save(data);
        }
    }

    public int getLoadedCount() {
        return loaded.size();
    }
}