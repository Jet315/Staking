package me.jet315.staking.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class StatsCommandListener implements Listener {

    @EventHandler
    public void onStatsCommand(PlayerCommandPreprocessEvent e){
        if(e.getMessage().startsWith("/stats")){
            e.setCancelled(true);
            String messageSplit[] = e.getMessage().split(" ");
            if(messageSplit.length > 1){
                e.getPlayer().performCommand("stake stats " + messageSplit[1]);
            }else{
                e.getPlayer().performCommand("stake stats");
            }
        }
    }
}
