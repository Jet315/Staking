package me.jet315.staking.listeners;

import me.jet315.staking.Core;
import me.jet315.staking.StakePlayer;
import me.jet315.staking.utils.InvUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class StakingInventoryDrag implements Listener {

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTitle().equals(Core.getInstance().getGui().getStakeInventoryName())) {
            Player p = (Player) e.getWhoClicked();
            StakePlayer playerWhoClicked = Core.getInstance().getStakingPlayerManager().getStakePlayer(p);


            for (int slot : e.getNewItems().keySet()) {
                if (!InvUtils.validLeftSlots.contains(slot)) {
                    //it has been put on the other side
                    e.setCancelled(true);
                    e.setResult(Event.Result.DENY);
                    return;
                }
            }

            for (int slot : e.getNewItems().keySet()) {
                playerWhoClicked.getStakeInventory().updateInventory(e.getNewItems().get(slot), slot, playerWhoClicked.getOpponent().getPlayer().getOpenInventory().getTopInventory());
            }
            if (Core.getInstance().getGui().getAddItemNoise() != null) {
                playerWhoClicked.getPlayer().playSound(playerWhoClicked.getPlayer().getLocation(), Core.getInstance().getGui().getAddItemNoise(), 1, 1);
                playerWhoClicked.getOpponent().getPlayer().playSound(playerWhoClicked.getOpponent().getPlayer().getLocation(), Core.getInstance().getGui().getAddItemNoise(), 1, 1);
            }
            playerWhoClicked.getStakeInventory().unConfirmPlayers();

        }


    }

}
