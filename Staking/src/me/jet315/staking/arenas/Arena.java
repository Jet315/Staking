package me.jet315.staking.arenas;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;

public class Arena {

    private String arenaID;

    private Location spawnLocation1;
    private Location spawnLocation2;
    private int matchTime = 0;

    //Called when a player has died, and the runnable task needs to reset the arena
    private boolean resetArena = false;
    
    private ArrayList<Block> blocksChangedToBarriers = new ArrayList<>();

    private boolean isArenaActive = false;

    public Arena(String arenaID){

        this.arenaID = arenaID;
    }

    public Location getSpawnLocation1() {
        return spawnLocation1;
    }


    public void setSpawnLocation1(Location spawnLocation1) {
        this.spawnLocation1 = spawnLocation1;
    }

    public Location getSpawnLocation2() {
        return spawnLocation2;
    }

    public void setSpawnLocation2(Location spawnLocation2) {
        this.spawnLocation2 = spawnLocation2;
    }

    public String getArenaID() {
        return arenaID;
    }

    public void setArenaActive(boolean arenaActive) {
        isArenaActive = arenaActive;
    }

    public boolean isArenaReady(){
        return !isArenaActive && spawnLocation1!=null && spawnLocation2!=null;
    }

    public ArrayList<Block> getBlocksChangedToBarriers() {
        return blocksChangedToBarriers;
    }

    public int getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(int matchTime) {
        this.matchTime = matchTime;
    }

    public boolean isResetArena() {
        return resetArena;
    }

    public void setResetArena(boolean resetArena) {
        this.resetArena = resetArena;
    }
}
