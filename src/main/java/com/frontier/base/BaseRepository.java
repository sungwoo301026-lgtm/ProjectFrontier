package com.frontier.base;

import com.frontier.data.DataStore;
import com.frontier.data.Repository;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BaseRepository implements Repository<BaseData, UUID> {

    private final DataStore<BaseData, UUID> dataStore;
    private final Map<UUID, BaseData> loadedBases = new ConcurrentHashMap<>();

    public BaseRepository(DataStore<BaseData, UUID> dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public BaseData load(UUID uuid) {
        try {
            return dataStore.load(uuid);
        } catch (IOException e) {
            throw new IllegalStateException("BaseData 로드 실패: " + uuid, e);
        }
    }

    public BaseData loadOrCreate(UUID uuid, String ownerName) {
        BaseData loaded = loadedBases.get(uuid);

        if (loaded != null) {
            return loaded;
        }

        BaseData data = load(uuid);

        if (data == null) {
            data = BaseData.createNew(uuid, ownerName);
        }

        loadedBases.put(uuid, data);

        return data;
    }

    @Override
    public void save(BaseData data) {
        try {
            dataStore.save(data);
        } catch (IOException e) {
            throw new IllegalStateException("BaseData 저장 실패: " + data.getOwner(), e);
        }
    }

    @Override
    public boolean exists(UUID uuid) {
        return dataStore.exists(uuid);
    }

    @Override
    public void delete(UUID uuid) {
        try {
            loadedBases.remove(uuid);
            dataStore.delete(uuid);
        } catch (IOException e) {
            throw new IllegalStateException("BaseData 삭제 실패: " + uuid, e);
        }
    }

    public BaseData getLoaded(UUID uuid) {
        return loadedBases.get(uuid);
    }

    public void saveLoaded(UUID uuid) {
        BaseData data = loadedBases.get(uuid);

        if (data != null) {
            save(data);
        }
    }

    public void saveAllLoaded() {
        for (BaseData data : loadedBases.values()) {
            save(data);
        }
    }

    public void unload(UUID uuid) {
        loadedBases.remove(uuid);
    }

    public int getLoadedCount() {
        return loadedBases.size();
    }
}