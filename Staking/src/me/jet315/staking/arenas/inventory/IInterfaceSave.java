package me.jet315.staking.arenas.inventory;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public interface IInterfaceSave {

    public ItemStack getShieldSlot();
    public ItemStack getHeadSlot();
    public ItemStack getBodySlot();
    public ItemStack getLegsSlot();
    public ItemStack getFeetSlot();
    public ArrayList<ItemStack> getInventoryItems();
}
