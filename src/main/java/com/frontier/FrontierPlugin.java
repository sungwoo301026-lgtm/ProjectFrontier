package com.frontier;

import com.frontier.base.BaseManager;
import com.frontier.blueprint.BlueprintManager; // ← 추가
import com.frontier.command.CommandManager;
import com.frontier.config.ConfigManager;
import com.frontier.core.ManagerRegistry;
import com.frontier.data.DataManager;
import com.frontier.event.ListenerManager;
import com.frontier.gui.GUIManager;
import com.frontier.research.ResearchManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project Frontier 진입점.
 * 로직을 담지 않으며, Manager 등록과 라이프사이클 위임만 수행한다.
 */
public final class FrontierPlugin extends JavaPlugin {

    private ManagerRegistry registry;

    @Override
    public void onEnable() {
        registry = new ManagerRegistry(this);

        // ── Manager 등록 (이후 Phase에서 이 블록에만 추가하면 된다) ──
        registry.register(new ConfigManager(this));
        registry.register(new DataManager(this, registry));
        registry.register(new BaseManager(this, registry));
        registry.register(new ResearchManager(this, registry));
        registry.register(new BlueprintManager(this, registry)); // ← 추가 (Research 이후, Command 이전)
        registry.register(new CommandManager(this, registry));
        registry.register(new ListenerManager(this, registry));
        registry.register(new GUIManager(this, registry));

        if (!registry.initializeAll()) {
            getLogger().severe("초기화 실패 — 플러그인을 비활성화합니다.");
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