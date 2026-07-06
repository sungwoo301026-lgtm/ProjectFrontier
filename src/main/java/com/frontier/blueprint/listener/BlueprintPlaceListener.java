package com.frontier.blueprint.listener;

import com.frontier.blueprint.BlueprintManager;
import com.frontier.blueprint.BlueprintType;
import com.frontier.furniture.FurnitureManager;
import com.frontier.furniture.FurnitureType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * 청사진 설치 리스너.
 * Feature 006: 우클릭 설치 + 청사진 소모
 * Feature 007A: 설치 성공 시 FurnitureManager에 등록
 */
public final class BlueprintPlaceListener implements Listener {

    private final BlueprintManager blueprintManager;
    private final FurnitureManager furnitureManager;

    public BlueprintPlaceListener(BlueprintManager blueprintManager, FurnitureManager furnitureManager) {
        this.blueprintManager = blueprintManager;
        this.furnitureManager = furnitureManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }

        BlueprintType type = blueprintManager.getItemFactory().getBlueprintType(item);
        if (type == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();

        Block target = clicked.getRelative(BlockFace.UP);
        if (!target.getType().isAir()) {
            player.sendMessage(Component.text("이곳에는 설치할 수 없습니다. 위 공간이 비어 있어야 합니다.", NamedTextColor.RED));
            return;
        }

        Material placeMaterial = switch (type) {
            case CRAFTING_TABLE -> Material.CRAFTING_TABLE;
        };

        FurnitureType furnitureType = switch (type) {
            case CRAFTING_TABLE -> FurnitureType.CRAFTING_TABLE;
        };

        // TODO: 현재는 설치 성공을 확인하기 위한 임시 바닐라 블록이다.
        // 이후 Frontier 제작 GUI를 연결한다.
        target.setType(placeMaterial);

        furnitureManager.registerFurniture(player, target.getLocation(), furnitureType);

        item.setAmount(item.getAmount() - 1);

        player.sendMessage(Component.text("제작대를 설치했습니다.", NamedTextColor.GREEN));
    }
}