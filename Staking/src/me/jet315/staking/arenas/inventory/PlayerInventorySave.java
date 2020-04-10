package me.jet315.staking.arenas.inventory;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PlayerInventorySave implements IInterfacePlayerSave{

    private ItemStack headSlot;
    private ItemStack bodySlot;
    private ItemStack legsSlot;
    private ItemStack feetSlot;
    private ItemStack shieldSlot;

    private Location lastLocation;
    private ArrayList<ItemStack> inventoryItems = new ArrayList<>();


    public PlayerInventorySave(ItemStack shieldSlot, ItemStack headSlot, ItemStack bodySlot, ItemStack legsSlot, ItemStack feetSlot, ArrayList<ItemStack> inventoryItems,Location location) {
        this.shieldSlot = shieldSlot;
        this.headSlot = headSlot;
        this.bodySlot = bodySlot;
        this.legsSlot = legsSlot;
        this.feetSlot = feetSlot;
        this.inventoryItems = inventoryItems;
        this.lastLocation = location;
    }


    @Override
    public ItemStack getHeadSlot() {
        return headSlot;
    }

    @Override
    public ItemStack getBodySlot() {
        return bodySlot;
    }

    @Override
    public ItemStack getLegsSlot() {
        return legsSlot;
    }

    @Override
    public ItemStack getFeetSlot() {
        return feetSlot;
    }

    @Override
    public ArrayList<ItemStack> getInventoryItems() {
        return inventoryItems;
    }

    public ItemStack getShieldSlot() {
        return shieldSlot;
    }
}



