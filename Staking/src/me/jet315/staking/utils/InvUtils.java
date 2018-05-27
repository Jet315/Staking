package me.jet315.staking.utils;

import me.jet315.staking.Core;
import me.jet315.staking.arenas.inventory.IInterfaceKitSave;
import me.jet315.staking.arenas.inventory.IInterfaceSave;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class InvUtils {

    public static final ArrayList<Integer> validLeftSlots = new ArrayList<>(Arrays.asList(1,2, 9, 10, 11, 12, 18, 19, 20, 21,
            27, 28, 29, 30, 36, 37, 38, 39, 45, 46,
            47, 48));

    public static final ArrayList<ClickType> validClickTypes = new ArrayList<>(Arrays.asList(ClickType.DROP,ClickType.LEFT,ClickType.RIGHT,ClickType.SHIFT_LEFT,ClickType.SHIFT_RIGHT));

    public static Inventory generateInventory(Player player1, Player player2){
        //Create inventory
        Inventory inv = Bukkit.createInventory(player1.getInventory().getHolder(), 54, Core.getInstance().getGui().getStakeInventoryName().replaceAll("%PLAYER1%",player1.getName()).replaceAll("%PLAYER2%",player2.getName()));
        //Setting the separator item
        for(int i = 4; i <= 49 ; i +=9){
            inv.setItem(i,Core.getInstance().getGui().getDividerItem());
        }

        if(Core.getInstance().getGui().isMoneyEnabledInGUI()) {
            //money option - if enabled
            if(Core.economy != null) {
                inv.setItem(0, generateMoneyItem(0));
            }else{
                System.out.println(ChatColor.RED + "[STAKING] VAULT IS NOT INSTALLED AND IS NEEDED TO ENABLE MONEY");
                inv.setItem(0,Core.getInstance().getGui().getDudItem());
            }
        }else{
            inv.setItem(0,Core.getInstance().getGui().getDudItem());
        }

        //oponents money option (will be dud currently)
        inv.setItem(8,Core.getInstance().getGui().getDudItem());

        //accept option
        inv.setItem(3,Core.getInstance().getGui().getNotAcceptedItem());
        inv.setItem(5,Core.getInstance().getGui().getNotAcceptedItem());
        return inv;
    }


    public static ItemStack generateMoneyItem(double amount){
        ItemStack playerMoneyItem = Core.getInstance().getGui().getPlayersMoneyItem().clone();
        ItemMeta meta = playerMoneyItem.getItemMeta();
        meta.setDisplayName(meta.getDisplayName().replaceAll("%AMOUNT%",String.valueOf(amount)));
        playerMoneyItem.setItemMeta(meta);
        return playerMoneyItem;
    }

    public static ItemStack generateOpponentsMoneyItem(double amount){

        ItemStack opponentsMoneyItem = Core.getInstance().getGui().getOpponentsMoneyItem().clone();
        ItemMeta meta = opponentsMoneyItem.getItemMeta();
        meta.setDisplayName(meta.getDisplayName().replaceAll("%OPPONENTSAMOUNT%",String.valueOf(amount)));
        opponentsMoneyItem.setItemMeta(meta);
        return opponentsMoneyItem;
    }

    /**
     *
     * @param p The player
     * @return The valid players items in the inventory
     */
    public static ArrayList<ItemStack> getItemsInInventory(Player p){
        ArrayList<ItemStack> itemsBetted = new ArrayList<>();
        Inventory inventory = p.getOpenInventory().getTopInventory();
        if(inventory != null && inventory.getName().equals(Core.getInstance().getGui().getStakeInventoryName())){
        for(int i = 0; i < inventory.getSize() ; i++){
            if(InvUtils.validLeftSlots.contains(i)) {
                if (inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
                    itemsBetted.add(inventory.getItem(i).clone());
                }
            }
            }
        }
        return itemsBetted;
    }

    /**
     *
     * @param p
     * @return The inventory items for the specified player
     */
    public static ArrayList<ItemStack> getPlayersInventoryItems(Player p) {
        ArrayList<ItemStack> playersItems = new ArrayList<>();
        for (ItemStack itemInInventory : p.getInventory().getStorageContents()) {
            if (itemInInventory != null && itemInInventory.getType() != Material.AIR) {
                playersItems.add(itemInInventory.clone());

            }
        }
        return playersItems;
    }

    /**
     * Clears items off the players main hand
     * @param p The player
     */
    public static void clearItemInOffHand(Player p){
        if(p.getInventory().getItemInOffHand() != null && p.getInventory().getItemInOffHand().getType() != Material.AIR){
            ItemStack materialInOffHand = p.getInventory().getItemInOffHand().clone();
            p.getInventory().addItem(materialInOffHand);
            p.getInventory().getItemInOffHand().setType(Material.AIR);
        }
    }

    /**
     * Clears amour slots and players inventory
     * @param p The player
     */
    public static void clearInventory(Player p){
        p.getInventory().clear();
        p.getInventory().setHelmet(new ItemStack(Material.AIR));
        p.getInventory().setChestplate(new ItemStack(Material.AIR));
        p.getInventory().setLeggings(new ItemStack(Material.AIR));
        p.getInventory().setBoots(new ItemStack(Material.AIR));
    }

    /**
     * Loads and equips a saved inventory
     * @param p
     */
    public static void loadPlayerSave(Player p, IInterfaceSave playerSave){

        //adds items to body slots
        if(playerSave.getHeadSlot() != null && playerSave.getHeadSlot().getType() != Material.AIR){
            p.getInventory().setHelmet(playerSave.getHeadSlot());
        }
        if(playerSave.getBodySlot() != null && playerSave.getBodySlot().getType() != Material.AIR){
            p.getInventory().setChestplate(playerSave.getBodySlot());
        }
        if(playerSave.getLegsSlot() != null && playerSave.getLegsSlot().getType() != Material.AIR){
            p.getInventory().setLeggings(playerSave.getLegsSlot());
        }
        if(playerSave.getFeetSlot() != null && playerSave.getFeetSlot().getType() != Material.AIR){
            p.getInventory().setHelmet(playerSave.getFeetSlot());
        }

        //adds rest of items to inventory
        if(playerSave.getInventoryItems().size() > 0) {
            for (ItemStack item : playerSave.getInventoryItems()) {
                p.getInventory().addItem(item);
            }
        }


    }
    public static void loadKitSave(Player p, IInterfaceKitSave kitSave){

        //adds items to body slots
        if(kitSave.getHeadSlot() != null && kitSave.getHeadSlot().getType() != Material.AIR){
            p.getInventory().setHelmet(kitSave.getHeadSlot());

        }
        if(kitSave.getBodySlot() != null && kitSave.getBodySlot().getType() != Material.AIR){
            p.getInventory().setChestplate(kitSave.getBodySlot());

        }
        if(kitSave.getLegsSlot() != null && kitSave.getLegsSlot().getType() != Material.AIR){
            p.getInventory().setLeggings(kitSave.getLegsSlot());

        }
        if(kitSave.getFeetSlot() != null && kitSave.getFeetSlot().getType() != Material.AIR){
            p.getInventory().setBoots(kitSave.getFeetSlot());

        }

        //adds rest of items to inventory

        if(kitSave.getInventoryItems().size() > 0) {

            for (ItemStack item : kitSave.getInventoryItems()) {
                p.getInventory().addItem(item);

            }
        }

    }
}
