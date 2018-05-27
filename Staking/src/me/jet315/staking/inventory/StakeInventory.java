package me.jet315.staking.inventory;

import me.jet315.staking.Core;
import me.jet315.staking.StakePlayer;
import me.jet315.staking.utils.InvUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class StakeInventory {

    /**
     * The players in the staking inventory
     */
    private StakePlayer player1, player2;

    /**
     * The two inventories used
     */
    private Inventory inventoryPlayer1, invetoryPlayer2;

    /**
     * The amount each player has staken
     */
    private double player1StakeAmount, player2StakeAmount;


    public StakeInventory(StakePlayer player1, StakePlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
        //create inventory
        inventoryPlayer1 = InvUtils.generateInventory(player1.getPlayer(), player2.getPlayer());
        invetoryPlayer2 = InvUtils.generateInventory(player2.getPlayer(), player1.getPlayer());

        player1.getPlayer().openInventory(inventoryPlayer1);
        player2.getPlayer().openInventory(invetoryPlayer2);

    }


    /**
     * @param item              (can be air)
     * @param slot
     * @param inventoryToUpdate Updates a specified inventory
     */
    public void updateInventory(ItemStack item, int slot, Inventory inventoryToUpdate) {
        int reflectedSlot = getReflectedSlot(slot);
        inventoryToUpdate.setItem(reflectedSlot, item);

    }

    /**
     * @return Whether the inventories both players have accepted
     */
    public boolean isBothAccepted() {
        if (inventoryPlayer1.getItem(3).isSimilar(invetoryPlayer2.getItem(3))) {
            if (inventoryPlayer1.getItem(3).isSimilar(Core.getInstance().getGui().getAcceptItem())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param slot Should be a valid left inventory slot (Checked against the InvUtils validSlots list)
     * @return The reflected inventory
     */
    private int getReflectedSlot(int slot) {
        for (int i = 4; i <= 54; i += 9) {
            if (slot < i) {
                int reflectedPlaces = i - slot;
                return i + reflectedPlaces;
            }
        }
        System.out.println("[Staking] An issue reflecting an inventory item has occurred. Please contact the developer (jet315) with this: SLOTID:" + slot + " class: inventory.StakeInventory:getReflectedSlot()");
        return -1;
    }


    /**
     * Unconfirmed both player, called if a user has updated his/her inventory
     *
     */
    public void unConfirmPlayers() {
        inventoryPlayer1.setItem(3, Core.getInstance().getGui().getNotAcceptedItem());
        inventoryPlayer1.setItem(5, Core.getInstance().getGui().getNotAcceptedItem());

        invetoryPlayer2.setItem(3, Core.getInstance().getGui().getNotAcceptedItem());
        invetoryPlayer2.setItem(5, Core.getInstance().getGui().getNotAcceptedItem());

    }

    public StakePlayer getPlayer1() {
        return player1;
    }

    public StakePlayer getPlayer2() {
        return player2;
    }

    public Inventory getInventoryPlayer1() {
        return inventoryPlayer1;
    }

    public Inventory getInvetoryPlayer2() {
        return invetoryPlayer2;
    }

    public double getPlayer1StakeAmount() {
        return player1StakeAmount;
    }

    public double getPlayer2StakeAmount() {
        return player2StakeAmount;
    }


}
