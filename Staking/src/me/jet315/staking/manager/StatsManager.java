package me.jet315.staking.manager;

import me.jet315.staking.StatsPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class StatsManager {

    private HashMap<Player,StatsPlayer> playerStats = new HashMap<>();

    public void loadPlayersData(Player p){

    }

    public void removePlayersData(Player p){

    }

    /**
     *
     * @param p
     * @return May return null, if the player is not online
     */
    public StatsPlayer getStatsPlayer(Player p){
        return playerStats.get(p);
    }
    public HashMap<Player, StatsPlayer> getPlayerStats() {
        return playerStats;
    }
}
