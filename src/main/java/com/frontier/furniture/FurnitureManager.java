package com.frontier.furniture;

import com.frontier.core.Manager;
import com.frontier.core.ManagerRegistry;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 가구 시스템 Manager — Furniture Storage MVP.
 *
 * - Repository 키: furnitureId(UUID)
 * - 위치 검색: locationIndex
 * - 서버 재시작 시 저장된 가구를 복원한다.
 */
public final class FurnitureManager implements Manager {

    private record LocationKey(String worldName, int x, int y, int z) {

        static LocationKey of(Location location) {
            return new LocationKey(
                    location.getWorld().getName(),
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ()
            );
        }

        static LocationKey of(FurnitureData data) {
            return new LocationKey(
                    data.getWorldName(),
                    data.getX(),
                    data.getY(),
                    data.getZ()
            );
        }
    }

    private final JavaPlugin plugin;
    private final ManagerRegistry registry;

    private FurnitureRepository furnitureRepository;
    private final Map<LocationKey, UUID> locationIndex = new ConcurrentHashMap<>();

    public FurnitureManager(JavaPlugin plugin, ManagerRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "Furniture";
    }

    @Override
    public void initialize() {
        this.furnitureRepository = new FurnitureRepository(new FurnitureYamlDataStore(plugin));

        int restored = furnitureRepository.loadAllIntoCache();

        for (FurnitureData data : furnitureRepository.getAllLoaded()) {
            locationIndex.put(LocationKey.of(data), data.getFurnitureId());
        }

        plugin.getLogger().info("[Furniture] 가구 " + restored + "개 복원 완료");
    }

    @Override
    public void shutdown() {
        furnitureRepository.saveAllLoaded();
        locationIndex.clear();
        plugin.getLogger().info("[Furniture] 가구 데이터 저장 완료");
    }

    public void registerFurniture(Player player, Location location, FurnitureType type) {
        UUID furnitureId = UUID.randomUUID();

        FurnitureData data = new FurnitureData(
                furnitureId,
                player.getUniqueId(),
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                type,
                System.currentTimeMillis(),
                FurnitureData.CURRENT_DATA_VERSION
        );

        furnitureRepository.register(data);
        locationIndex.put(LocationKey.of(location), furnitureId);
    }

    public boolean isFurniture(Location location) {
        return locationIndex.containsKey(LocationKey.of(location));
    }

    public FurnitureData getFurniture(Location location) {
        UUID furnitureId = locationIndex.get(LocationKey.of(location));

        if (furnitureId == null) {
            return null;
        }

        return furnitureRepository.getLoaded(furnitureId);
    }

    public int getFurnitureCount() {
        return furnitureRepository.getLoadedCount();
    }

    public FurnitureRepository getFurnitureRepository() {
        return furnitureRepository;
    }
}