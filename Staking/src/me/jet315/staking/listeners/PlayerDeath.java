package me.jet315.staking.listeners;

import me.jet315.staking.Core;
import me.jet315.staking.StakePlayer;
import me.jet315.staking.StatsPlayer;
import me.jet315.staking.arenas.inventory.IInterfacePlayerSave;
import me.jet315.staking.utils.InvUtils;
import me.jet315.staking.utils.StakePhase;
import me.jet315.staking.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerDeath implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player playerWhoDied = (Player) e.getEntity();
        //player died
        StakePlayer playerWhoLost = Core.getInstance().getStakingPlayerManager().getStakePlayer(playerWhoDied);
        //Check they are in the arena
        if (playerWhoLost != null) {

            //Quick check to prevent them fighting if the game is over (but have not been teleported)
            if (playerWhoLost.getArena().isResetArena()) {
                e.setCancelled(true);
                return;
            }

            //check if player died
            if ((playerWhoDied.getHealth() - e.getDamage()) <= 0) {

                StakePlayer playerWhoWon = playerWhoLost.getOpponent();

                if (playerWhoLost.getStakePhase() == StakePhase.FIGHTING) {
                    //Set the event canceled so the player doesn't actually die
                    e.setCancelled(true);
                    //Heal the players
                    Utils.healPlayers(playerWhoDied, playerWhoWon.getPlayer());

                    //reset the arena
                    playerWhoWon.getArena().setResetArena(true);

                    //Reset the players
                    Core.getInstance().getStakingPlayerManager().removePlayerFromStake(playerWhoLost, playerWhoWon);


                    //Send winner message
                    playerWhoWon.getPlayer().sendMessage(Core.getInstance().getMessages().getWinnerMessage().replaceAll("%PLUGINPREFIX%", Core.getInstance().getProperties().getPluginsPrefix()));
                    //Send looser message
                    playerWhoDied.sendMessage(Core.getInstance().getMessages().getLooserMessage().replaceAll("%PLUGINPREFIX%", Core.getInstance().getProperties().getPluginsPrefix()));

                    //TODO: fireworks

                    //wait three seconds, reset inventories (if needed)
/*                    Bukkit.getScheduler().runTaskLater(Core.getInstance(), new Runnable() {
                        @Override
                        public void run() {*/
                    //Reset inventories
                    IInterfacePlayerSave playerWhoDiedInventorySave = Core.getInstance().getStakingPlayerManager().getInventorySaves().get(playerWhoDied);
                    if (playerWhoDiedInventorySave != null) {
                        InvUtils.clearInventory(playerWhoDied);
                        InvUtils.loadPlayerSave(playerWhoDied, playerWhoDiedInventorySave);
                        Core.getInstance().getStakingPlayerManager().getInventorySaves().remove(playerWhoDied);
                    }

                    IInterfacePlayerSave playerWhoWonInventorySave = Core.getInstance().getStakingPlayerManager().getInventorySaves().get(playerWhoWon.getPlayer());
                    if (playerWhoWonInventorySave != null) {
                        InvUtils.clearInventory(playerWhoWon.getPlayer());
                        InvUtils.loadPlayerSave(playerWhoWon.getPlayer(), playerWhoWonInventorySave);
                        Core.getInstance().getStakingPlayerManager().getInventorySaves().remove(playerWhoWon.getPlayer());
                    }
                    //Give rewards to winner
                    if (Core.economy != null) {
                        Core.economy.depositPlayer(playerWhoWon.getPlayer(), playerWhoWon.getBetMoney());
                        Core.economy.depositPlayer(playerWhoWon.getPlayer(), playerWhoLost.getBetMoney());
                    }
                    for (ItemStack item : playerWhoWon.getBetItems()) {
                        playerWhoWon.getPlayer().getInventory().addItem(item);
                    }
                    for (ItemStack item : playerWhoLost.getBetItems()) {
                        playerWhoWon.getPlayer().getInventory().addItem(item);
                    }

                    //Teleport back
                    String lastLocationOrWorld = Core.getInstance().getProperties().getWhereToTeleportAfterDuel();
                    if (lastLocationOrWorld.equalsIgnoreCase("lastlocation") || Bukkit.getWorld(lastLocationOrWorld) == null) {
                        playerWhoDied.teleport(playerWhoLost.getPlayersPreviousLocation());
                        playerWhoWon.getPlayer().teleport(playerWhoWon.getPlayersPreviousLocation());
                    } else {
                        World world = Bukkit.getWorld(lastLocationOrWorld);
                        playerWhoDied.teleport(world.getSpawnLocation());
                        playerWhoWon.getPlayer().teleport(world.getSpawnLocation());

                    }

                    //stats
                    if(Core.getInstance().getProperties().isSaveStatsInformation()){
                        StatsPlayer winner = Core.getInstance().getStatsManager().getStatsPlayer(playerWhoWon.getPlayer());
                        StatsPlayer looser = Core.getInstance().getStatsManager().getStatsPlayer(playerWhoDied);
                        if(winner != null && looser != null){
                            winner.setHasStatsBeenUpdated(true);
                            looser.setHasStatsBeenUpdated(true);
                            winner.setMatchesWon(winner.getMatchesWon() +1);
                            looser.setMatchesLost(looser.getMatchesLost()+1);
                        }
                    }

                    /*              }, 40L);*/


                } else {
                    //TODO message?
                    Core.getInstance().getStakingPlayerManager().forceKickPlayersFromDuel(playerWhoLost, playerWhoLost);
                }
            }
        }
    }

}
