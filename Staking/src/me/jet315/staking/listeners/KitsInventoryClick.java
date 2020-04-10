package me.jet315.staking.listeners;

import me.jet315.staking.Core;
import me.jet315.staking.StakePlayer;
import me.jet315.staking.arenas.inventory.IInterfaceKitSave;
import me.jet315.staking.utils.InvUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

public class KitsInventoryClick implements Listener {


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (e.getView().getTitle().equals(Core.getInstance().getKitManager().getInventoryName())) {
            for(IInterfaceKitSave kitSelected : Core.getInstance().getKitManager().getAvailableKits().values()) {
                if(kitSelected.isKitClicked(e.getCurrentItem())){
                    Player p = (Player) e.getWhoClicked();
                    StakePlayer stakePlayer = Core.getInstance().getStakingPlayerManager().getStakePlayer(p);
                    if(stakePlayer != null) {
                        stakePlayer.setHasSelectedKit(true);
                        p.closeInventory();
                        InvUtils.loadKitSave(p,kitSelected);
                        e.setCancelled(true);
                        break;
                    }

                }

            }

        }
    }

}
