package me.jet315.staking.listeners;

import me.jet315.staking.Core;
import me.jet315.staking.StakePlayer;
import me.jet315.staking.manager.StakeManager;
import me.jet315.staking.utils.InvUtils;
import me.jet315.staking.utils.StakePhase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class StakeInventoryClick implements Listener {


    public static ArrayList<Player> activeCountDown = new ArrayList<>();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;

        if (e.getClickedInventory().getName().equals(Core.getInstance().getGui().getStakeInventoryName())) {
            Player p = (Player) e.getWhoClicked();
            StakePlayer playerWhoClicked = Core.getInstance().getStakingPlayerManager().getStakePlayer(((Player) e.getWhoClicked()));

            int slotClicked = e.getSlot();
            ClickType clickType = e.getClick();

            //Check if item is valid slot
            if (!(InvUtils.validLeftSlots.contains(slotClicked))) {
                if (slotClicked != 0 && slotClicked != 3){
                    //Sometimes for some reason they can glitch an item into an invalid slot, this gives the item back if they manage to do it
                    //cant use bit below tho as they can dupe opponents items out
                    e.setResult(InventoryClickEvent.Result.DENY);
                    e.setCancelled(true);
                    return;
                }
            }

            if(!(InvUtils.validClickTypes.contains(clickType))){
                e.setCancelled(true);
                return;
            }

            //Check if clicked money
            if (slotClicked == 0) {
                e.setResult(Event.Result.DENY);
                e.setCancelled(true);
                if(!Core.getInstance().getGui().isMoneyEnabledInGUI() || Core.economy == null){
                    return;
                }
                if(Core.getInstance().getGui().getChangeMoneyNoise() != null){
                    playerWhoClicked.getPlayer().playSound(playerWhoClicked.getPlayer().getLocation(),Core.getInstance().getGui().getChangeMoneyNoise(),1,1);
                    playerWhoClicked.getOpponent().getPlayer().playSound(playerWhoClicked.getOpponent().getPlayer().getLocation(),Core.getInstance().getGui().getChangeMoneyNoise(),1,1);
                }
                clickedMoney(e);
                playerWhoClicked.getStakeInventory().unConfirmPlayers();
                return;
            }

            //Check if confirm button clicked
            if(slotClicked == 3){
                e.setResult(Event.Result.DENY);
                e.setCancelled(true);
                ItemStack itemClicked = e.getCurrentItem();
                //check if disabling the button
                if(itemClicked.isSimilar(Core.getInstance().getGui().getAcceptItem())){
                    e.getClickedInventory().setItem(3,Core.getInstance().getGui().getNotAcceptedItem());
                    playerWhoClicked.getOpponent().getPlayer().getOpenInventory().setItem(5,Core.getInstance().getGui().getNotAcceptedItem());
                    return;
                }
                //check if enabling the button
                if(itemClicked.isSimilar(Core.getInstance().getGui().getNotAcceptedItem())){
                    e.getClickedInventory().setItem(3,Core.getInstance().getGui().getAcceptItem());
                    playerWhoClicked.getOpponent().getPlayer().getOpenInventory().setItem(5,Core.getInstance().getGui().getAcceptItem());
                    if(playerWhoClicked.getStakeInventory().isBothAccepted()){
                        if(!activeCountDown.contains(playerWhoClicked.getPlayer())) {
                            activeCountDown.add(playerWhoClicked.getPlayer());
                            activeCountDown.add(playerWhoClicked.getOpponent().getPlayer());
                        }else{
                            return;
                        }
                        new BukkitRunnable() {
                            int counter = Core.getInstance().getProperties().getConfirmTime();
                            public void run() {

                                    if(!playerWhoClicked.getStakeInventory().isBothAccepted() || playerWhoClicked.getStakePhase() != StakePhase.TRADING){
                                        activeCountDown.remove(playerWhoClicked.getPlayer());
                                        activeCountDown.remove(playerWhoClicked.getOpponent().getPlayer());
                                        cancel();
                                        return;
                                    }
                                    if(!activeCountDown.contains(playerWhoClicked.getPlayer()) || !activeCountDown.contains(playerWhoClicked.getOpponent().getPlayer())){
                                        cancel();
                                        return;
                                    }
                                if(counter <= 0){
                                    if(Core.getInstance().getGui().getEndCountDownNoise() != null){
                                        playerWhoClicked.getPlayer().playSound(playerWhoClicked.getPlayer().getLocation(),Core.getInstance().getGui().getEndCountDownNoise(),1,1);
                                        playerWhoClicked.getOpponent().getPlayer().playSound(playerWhoClicked.getOpponent().getPlayer().getLocation(),Core.getInstance().getGui().getEndCountDownNoise(),1,1);
                                    }
                                    //prevents this from being called twice
                                    if(playerWhoClicked.getStakePhase() == StakePhase.TRADING) {
                                        Core.getInstance().getStakeManager().closeStakeInventoryAndSaveBetItems(playerWhoClicked, playerWhoClicked.getOpponent());
                                    }
                                    activeCountDown.remove(playerWhoClicked.getPlayer());
                                    activeCountDown.remove(playerWhoClicked.getOpponent().getPlayer());

                                    //remove money from players
                                    if(Core.economy != null){
                                        Core.economy.withdrawPlayer(playerWhoClicked.getPlayer(),playerWhoClicked.getBetMoney());
                                        Core.economy.withdrawPlayer(playerWhoClicked.getOpponent().getPlayer(),playerWhoClicked.getOpponent().getBetMoney());
                                    }
                                    cancel();
                                    return;
                                }
                                if(Core.getInstance().getGui().getCountDownNoise() != null){
                                    playerWhoClicked.getPlayer().playSound(playerWhoClicked.getPlayer().getLocation(),Core.getInstance().getGui().getCountDownNoise(),1,1);
                                    playerWhoClicked.getOpponent().getPlayer().playSound(playerWhoClicked.getOpponent().getPlayer().getLocation(),Core.getInstance().getGui().getCountDownNoise(),1,1);
                                }
                                counter--;

                            }
                        }.runTaskTimer(Core.getInstance(), 10, 20);
                    }
                    return;
                }
                System.out.println("Staking - Error occurred, slot three was clicked but was neither accept/not accept item");
                return;
            }
            if(clickType.isShiftClick()){
                playerWhoClicked.getStakeInventory().updateInventory(new ItemStack(Material.AIR),slotClicked,playerWhoClicked.getOpponent().getPlayer().getOpenInventory().getTopInventory());
                return;
            }
            //check if clicked blank spot
            if((e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.AIR) && (e.getCursor() != null && e.getCursor().getType() == Material.AIR)){
                return;
            }
           // if(clickType == ClickType.RIGHT){
                Bukkit.getScheduler().runTaskLater(Core.getInstance(), new Runnable() {
                    @Override
                    public void run() {

                        playerWhoClicked.getStakeInventory().updateInventory(e.getCurrentItem(),slotClicked,playerWhoClicked.getOpponent().getPlayer().getOpenInventory().getTopInventory());
                        playerWhoClicked.getStakeInventory().unConfirmPlayers();
                        if(Core.getInstance().getGui().getAddItemNoise() != null){
                            playerWhoClicked.getPlayer().playSound(playerWhoClicked.getPlayer().getLocation(),Core.getInstance().getGui().getAddItemNoise(),1,1);
                            playerWhoClicked.getOpponent().getPlayer().playSound(playerWhoClicked.getOpponent().getPlayer().getLocation(),Core.getInstance().getGui().getAddItemNoise(),1,1);
                        }

                    }

                }, 3L);
                return;
          /*  }
            if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                playerWhoClicked.getStakeInventory().updateInventory(e.getCursor(),slotClicked,playerWhoClicked.getOpponent().getOpenInventory().getTopInventory());
                return;
            }
            if(e.getCursor() != null && e.getCursor().getType() != Material.AIR) {
                playerWhoClicked.getStakeInventory().updateInventory(e.getCursor(),slotClicked,playerWhoClicked.getOpponent().getOpenInventory().getTopInventory());
                return;
            }
*/
        }
        //Disables shift clicking to the Stake Inventory inventory
        if(e.getInventory().getName().equals(Core.getInstance().getGui().getStakeInventoryName())){
            if(e.getClick().isShiftClick()){
                if(!(e.getClickedInventory().getName().equals(Core.getInstance().getGui().getStakeInventoryName()))){
                    e.setCancelled(true);
                }
            }
            if(e.getClick() == ClickType.DOUBLE_CLICK){
                e.setCancelled(true);
            }
        }
    }


    /**
     * Updates the money bit
     *
     * @param e
     */
    private void clickedMoney(InventoryClickEvent e) {
        //Figure out the amount clicked by
        ClickType type = e.getClick();
        double amountToUpdate;

        //left click
        if (type.isLeftClick()) {
            if (type.isShiftClick()) {
                amountToUpdate = Core.getInstance().getGui().getShiftLeftClickAmount();
            } else {
                amountToUpdate = Core.getInstance().getGui().getLeftClickAmount();
            }


            //must be right click
        } else {
            if (type.isShiftClick()) {
                amountToUpdate = Core.getInstance().getGui().getShiftRightClickAmount();
            } else {
                amountToUpdate = Core.getInstance().getGui().getRightClickAmount();
            }
        }
        StakePlayer playerWhoClicked = Core.getInstance().getStakingPlayerManager().getStakePlayer(((Player) e.getWhoClicked()));
        if (playerWhoClicked != null) {
            //Check if they are even adding money
            if (playerWhoClicked.getBetMoney() == 0 && amountToUpdate <= 0) return;

            double amountOfMoneyToSet;

            //Calculate the money that they will be betting
            if (playerWhoClicked.getBetMoney() + amountToUpdate <= 0) {
                amountOfMoneyToSet = 0;
            } else {
                amountOfMoneyToSet = playerWhoClicked.getBetMoney() + amountToUpdate;
            }
            //Ensure player has that money available
            double playerBalance = Core.economy.getBalance(playerWhoClicked.getPlayer());
            if(!(playerBalance >= amountOfMoneyToSet)){
                //Todo sound?
                playerWhoClicked.getPlayer().sendMessage(Core.getInstance().getMessages().getNotEnoughFunds());
                return;
            }

            playerWhoClicked.setBetMoney(amountOfMoneyToSet);

            //update their own money
            ItemStack moneyItem = InvUtils.generateMoneyItem(amountOfMoneyToSet);
            e.getInventory().setItem(e.getSlot(), moneyItem);

            //update opponents money too
            Inventory opponentInventory = playerWhoClicked.getOpponent().getPlayer().getOpenInventory().getTopInventory();
            //check if 0, if so set dud item
            if (amountOfMoneyToSet == 0) {
                opponentInventory.setItem(8, Core.getInstance().getGui().getDudItem());
            } else {
                opponentInventory.setItem(8, InvUtils.generateOpponentsMoneyItem(amountOfMoneyToSet));
            }
            playerWhoClicked.setBetMoney(amountOfMoneyToSet);

        } else {
            System.out.println("Error occurred while processing Money in staking inventory (StakeInventoryClick, null)");
        }


    }




}
