package me.jet315.staking.arenas.inventory;

import org.bukkit.inventory.ItemStack;

public interface IInterfaceKitSave extends IInterfaceSave{

    public ItemStack getDisplayItem();
    public boolean isKitClicked(ItemStack itemClicked);
}
