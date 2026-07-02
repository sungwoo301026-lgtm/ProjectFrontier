package com.frontier.data.player;

import com.frontier.data.DataStore;
import com.frontier.data.Repository;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PlayerData 저장소.
 * - 메모리 캐시 관리
 * - DataStore를 통한 실제 저장/로드
 */
public final class PlayerDataRepository implements Repository<PlayerData, UUID> {

    private final DataStore<PlayerData, UUID> dataStore;
    private final Map<UUID, PlayerData> loadedPlayers = new ConcurrentHashMap<>();

    public PlayerDataRepository(DataStore<PlayerData, UUID> dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public PlayerData load(UUID uuid) {
        try {
            return dataStore.load(uuid);
        } catch (IOException e) {
            throw new IllegalStateException("PlayerData 로드 실패: " + uuid, e);
        }
    }

    public PlayerData loadOrCreate(Player player) {
        UUID uuid = player.getUniqueId();

        PlayerData loaded = loadedPlayers.get(uuid);
        if (loaded != null) {
            return loaded;
        }

        PlayerData data = load(uuid);

        if (data == null) {
            data = PlayerData.createNew(uuid, player.getName());
        }

        data.setName(player.getName());
        loadedPlayers.put(uuid, data);

        return data;
    }

    @Override
    public void save(PlayerData data) {
        try {
            dataStore.save(data);
        } catch (IOException e) {
            throw new IllegalStateException("PlayerData 저장 실패: " + data.getUuid(), e);
        }
    }

    public void saveLoaded(UUID uuid) {
        PlayerData data = loadedPlayers.get(uuid);

        if (data != null) {
            data.setLastSeen(System.currentTimeMillis());
            save(data);
        }
    }

    public void saveAllLoaded() {
        for (PlayerData data : loadedPlayers.values()) {
            data.setLastSeen(System.currentTimeMillis());
            save(data);
        }
    }

    public PlayerData getLoaded(UUID uuid) {
        return loadedPlayers.get(uuid);
    }

    public Collection<PlayerData> getLoadedPlayers() {
        return loadedPlayers.values();
    }

    public int getLoadedCount() {
        return loadedPlayers.size();
    }

    public void unload(UUID uuid) {
        loadedPlayers.remove(uuid);
    }

    @Override
    public boolean exists(UUID uuid) {
        return dataStore.exists(uuid);
    }

    @Override
    public void delete(UUID uuid) {
        try {
            loadedPlayers.remove(uuid);
            dataStore.delete(uuid);
        } catch (IOException e) {
            throw new IllegalStateException("PlayerData 삭제 실패: " + uuid, e);
        }
    }
}