package me.jet315.staking.listeners;

import me.jet315.staking.Core;
import me.jet315.staking.StatsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StatsListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(Core.getInstance().getProperties().isSaveStatsInformation()){
            Core.getInstance().getDb().loadPlayerStats(e.getPlayer());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        if(Core.getInstance().getProperties().isSaveStatsInformation()){
            StatsPlayer statsPlayer = Core.getInstance().getStatsManager().getStatsPlayer(e.getPlayer());
            if(statsPlayer != null)
            Core.getInstance().getDb().savePlayerStats(statsPlayer);
        }
    }
}
