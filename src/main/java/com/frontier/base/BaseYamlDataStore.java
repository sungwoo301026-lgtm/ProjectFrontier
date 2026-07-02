package com.frontier.base;

import com.frontier.data.DataStore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * BaseData를 YAML 파일로 저장하는 DataStore 구현체.
 */
public final class BaseYamlDataStore implements DataStore<BaseData, UUID> {

    private final File baseFolder;

    public BaseYamlDataStore(JavaPlugin plugin) {
        this.baseFolder = new File(plugin.getDataFolder(), "data/bases");

        if (!baseFolder.exists()) {
            baseFolder.mkdirs();
        }
    }

    private File getBaseFile(UUID uuid) {
        return new File(baseFolder, uuid + ".yml");
    }

    @Override
    public BaseData load(UUID uuid) throws IOException {
        File file = getBaseFile(uuid);

        if (!file.exists()) {
            return null;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        return new BaseData(
                uuid,
                config.getInt("data-version", 1),
                config.getString("name", "이름 없는 거점"),
                config.getLong("created-at"),
                config.getLong("last-opened")
        );
    }

    @Override
    public void save(BaseData data) throws IOException {
        File file = getBaseFile(data.getOwner());

        FileConfiguration config = new YamlConfiguration();

        config.set("data-version", data.getDataVersion());
        config.set("name", data.getName());
        config.set("created-at", data.getCreatedAt());
        config.set("last-opened", data.getLastOpened());

        config.save(file);
    }

    @Override
    public boolean exists(UUID uuid) {
        return getBaseFile(uuid).exists();
    }

    @Override
    public void delete(UUID uuid) throws IOException {
        File file = getBaseFile(uuid);

        if (file.exists()) {
            file.delete();
        }
    }
}