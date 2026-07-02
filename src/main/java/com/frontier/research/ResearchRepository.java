package com.frontier.research;

import com.frontier.data.DataStore;
import com.frontier.data.Repository;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ResearchData 저장소.
 */
public final class ResearchRepository implements Repository<ResearchData, UUID> {

    private final DataStore<ResearchData, UUID> dataStore;
    private final Map<UUID, ResearchData> loadedResearch = new ConcurrentHashMap<>();

    public ResearchRepository(DataStore<ResearchData, UUID> dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public ResearchData load(UUID uuid) {
        try {
            return dataStore.load(uuid);
        } catch (IOException e) {
            throw new IllegalStateException("ResearchData 로드 실패: " + uuid, e);
        }
    }

    public ResearchData loadOrCreate(UUID uuid) {
        ResearchData loaded = loadedResearch.get(uuid);

        if (loaded != null) {
            return loaded;
        }

        ResearchData data = load(uuid);

        if (data == null) {
            data = ResearchData.createNew(uuid);
        }

        loadedResearch.put(uuid, data);

        return data;
    }

    @Override
    public void save(ResearchData data) {
        try {
            dataStore.save(data);
        } catch (IOException e) {
            throw new IllegalStateException("ResearchData 저장 실패: " + data.getPlayer(), e);
        }
    }

    @Override
    public boolean exists(UUID uuid) {
        return dataStore.exists(uuid);
    }

    @Override
    public void delete(UUID uuid) {
        try {
            loadedResearch.remove(uuid);
            dataStore.delete(uuid);
        } catch (IOException e) {
            throw new IllegalStateException("ResearchData 삭제 실패: " + uuid, e);
        }
    }

    public void saveLoaded(UUID uuid) {
        ResearchData data = loadedResearch.get(uuid);

        if (data != null) {
            save(data);
        }
    }

    public void saveAllLoaded() {
        for (ResearchData data : loadedResearch.values()) {
            save(data);
        }
    }

    public void unload(UUID uuid) {
        loadedResearch.remove(uuid);
    }

    public int getLoadedCount() {
        return loadedResearch.size();
    }
}