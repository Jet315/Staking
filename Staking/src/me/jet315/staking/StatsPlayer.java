package me.jet315.staking;

import org.bukkit.entity.Player;

public class StatsPlayer {

    private Player player;
    private int matchesWon ;
    private int matchesLost;
    //Used when player logs out
    private boolean hasStatsBeenUpdated = false;

    public StatsPlayer(Player p,int matchesWon,int matchesLost){
        this.player = p;
        this.matchesLost = matchesLost;
        this.matchesWon = matchesWon;
    }

    public Player getPlayer() {
        return player;
    }

    public int getMatchesWon() {
        return matchesWon;
    }

    public void setMatchesWon(int matchesWon) {
        this.matchesWon = matchesWon;
    }

    public int getMatchesLost() {
        return matchesLost;
    }

    public void setMatchesLost(int matchesLost) {
        this.matchesLost = matchesLost;
    }

    //Prevents divided by 0 error
    public double getWinLooseRatio(){
        if(matchesWon == 0) return 0.00;
        if(matchesLost == 0) return matchesWon;

        return ((double) matchesWon / matchesLost);
    }

    public boolean isHasStatsBeenUpdated() {
        return hasStatsBeenUpdated;
    }

    public void setHasStatsBeenUpdated(boolean hasStatsBeenUpdated) {
        this.hasStatsBeenUpdated = hasStatsBeenUpdated;
    }
}
