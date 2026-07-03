package com.frontier.blueprint;

import com.frontier.blueprint.item.BlueprintItemFactory;
import com.frontier.core.Manager;
import com.frontier.core.ManagerRegistry;
import com.frontier.research.ResearchManager;
import com.frontier.research.ResearchState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 청사진 시스템 Manager.
 *
 * Feature 004: 연구 완료 기반 제작 가능 판단
 * Feature 005: 청사진 아이템 제작(지급) — craftBlueprint
 *
 * 아직 없음: 설치, 재료/골드 비용, 제작 시간 (이후 Feature)
 */
public final class BlueprintManager implements Manager {

    private final JavaPlugin plugin;
    private final ManagerRegistry registry;

    private ResearchManager researchManager;
    private BlueprintItemFactory itemFactory;

    public BlueprintManager(JavaPlugin plugin, ManagerRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @Override
    public String getName() {
        return "Blueprint";
    }

    @Override
    public void initialize() {
        this.researchManager = registry.get(ResearchManager.class);
        this.itemFactory = new BlueprintItemFactory(plugin);
    }

    @Override
    public void shutdown() {
        // 자체 저장 데이터 없음
    }

    /**
     * 해당 청사진을 제작할 수 있는지 판단한다.
     * 조건: 해금 연구가 COMPLETED.
     */
    public boolean canCraftBlueprint(Player player, BlueprintType type) {
        ResearchState state = researchManager.getOrCreateResearch(player)
                .getState(type.getRequiredResearchId());
        return state == ResearchState.COMPLETED;
    }

    /** 현재 제작 가능한 청사진 목록 */
    public List<BlueprintType> getCraftableBlueprints(Player player) {
        List<BlueprintType> result = new ArrayList<>();
        for (BlueprintType type : BlueprintType.values()) {
            if (canCraftBlueprint(player, type)) {
                result.add(type);
            }
        }
        return result;
    }

    /**
     * 청사진 아이템을 제작해 인벤토리에 지급한다.
     * 연구 미완료면 false. (중복 제작 제한 없음 — Feature 005 명세)
     * 인벤토리가 가득 차면 발밑에 드롭한다 (지급 유실 방지).
     */
    public boolean craftBlueprint(Player player, BlueprintType type) {
        if (!canCraftBlueprint(player, type)) {
            return false;
        }

        ItemStack item = itemFactory.create(type);
        Map<Integer, ItemStack> overflow = player.getInventory().addItem(item);

        // 인벤토리가 가득 찬 경우 남은 아이템을 발밑에 드롭
        for (ItemStack leftover : overflow.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }

        return true;
    }

    /** 아이템 → 청사진 종류 판별 창구 (이후 설치 Feature에서 사용) */
    public BlueprintItemFactory getItemFactory() {
        return itemFactory;
    }
}