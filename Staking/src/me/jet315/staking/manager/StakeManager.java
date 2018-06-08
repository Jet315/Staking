package me.jet315.staking.manager;

import me.jet315.staking.Core;
import me.jet315.staking.StakePlayer;
import me.jet315.staking.arenas.Arena;
import me.jet315.staking.arenas.inventory.IInterfaceKitSave;
import me.jet315.staking.arenas.inventory.IInterfacePlayerSave;
import me.jet315.staking.arenas.inventory.IInterfaceSave;
import me.jet315.staking.arenas.inventory.PlayerInventorySave;
import me.jet315.staking.inventory.StakeInventory;
import me.jet315.staking.utils.InvUtils;
import me.jet315.staking.utils.StakePhase;
import me.jet315.staking.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class StakeManager {


    /**
     * Starts a stake for the two specified players, and opens up the stake inventory
     *
     * @param playerOne
     * @param playerTwo
     */
    public void startStake(Player playerOne, Player playerTwo) {

        /**
         * Create StakePlayer objects
         */
        StakePlayer player = new StakePlayer(playerOne, StakePhase.TRADING);
        StakePlayer opponent = new StakePlayer(playerTwo, StakePhase.TRADING);

        player.setOpponent(opponent);
        opponent.setOpponent(player);
        /**
         * Add players to the staking array
         */
        Core.getInstance().getStakingPlayerManager().addPlayerToStake(player, opponent);

        /**
         * Start the trade by opening the two inventories
         */
        StakeInventory stakeInventory = new StakeInventory(player, opponent);
        player.setStakeInventory(stakeInventory);
        opponent.setStakeInventory(stakeInventory);


    }

    /**
     * This should be called when both players have accepted the stake inventory
     * Inventory should still be open
     */
    public void closeStakeInventoryAndSaveBetItems(StakePlayer stakePlayer, StakePlayer stakePlayer2) {
        //This prevents the CloseInventory listener from giving items back when the inventory is closed
        stakePlayer.setStakePhase(StakePhase.FIGHTING);
        stakePlayer2.setStakePhase(StakePhase.FIGHTING);
        int availableKits = Core.getInstance().getKitManager().getAvailableKits().size();

        //bet items stuff
        Player p = stakePlayer.getPlayer();
        stakePlayer.setBetItems(InvUtils.getItemsInInventory(p));

        //second player
        Player p2 = stakePlayer2.getPlayer();
        stakePlayer2.setBetItems(InvUtils.getItemsInInventory(p2));


        if(availableKits != 0) {
            //first player


            p.closeInventory();

            InvUtils.clearItemInOffHand(p);

            ArrayList<ItemStack> playersItems = InvUtils.getPlayersInventoryItems(p);

            IInterfacePlayerSave player1Save = new PlayerInventorySave(p.getInventory().getHelmet() == null ? new ItemStack(Material.AIR) : p.getInventory().getHelmet().clone(),
                    p.getInventory().getChestplate() == null ? new ItemStack(Material.AIR) : p.getInventory().getChestplate().clone(),
                    p.getInventory().getLeggings() == null ? new ItemStack(Material.AIR) : p.getInventory().getLeggings().clone(),
                    p.getInventory().getBoots() == null ? new ItemStack(Material.AIR) : p.getInventory().getBoots().clone(),
                    playersItems, p.getLocation());

            InvUtils.clearInventory(p);

            p2.closeInventory();
            InvUtils.clearItemInOffHand(p2);
            ArrayList<ItemStack> playersItems2 = InvUtils.getPlayersInventoryItems(p2);
            IInterfacePlayerSave player2Save = new PlayerInventorySave(p.getInventory().getHelmet() == null ? new ItemStack(Material.AIR) : p.getInventory().getHelmet().clone(),
                    p.getInventory().getChestplate() == null ? new ItemStack(Material.AIR) : p.getInventory().getChestplate().clone(),
                    p.getInventory().getLeggings() == null ? new ItemStack(Material.AIR) : p.getInventory().getLeggings().clone(),
                    p.getInventory().getBoots() == null ? new ItemStack(Material.AIR) : p.getInventory().getBoots().clone(),
                    playersItems2, p2.getLocation());
            InvUtils.clearInventory(p2);

            //save the Player Inventories
            Core.getInstance().getStakingPlayerManager().getInventorySaves().put(p, player1Save);
            Core.getInstance().getStakingPlayerManager().getInventorySaves().put(p2, player2Save);
        }
        //players are now ready to be teleproted!
        teleportToFightingArena(stakePlayer, stakePlayer2);

    }

    /**
     * Starts the fighting phase,
     */
    private void teleportToFightingArena(StakePlayer stakePlayer, StakePlayer stakePlayer2) {
        //find an active arena
        Arena arena = Core.getInstance().getArenaManager().getFreeArena();
        if (arena != null) {
            arena.setArenaActive(true);
            stakePlayer.setPlayersPreviousLocation(stakePlayer.getPlayer().getLocation());
            stakePlayer2.setPlayersPreviousLocation(stakePlayer2.getPlayer().getLocation());

            stakePlayer.getPlayer().teleport(arena.getSpawnLocation1());
            stakePlayer.getOpponent().getPlayer().teleport(arena.getSpawnLocation2());
            stakePlayer.setArena(arena);
            stakePlayer2.setArena(arena);
            loadKitsGUI(stakePlayer, stakePlayer2,arena);

        } else {
            //Tell them the issue
            stakePlayer.getPlayer().sendMessage(Core.getInstance().getMessages().getNoArenaFound().replaceAll("%PLUGINPREFIX%", Core.getInstance().getProperties().getPluginsPrefix()));
            stakePlayer2.getPlayer().sendMessage(Core.getInstance().getMessages().getNoArenaFound().replaceAll("%PLUGINPREFIX%", Core.getInstance().getProperties().getPluginsPrefix()));

            //refund items & money
            //refund inventory that was cleared
            Core.getInstance().getStakingPlayerManager().forceKickPlayersFromDuel(stakePlayer,stakePlayer2);

        }

    }

    /**
     * Shows the kits to the user
     */
    private void loadKitsGUI(StakePlayer stakePlayer, StakePlayer stakePlayer2,Arena arena) {
        int timeToWait = Core.getInstance().getProperties().getMatchCountDownTime();

        if (timeToWait > 0) {
            Location playerOneLoc = stakePlayer.getPlayer().getLocation().clone();
            Location playerTwoLoc = stakePlayer.getOpponent().getPlayer().getLocation().clone();

       /*     playerOneLoc.add(0.5,0,0.5);
            playerTwoLoc.add(0.5,0,0.5);
*/
       for(int y = 0; y <=1 ; y++) {
           for (int x = -1; x < 2; x++) {
               INNER:
               for (int z = -1; z < 2; z++) {
                   if (x == 0 && z == 0 || z == x) continue;
                   Block playerOneBlock = playerOneLoc.getWorld().getBlockAt(new Location(playerOneLoc.getWorld(), playerOneLoc.getX() + x, playerOneLoc.getY() + y, playerOneLoc.getZ() + z));
                   Block playerTwoBlock = playerTwoLoc.getWorld().getBlockAt(new Location(playerTwoLoc.getWorld(), playerTwoLoc.getX() + x, playerTwoLoc.getY() + y, playerTwoLoc.getZ() + z));
                   if (playerOneBlock.getType() == Material.AIR) {
                       playerOneBlock.setType(Material.BARRIER);
                       arena.getBlocksChangedToBarriers().add(playerOneBlock);
                   }
                   if (playerTwoBlock.getType() == Material.AIR) {
                       playerTwoBlock.setType(Material.BARRIER);
                       arena.getBlocksChangedToBarriers().add(playerTwoBlock);
                   }
               }
           }
       }

        }

        //See if kits exist - The loading kit bit
        int availableKits = Core.getInstance().getKitManager().getAvailableKits().size();
        if(availableKits == 1 || timeToWait <= 0){
            if(availableKits != 0) {
                InvUtils.loadKitSave(stakePlayer.getPlayer(), generateKit());
                InvUtils.loadKitSave(stakePlayer.getOpponent().getPlayer(), generateKit());
            }
        }else if(availableKits > 1){
            //Therefor must be multiple kits to select from, and the time to wait must be greater than 0
            stakePlayer.getPlayer().openInventory(Core.getInstance().getKitManager().getKitInventory());
            stakePlayer.getOpponent().getPlayer().openInventory(Core.getInstance().getKitManager().getKitInventory());
        }
        runMatchCountdown(stakePlayer,stakePlayer2,arena);

    }

    private void runMatchCountdown(StakePlayer stakePlayer,StakePlayer stakePlayer2,Arena arena){
        int matchTime = Core.getInstance().getProperties().getMatchTime();
        int countDownTime = Core.getInstance().getProperties().getMatchCountDownTime();
        arena.setMatchTime(matchTime + countDownTime);
        //Start the counter
        int availableKits = Core.getInstance().getKitManager().getAvailableKits().size();
        new BukkitRunnable() {
            public void run() {

                // need to check if players has died, probably set a boolean in arena
                if(arena.isResetArena()){
                    //It's ended, so reset
                    arena.setResetArena(false);
                    arena.setArenaActive(false);
                    //make sure blocks are reset
                    //replace barriers to air
                    for (Block block : arena.getBlocksChangedToBarriers()) {
                        block.setType(Material.AIR);
                    }
                    //clear the array
                    arena.getBlocksChangedToBarriers().clear();
                    cancel();
                }

                //Check if in match countdown
                if(arena.getMatchTime() >= matchTime){
                    //Must be in match countdown
                    int matchCountDown = arena.getMatchTime() - matchTime;
                    //Check for a suitable message to show to the users
                    if(Core.getInstance().getMessages().getTitleMatchCountDown().containsKey(matchCountDown)){
                        Core.getInstance().getMessages().getTitleMatchCountDown().get(matchCountDown).playTitle(stakePlayer.getPlayer());
                        Core.getInstance().getMessages().getTitleMatchCountDown().get(matchCountDown).playTitle(stakePlayer.getOpponent().getPlayer());
                    }
                    if(Core.getInstance().getMessages().getMessageMatchCountDown().containsKey(matchCountDown)){
                        stakePlayer.getPlayer().sendMessage(Core.getInstance().getMessages().getMessageMatchCountDown().get(matchCountDown).replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
                        stakePlayer.getOpponent().getPlayer().sendMessage(Core.getInstance().getMessages().getMessageMatchCountDown().get(matchCountDown).replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
                    }
                    //Check if the match count down is over
                    if(matchCountDown == 0) {
                        //Heal players
                        Utils.healPlayers(stakePlayer.getPlayer(),stakePlayer2.getPlayer());
                        //replace barriers to air
                        for (Block block : arena.getBlocksChangedToBarriers()) {
                            block.setType(Material.AIR);
                        }
                        //clear the array
                        arena.getBlocksChangedToBarriers().clear();

                        //check if kits are available, and check if both players have selected a kit, if not, load it for them
                        if (availableKits >= 1) {

                            if (!stakePlayer.isHasSelectedKit()) {
                                stakePlayer.setHasSelectedKit(true);
                                stakePlayer.getPlayer().closeInventory();
                                InvUtils.loadKitSave(stakePlayer.getPlayer(), generateKit());
                            }
                            if (!stakePlayer2.isHasSelectedKit()) {
                                stakePlayer2.setHasSelectedKit(true);
                                stakePlayer2.getPlayer().closeInventory();
                               InvUtils.loadKitSave(stakePlayer2.getPlayer(), generateKit());
                            }
                        }
                    }

                }


                //Probably do config option so its like at a certain time, end game, certain time, potion effect, etc
                //check if matchcountdown time is up, check player has kits, etc
                if(arena.getMatchTime() == 0){

                    //tell them their time is up!
                    String messageToSend = Core.getInstance().getMessages().getMatchEndDueToTime().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix());
                    stakePlayer.getPlayer().sendMessage(messageToSend);
                    stakePlayer2.getPlayer().sendMessage(messageToSend);
                    if(availableKits != 0) {
                        InvUtils.clearInventory(stakePlayer.getPlayer());
                        InvUtils.clearInventory(stakePlayer2.getPlayer());
                    }
                    Core.getInstance().getStakingPlayerManager().forceKickPlayersFromDuel(stakePlayer,stakePlayer2);
                    //It's ended, so reset
                    arena.setResetArena(false);
                    arena.setArenaActive(false);

                    cancel();

                }
                arena.setMatchTime(arena.getMatchTime()-1);
            }

        }.runTaskTimer(Core.getInstance(), 0L,20L);
    }

    private IInterfaceKitSave generateKit(){
        int availableKits = Core.getInstance().getKitManager().getAvailableKits().size();
        List<IInterfaceKitSave> listOfKits = new ArrayList<>();
        for(IInterfaceKitSave kit : Core.getInstance().getKitManager().getAvailableKits().values()){
            listOfKits.add(kit);
        }

        return  listOfKits.get(new Random().nextInt(availableKits));
    }




}
