package com.frontier.research;

import com.frontier.data.DataStore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ResearchData 전용 YAML 저장소.
 * 경로: plugins/Frontier/data/research/<uuid>.yml
 *
 * v2에서 end-times 섹션 추가.
 * v1 파일(end-times 없음)도 안전하게 로드된다.
 */
public final class ResearchYamlDataStore implements DataStore<ResearchData, UUID> {

    private final File folder;

    public ResearchYamlDataStore(JavaPlugin plugin) {
        this.folder = new File(plugin.getDataFolder(), "data/research");
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("연구 데이터 폴더 생성 실패: " + folder.getPath());
        }
    }

    private File getFile(UUID id) {
        return new File(folder, id + ".yml");
    }

    @Override
    public ResearchData load(UUID id) throws IOException {
        File file = getFile(id);
        if (!file.exists()) {
            return null;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        Map<String, ResearchState> states = new LinkedHashMap<>();
        ConfigurationSection stateSection = yaml.getConfigurationSection("states");
        if (stateSection != null) {
            for (String key : stateSection.getKeys(false)) {
                ResearchState state = parseState(stateSection.getString(key));
                if (state != null) {
                    states.put(key, state);
                }
            }
        }

        Map<String, Long> endTimes = new LinkedHashMap<>();
        ConfigurationSection endSection = yaml.getConfigurationSection("end-times");
        if (endSection != null) {
            for (String key : endSection.getKeys(false)) {
                endTimes.put(key, endSection.getLong(key, 0L));
            }
        }

        return new ResearchData(
                id,
                states,
                endTimes,
                yaml.getLong("created-at", 0L),
                yaml.getLong("last-opened", 0L),
                ResearchData.CURRENT_DATA_VERSION
        );
    }

    @Override
    public void save(ResearchData data) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("data-version", data.getDataVersion());
        yaml.set("created-at", data.getCreatedAt());
        yaml.set("last-opened", data.getLastOpened());

        for (Map.Entry<String, ResearchState> entry : data.getAllStates().entrySet()) {
            yaml.set("states." + entry.getKey(), entry.getValue().name());
        }
        for (Map.Entry<String, Long> entry : data.getAllEndTimes().entrySet()) {
            yaml.set("end-times." + entry.getKey(), entry.getValue());
        }

        yaml.save(getFile(data.getPlayer()));
    }

    @Override
    public boolean exists(UUID id) {
        return getFile(id).exists();
    }

    @Override
    public void delete(UUID id) throws IOException {
        File file = getFile(id);
        if (file.exists() && !file.delete()) {
            throw new IOException("연구 데이터 삭제 실패: " + file.getPath());
        }
    }

    /** 알 수 없는 상태 문자열은 무시 (구버전/수동편집 안전장치) */
    private ResearchState parseState(String name) {
        if (name == null) {
            return null;
        }
        try {
            return ResearchState.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}