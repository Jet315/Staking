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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class DuelQuit implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        StakePlayer looser = Core.getInstance().getStakingPlayerManager().getStakePlayer(e.getPlayer());
        if(looser != null){
            if(looser.getStakePhase() == StakePhase.FIGHTING){
                StakePlayer winner = looser.getOpponent();
                //Player left while fighting


                //Heal the players
                Utils.healPlayers(looser.getPlayer(), winner.getPlayer());

                //send winner message explaining what happened
                winner.getPlayer().sendMessage(Core.getInstance().getMessages().getWinnerMessageDueToOpponentDisconect().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));

                //Reset inventories
                IInterfacePlayerSave playerWhoDiedInventorySave = Core.getInstance().getStakingPlayerManager().getInventorySaves().get(looser.getPlayer());
                if (playerWhoDiedInventorySave != null) {
                    InvUtils.clearInventory(looser.getPlayer());
                    InvUtils.loadPlayerSave(looser.getPlayer(), playerWhoDiedInventorySave);
                    Core.getInstance().getStakingPlayerManager().getInventorySaves().remove(looser.getPlayer());
                }

                IInterfacePlayerSave playerWhoWonInventorySave = Core.getInstance().getStakingPlayerManager().getInventorySaves().get(winner.getPlayer());
                if (playerWhoWonInventorySave != null) {
                    InvUtils.clearInventory(winner.getPlayer());
                    InvUtils.loadPlayerSave(winner.getPlayer(), playerWhoWonInventorySave);
                    Core.getInstance().getStakingPlayerManager().getInventorySaves().remove(winner.getPlayer());
                }
                //Give rewards to winner
                if (Core.economy != null) {
                    Core.economy.depositPlayer(winner.getPlayer(), winner.getBetMoney());
                    Core.economy.depositPlayer(winner.getPlayer(), looser.getBetMoney());
                }
                for (ItemStack item : winner.getBetItems()) {
                    winner.getPlayer().getInventory().addItem(item);
                }
                for (ItemStack item : looser.getBetItems()) {
                    winner.getPlayer().getInventory().addItem(item);
                }

                //reset the arena
                looser.getArena().setResetArena(true);

                //Reset the players
                Core.getInstance().getStakingPlayerManager().removePlayerFromStake(looser,winner);

                //Teleport both players
                //Teleport back
                String lastLocationOrWorld = Core.getInstance().getProperties().getWhereToTeleportAfterDuel();
                if (lastLocationOrWorld.equalsIgnoreCase("lastlocation") || Bukkit.getWorld(lastLocationOrWorld) == null) {
                    looser.getPlayer().teleport(looser.getPlayersPreviousLocation());
                    winner.getPlayer().teleport(winner.getPlayersPreviousLocation());
                } else {
                    World world = Bukkit.getWorld(lastLocationOrWorld);
                    looser.getPlayer().teleport(world.getSpawnLocation());
                    winner.getPlayer().teleport(world.getSpawnLocation());

                }
                //stats
                if(Core.getInstance().getProperties().isSaveStatsInformation()){
                    StatsPlayer statsWinner = Core.getInstance().getStatsManager().getStatsPlayer(winner.getPlayer());
                    StatsPlayer statsLooser = Core.getInstance().getStatsManager().getStatsPlayer(looser.getPlayer());
                    if(winner != null && looser != null){
                        statsWinner.setHasStatsBeenUpdated(true);
                        statsLooser.setHasStatsBeenUpdated(true);
                        statsWinner.setMatchesWon(statsWinner.getMatchesWon() +1);
                        statsLooser.setMatchesLost(statsLooser.getMatchesLost()+1);
                    }
                }
            }
        }
    }
}
