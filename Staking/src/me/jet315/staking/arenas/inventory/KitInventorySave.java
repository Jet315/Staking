package me.jet315.staking.arenas.inventory;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class KitInventorySave implements IInterfaceKitSave{

    private ItemStack displayItem;

    private ItemStack headSlot;
    private ItemStack bodySlot;
    private ItemStack legsSlot;
    private ItemStack feetSlot;

    private ArrayList<ItemStack> inventoryItems = new ArrayList<>();

    public KitInventorySave(ItemStack displayItem,ItemStack headSlot, ItemStack bodySlot, ItemStack legsSlot, ItemStack feetSlot, ArrayList<ItemStack> inventoryItems) {
        this.displayItem = displayItem;
        this.headSlot = headSlot;
        this.bodySlot = bodySlot;
        this.legsSlot = legsSlot;
        this.feetSlot = feetSlot;
        this.inventoryItems = inventoryItems;
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

    @Override
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    @Override
    public boolean isKitClicked(ItemStack itemClicked) {
        return itemClicked.isSimilar(displayItem);
    }


}



