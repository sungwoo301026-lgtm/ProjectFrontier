package com.frontier.config;

import com.frontier.core.Manager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * config.yml 로드/리로드를 담당한다.
 */
public final class ConfigManager implements Manager {

    private final JavaPlugin plugin;
    private boolean debug;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Config";
    }

    @Override
    public void initialize() {
        plugin.saveDefaultConfig(); // plugins/Frontier/config.yml이 없을 때만 생성
        reload();
    }

    @Override
    public void shutdown() {
        // 현재는 저장할 상태 없음
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        this.debug = config.getBoolean("debug", false);
    }

    public boolean isDebug() {
        return debug;
    }
}