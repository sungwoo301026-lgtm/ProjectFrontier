package com.frontier;

import com.frontier.command.CommandManager;
import com.frontier.config.ConfigManager;
import com.frontier.core.ManagerRegistry;
import com.frontier.event.ListenerManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FrontierPlugin extends JavaPlugin {

    private ManagerRegistry registry;

    @Override
    public void onEnable() {
        registry = new ManagerRegistry(this);

        registry.register(new ConfigManager(this));
        registry.register(new CommandManager(this, registry));
        registry.register(new ListenerManager(this, registry));

        if (!registry.initializeAll()) {
            getLogger().severe("초기화 실패 - 플러그인을 비활성화합니다.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Project Frontier v" + getPluginMeta().getVersion() + " 활성화 완료");
    }

    @Override
    public void onDisable() {
        if (registry != null) {
            registry.shutdownAll();
        }

        getLogger().info("Project Frontier 비활성화 완료");
    }

    public ManagerRegistry getRegistry() {
        return registry;
    }
}