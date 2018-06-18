package me.jet315.staking.listeners;

import me.jet315.staking.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class StakeItemDrop implements Listener {

    @EventHandler
    public void onItemEvent(PlayerDropItemEvent e){
        if(Core.getInstance().getStakingPlayerManager().getStakePlayer(e.getPlayer()) != null){
            e.setCancelled(true);
        }
    }
}
