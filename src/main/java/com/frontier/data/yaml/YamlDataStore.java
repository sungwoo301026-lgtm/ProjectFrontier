package com.frontier.data.yaml;

import com.frontier.data.DataStore;
import com.frontier.data.player.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

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
                config.getString("name", "Unknown"),
                config.getLong("first-join"),
                config.getLong("last-seen"),
                config.getLong("gold", 0L),
                config.getBoolean("tutorial-completed", false),
                PlayerData.CURRENT_DATA_VERSION
        );
    }

    @Override
    public void save(PlayerData data) throws IOException {
        File file = getPlayerFile(data.getUuid());

        FileConfiguration config = new YamlConfiguration();

        config.set("data-version", data.getDataVersion());
        config.set("name", data.getName());
        config.set("first-join", data.getFirstJoin());
        config.set("last-seen", data.getLastSeen());
        config.set("gold", data.getGold());
        config.set("tutorial-completed", data.isTutorialCompleted());

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