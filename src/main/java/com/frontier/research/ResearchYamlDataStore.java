package com.frontier.research;

import com.frontier.data.DataStore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ResearchData를 YAML 파일로 저장하는 DataStore 구현체.
 */
public final class ResearchYamlDataStore implements DataStore<ResearchData, UUID> {

    private final File researchFolder;

    public ResearchYamlDataStore(JavaPlugin plugin) {
        this.researchFolder = new File(plugin.getDataFolder(), "data/research");

        if (!researchFolder.exists()) {
            researchFolder.mkdirs();
        }
    }

    private File getResearchFile(UUID uuid) {
        return new File(researchFolder, uuid + ".yml");
    }

    @Override
    public ResearchData load(UUID uuid) throws IOException {
        File file = getResearchFile(uuid);

        if (!file.exists()) {
            return null;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("data-version", config.getInt("data-version", 1));
        map.put("created-at", config.getLong("created-at"));
        map.put("last-opened", config.getLong("last-opened"));

        ConfigurationSection statesSection = config.getConfigurationSection("states");
        Map<String, Object> states = new LinkedHashMap<>();

        if (statesSection != null) {
            for (String key : statesSection.getKeys(false)) {
                states.put(key, statesSection.getString(key));
            }
        }

        map.put("states", states);

        return ResearchData.fromMap(uuid, map);
    }

    @Override
    public void save(ResearchData data) throws IOException {
        File file = getResearchFile(data.getPlayer());

        FileConfiguration config = new YamlConfiguration();

        Map<String, Object> map = data.toMap();

        config.set("data-version", map.get("data-version"));
        config.set("created-at", map.get("created-at"));
        config.set("last-opened", map.get("last-opened"));
        config.set("states", map.get("states"));

        config.save(file);
    }

    @Override
    public boolean exists(UUID uuid) {
        return getResearchFile(uuid).exists();
    }

    @Override
    public void delete(UUID uuid) throws IOException {
        File file = getResearchFile(uuid);

        if (file.exists()) {
            file.delete();
        }
    }
}