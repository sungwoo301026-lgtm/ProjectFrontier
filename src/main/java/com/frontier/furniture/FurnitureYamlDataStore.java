package com.frontier.furniture;

import com.frontier.data.DataStore;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * FurnitureData 전용 YAML 저장소.
 * 경로: plugins/Frontier/data/furniture/<furnitureId>.yml
 *
 * 역할: 단일 파일의 load/save/exists/delete만 담당한다.
 * 폴더 전체 스캔/일괄 로드는 FurnitureRepository의 책임이다.
 */
public final class FurnitureYamlDataStore implements DataStore<FurnitureData, UUID> {

    private final File folder;

    public FurnitureYamlDataStore(JavaPlugin plugin) {
        this.folder = new File(plugin.getDataFolder(), "data/furniture");
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("가구 데이터 폴더 생성 실패: " + folder.getPath());
        }
    }

    private File getFile(UUID id) {
        return new File(folder, id + ".yml");
    }

    @Override
    public FurnitureData load(UUID id) throws IOException {
        File file = getFile(id);
        if (!file.exists()) {
            return null;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        String idRaw = yaml.getString("furniture-id");
        String ownerRaw = yaml.getString("owner");
        String world = yaml.getString("world");
        String typeRaw = yaml.getString("type");

        if (idRaw == null || ownerRaw == null || world == null || typeRaw == null) {
            return null;
        }

        UUID furnitureId;
        UUID owner;
        FurnitureType type;

        try {
            furnitureId = UUID.fromString(idRaw);
            owner = UUID.fromString(ownerRaw);
            type = FurnitureType.valueOf(typeRaw);
        } catch (IllegalArgumentException e) {
            return null;
        }

        return new FurnitureData(
                furnitureId,
                owner,
                world,
                yaml.getInt("x"),
                yaml.getInt("y"),
                yaml.getInt("z"),
                type,
                yaml.getLong("placed-at", 0L),
                yaml.getInt("data-version", 1)
        );
    }

    @Override
    public void save(FurnitureData data) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();

        yaml.set("data-version", data.getDataVersion());
        yaml.set("furniture-id", data.getFurnitureId().toString());
        yaml.set("owner", data.getOwner().toString());
        yaml.set("world", data.getWorldName());
        yaml.set("x", data.getX());
        yaml.set("y", data.getY());
        yaml.set("z", data.getZ());
        yaml.set("type", data.getType().name());
        yaml.set("placed-at", data.getPlacedAt());

        yaml.save(getFile(data.getFurnitureId()));
    }

    @Override
    public boolean exists(UUID id) {
        return getFile(id).exists();
    }

    @Override
    public void delete(UUID id) throws IOException {
        File file = getFile(id);

        if (file.exists() && !file.delete()) {
            throw new IOException("가구 데이터 삭제 실패: " + file.getPath());
        }
    }

    /**
     * furniture 폴더에 존재하는 UUID 목록.
     */
    public List<UUID> listIds() {
        List<UUID> result = new ArrayList<>();

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return result;
        }

        for (File file : files) {
            String name = file.getName();
            String idPart = name.substring(0, name.length() - 4);

            try {
                result.add(UUID.fromString(idPart));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return result;
    }
}