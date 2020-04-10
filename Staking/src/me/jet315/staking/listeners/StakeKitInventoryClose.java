package me.jet315.staking.listeners;

import me.jet315.staking.Core;
import me.jet315.staking.StakePlayer;
import me.jet315.staking.utils.StakePhase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class StakeKitInventoryClose implements Listener {

    //give items back
    @EventHandler
    public void onKitInventoryClose(InventoryCloseEvent e) {

        if (e.getView().getTitle().equals(Core.getInstance().getKitManager().getInventoryName())) {
            //closed kit
            //Get stake player
            StakePlayer stakePlayer = Core.getInstance().getStakingPlayerManager().getStakePlayer((Player) e.getPlayer());
                if(stakePlayer != null){
                    //check if in reset phase
                    if(stakePlayer.getStakePhase() == StakePhase.RESET) return;
                    //See if has selected kit, if not, stop  them from closing the inventory
                    if(!stakePlayer.isHasSelectedKit()){
                        Bukkit.getServer().getScheduler().runTaskLater(Core.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                stakePlayer.getPlayer().sendMessage(Core.getInstance().getMessages().getNoKitSelected().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
                                stakePlayer.getPlayer().openInventory(Core.getInstance().getKitManager().getKitInventory());
                            }
                        },4L);
                    }


            }

            //todo check out minesaga
        }
    }
}
