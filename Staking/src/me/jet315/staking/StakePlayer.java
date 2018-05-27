package me.jet315.staking;

import me.jet315.staking.arenas.Arena;
import me.jet315.staking.inventory.StakeInventory;
import me.jet315.staking.utils.StakePhase;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Stake Player
 *  - Stores a player who is currently in the Staking Process
 */
public class StakePlayer {


    /**
     * The player
     */
    private Player player;

    /**
     * Who the player is against
     */
    private StakePlayer opponent;
    /**
     * The items that the player has bet
     */
    private ArrayList<ItemStack> betItems = new ArrayList<>();

    /**
     * The money the player has bet
     */
    private double betMoney = 0;

    /**
     * Stores what stage the stake is currently in
     */
    private StakePhase stakePhase;

    /**
     * Stores the StakeInventory object
     */
    private StakeInventory stakeInventory;

    /**
     * Stores arena player is in
     */
    private Arena arena;
    /**
     * Stores whether they have selected a kit
     */
    private boolean hasSelectedKit = false;

    /**
     * Stores the location they were previous to being teleported to the arena
     */
    private Location playersPreviousLocation;



    public StakePlayer(Player player, StakePhase stakePhase) {
        this.player = player;
        this.stakePhase = stakePhase;

    }


    /**
     *
     * @return The player that this StakePlayer relates to
     */
    public Player getPlayer() {
        return player;
    }


    public double getBetMoney() {
        return betMoney;
    }

    public void setBetMoney(double betMoney) {
        this.betMoney = betMoney;
    }

    public StakePhase getStakePhase() {
        return stakePhase;
    }

    public void setStakePhase(StakePhase stakePhase) {
        this.stakePhase = stakePhase;
    }

    public StakePlayer getOpponent() {
        return opponent;
    }

    public StakeInventory getStakeInventory() {
        return stakeInventory;
    }

    public void setStakeInventory(StakeInventory stakeInventory) {
        this.stakeInventory = stakeInventory;
    }

    public ArrayList<ItemStack> getBetItems() {
        return betItems;
    }

    public void setBetItems(ArrayList<ItemStack> betItems) {
        this.betItems = betItems;
    }

    public void setOpponent(StakePlayer opponent) {
        this.opponent = opponent;
    }

    public boolean isHasSelectedKit() {
        return hasSelectedKit;
    }

    public void setHasSelectedKit(boolean hasSelectedKit) {
        this.hasSelectedKit = hasSelectedKit;
    }

    public Arena getArena() {
        return arena;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public Location getPlayersPreviousLocation() {
        return playersPreviousLocation;
    }

    public void setPlayersPreviousLocation(Location playersPreviousLocation) {
        this.playersPreviousLocation = playersPreviousLocation;
    }
}
