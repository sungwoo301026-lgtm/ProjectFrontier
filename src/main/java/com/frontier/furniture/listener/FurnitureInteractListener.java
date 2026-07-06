package com.frontier.furniture.listener;

import com.frontier.blueprint.BlueprintManager;
import com.frontier.furniture.FurnitureData;
import com.frontier.furniture.FurnitureManager;
import com.frontier.furniture.FurnitureType;
import com.frontier.furniture.gui.CraftingTableGUI;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * 가구 상호작용 리스너.
 * 등록된 CRAFTING_TABLE 가구 우클릭 → 바닐라 제작대 차단 + Frontier 제작 GUI.
 *
 * 등록되지 않은 (일반) 제작대는 바닐라 동작을 유지한다.
 * 손에 청사진을 들고 있으면 통과시킨다 — 설치(BlueprintPlaceListener)가 우선.
 */
public final class FurnitureInteractListener implements Listener {

    private final FurnitureManager furnitureManager;
    private final BlueprintManager blueprintManager;

    public FurnitureInteractListener(FurnitureManager furnitureManager, BlueprintManager blueprintManager) {
        this.furnitureManager = furnitureManager;
        this.blueprintManager = blueprintManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        // 주손만 처리 (HAND/OFF_HAND 이중 발화 → GUI 이중 오픈 방지)
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }

        // 청사진 설치가 우선 — 청사진을 들고 있으면 이 리스너는 관여하지 않는다
        ItemStack item = event.getItem();
        if (item != null && blueprintManager.getItemFactory().getBlueprintType(item) != null) {
            return;
        }

        // 등록된 가구인지 확인 (일반 제작대는 바닐라 유지)
        FurnitureData furniture = furnitureManager.getFurniture(clicked.getLocation());
        if (furniture == null) {
            return;
        }

        if (furniture.getType() != FurnitureType.CRAFTING_TABLE) {
            return; // 다른 가구 종류는 이후 Feature에서 처리
        }

        // 바닐라 제작대 GUI 차단 → Frontier GUI
        event.setCancelled(true);
        new CraftingTableGUI(blueprintManager).open(event.getPlayer()); // ← 인자 변경
    }
}