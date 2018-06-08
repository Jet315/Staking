package me.jet315.staking.listeners;

import me.jet315.staking.Core;
import me.jet315.staking.StakePlayer;
import me.jet315.staking.utils.InvUtils;
import me.jet315.staking.utils.StakePhase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class StakeInventoryClose implements Listener {

    //give items back
    @EventHandler
    public void onClose(InventoryCloseEvent e) {

        if (e.getInventory().getName().equals(Core.getInstance().getGui().getStakeInventoryName())) {

            StakePlayer stakePlayer = Core.getInstance().getStakingPlayerManager().getStakePlayer((Player) e.getPlayer());
            if (stakePlayer != null) {
                //prevents items being given if the inventory closes due to tping into arena
                if(stakePlayer.getStakePhase() == StakePhase.RESET) return;
                if(stakePlayer.getStakePhase() != StakePhase.TRADING) return;

                StakePlayer stakeOpponent = stakePlayer.getOpponent();

                Core.getInstance().getStakingPlayerManager().removePlayerFromStake(stakePlayer,stakeOpponent);

                    ArrayList<ItemStack> itemsToGiveBack = InvUtils.getItemsInInventory(stakePlayer.getPlayer());
                    if (itemsToGiveBack.size() > 0) {
                        for (ItemStack itemToGiveBack : itemsToGiveBack) {
                            e.getPlayer().getInventory().addItem(itemToGiveBack);
                        }
                    }

                ArrayList<ItemStack> itemsToGiveBackToOpponent = InvUtils.getItemsInInventory(stakePlayer.getOpponent().getPlayer());
                if (itemsToGiveBackToOpponent.size() > 0) {
                    for (ItemStack itemToGiveBack : itemsToGiveBackToOpponent) {
                        stakePlayer.getOpponent().getPlayer().getInventory().addItem(itemToGiveBack);
                    }
                }

                if(Core.economy != null) {
                    //money
                    Core.economy.depositPlayer(stakePlayer.getPlayer(), stakePlayer.getBetMoney());
                    Core.economy.depositPlayer(stakeOpponent.getPlayer(), stakeOpponent.getBetMoney());
                }
                stakeOpponent.getPlayer().closeInventory();
                StakeInventoryClick.activeCountDown.remove(stakePlayer.getPlayer());
                StakeInventoryClick.activeCountDown.remove(stakeOpponent.getPlayer());



            }
        }
    }

}