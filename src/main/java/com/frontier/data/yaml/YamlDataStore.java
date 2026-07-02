package com.frontier.data.yaml;

import com.frontier.data.DataStore;
import com.frontier.data.player.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * PlayerData를 YAML 파일로 저장하는 DataStore 구현체.
 */
public final class YamlDataStore implements DataStore<PlayerData, UUID> {

    private final File playerFolder;

    public YamlDataStore(JavaPlugin plugin) {
        this.playerFolder = new File(plugin.getDataFolder(), "data/players");

        if (!playerFolder.exists()) {
            playerFolder.mkdirs();
        }
    }

    private File getPlayerFile(UUID uuid) {
        return new File(playerFolder, uuid + ".yml");
    }

    @Override
    public PlayerData load(UUID uuid) throws IOException {

        File file = getPlayerFile(uuid);

        if (!file.exists()) {
            return null;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        return new PlayerData(
                uuid,
                config.getInt("data-version", 1),
                config.getString("name", "Unknown"),
                config.getLong("first-join"),
                config.getLong("last-seen"),
                config.getInt("level", 1),
                config.getLong("experience", 0)
        );
    }

    @Override
    public void save(PlayerData data) throws IOException {

        File file = getPlayerFile(data.getUuid());

        FileConfiguration config = new YamlConfiguration();

        config.set("data-version", data.getDataVersion());
        config.set("uuid", data.getUuid().toString());
        config.set("name", data.getName());
        config.set("first-join", data.getFirstJoin());
        config.set("last-seen", data.getLastSeen());
        config.set("level", data.getLevel());
        config.set("experience", data.getExperience());

        config.save(file);
    }

    @Override
    public boolean exists(UUID uuid) {
        return getPlayerFile(uuid).exists();
    }

    @Override
    public void delete(UUID uuid) throws IOException {

        File file = getPlayerFile(uuid);

        if (file.exists()) {
            file.delete();
        }
    }
}