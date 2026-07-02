package com.frontier;

import com.frontier.command.CommandManager;
import com.frontier.config.ConfigManager;
import com.frontier.core.ManagerRegistry;
import com.frontier.event.ListenerManager;
import com.frontier.gui.GUIManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project Frontier 진입점.
 * 실제 로직은 Manager들에게 위임한다.
 */
public final class FrontierPlugin extends JavaPlugin {

    private ManagerRegistry registry;

    @Override
    public void onEnable() {

        registry = new ManagerRegistry(this);

        // ── Manager 등록 ─────────────────────────────
        registry.register(new ConfigManager(this));
        registry.register(new CommandManager(this, registry));
        registry.register(new ListenerManager(this, registry));
        registry.register(new GUIManager(this, registry));

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