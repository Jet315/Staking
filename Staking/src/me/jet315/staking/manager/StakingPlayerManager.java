package me.jet315.staking.manager;

import me.jet315.staking.Core;
import me.jet315.staking.StakePlayer;
import me.jet315.staking.arenas.inventory.IInterfacePlayerSave;
import me.jet315.staking.arenas.inventory.IInterfaceSave;
import me.jet315.staking.arenas.inventory.PlayerInventorySave;
import me.jet315.staking.utils.InvUtils;
import me.jet315.staking.utils.StakePhase;
import me.jet315.staking.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StakingPlayerManager {

    /**
     * Stores the players who are actively in a duel
     */
    private ArrayList<StakePlayer> playersInStake = new ArrayList<>();

    /**
     * Stores the players who are in the fighting, with their saved inventories
     */
    private Map<Player, IInterfacePlayerSave> inventorySaves = new HashMap<>();

    /**
     * Stores players who have recently sent a duel request
     * Stores Player who sent the request | player who the request is for
     */
    private Map<Player, Player> recentDuels = new HashMap<>();

    /**
     * @param players Adds specified players from the stake array
     */
    public void addPlayerToStake(StakePlayer... players) {
        for (StakePlayer player : players) {
            if (playersInStake.contains(player)) {
                return;
            }
            //Although objects may not be the same, the player may still exist in the array? Maybe for loop through names?

            playersInStake.add(player);
        }
    }

    /**
     * @param players Removes specified players from the stake array
     */
    public void removePlayerFromStake(StakePlayer... players) {
        for (StakePlayer player : players) {
            if (playersInStake.contains(player)) {
                playersInStake.remove(player);
            }
        }
    }

    /**
     * Force kick all players from all arenas and refund items+Betting items (used in Reload, or in server stop)
     */
    public void forceKickAllPlayersFromDuels() {
        for (StakePlayer p : playersInStake) {

            p.getPlayer().sendMessage(Core.getInstance().getMessages().getForceKickOutOfDuel().replaceAll("%PLUGINPREFIX%", Core.getInstance().getProperties().getPluginsPrefix()));
            p.setStakePhase(StakePhase.RESET);
            p.getPlayer().closeInventory();
            p.getPlayer().updateInventory();

            InvUtils.clearInventory(p.getPlayer());

            //refund inventory that was cleared
            InvUtils.loadPlayerSave(p.getPlayer(), getInventorySaves().get(p.getPlayer()));
            getInventorySaves().remove(p.getPlayer());

            if (p.getPlayersPreviousLocation() != null) {
                String lastLocationOrWorld = Core.getInstance().getProperties().getWhereToTeleportAfterDuel();
                if (lastLocationOrWorld.equalsIgnoreCase("lastlocation") || Bukkit.getWorld(lastLocationOrWorld) == null) {
                    p.getPlayer().teleport(p.getPlayersPreviousLocation());
                } else {
                    World world = Bukkit.getWorld(lastLocationOrWorld);
                    p.getPlayer().teleport(world.getSpawnLocation());

                }
            }

            //refund items bet & money
            if (Core.economy != null) {
                Core.economy.depositPlayer(p.getPlayer(), p.getBetMoney());
            }

            for (ItemStack item : p.getBetItems()) {
                p.getPlayer().getInventory().addItem(item);
            }

            //reset arena
            if (p.getArena() != null) {
                p.getArena().setArenaActive(false);
                p.getArena().setResetArena(true);
            }


            Utils.healPlayers(p.getPlayer());


        }
        playersInStake.clear();

    }

    /**
     *
     */
    public void forceKickPlayersFromDuel(StakePlayer... players) {
        for (StakePlayer player : players) {
            IInterfacePlayerSave inventorySave = Core.getInstance().getStakingPlayerManager().getInventorySaves().get(player.getPlayer());
            if (inventorySave != null) {
                InvUtils.clearInventory(player.getPlayer());
                InvUtils.loadPlayerSave(player.getPlayer(), inventorySave);
                Core.getInstance().getStakingPlayerManager().getInventorySaves().remove(player.getPlayer());
            }


            //refund items bet & money
            if (Core.economy != null) {
                Core.economy.depositPlayer(player.getPlayer(), player.getBetMoney());
            }

            for (ItemStack item : player.getBetItems()) {
                player.getPlayer().getInventory().addItem(item);
            }

            if (player.getPlayersPreviousLocation() != null) {
                String lastLocationOrWorld = Core.getInstance().getProperties().getWhereToTeleportAfterDuel();
                if (lastLocationOrWorld.equalsIgnoreCase("lastlocation") || Bukkit.getWorld(lastLocationOrWorld) == null) {
                    player.getPlayer().teleport(player.getPlayersPreviousLocation());
                } else {
                    World world = Bukkit.getWorld(lastLocationOrWorld);
                    player.getPlayer().teleport(world.getSpawnLocation());

                }
            }

            //reset arena
            if (player.getArena() != null) {
                player.getArena().setArenaActive(false);
                player.getArena().setResetArena(true);
            }

            //Remove from arena list
            Core.getInstance().getStakingPlayerManager().removePlayerFromStake(player);

            player.getPlayer().closeInventory();
            Utils.healPlayers(player.getPlayer());
        }
    }

    /**
     * @param p The player
     * @return A StakePlayer, or null if non existent
     */
    public StakePlayer getStakePlayer(Player p) {
        for (StakePlayer player : playersInStake) {
            if (player.getPlayer() == p) {
                return player;
            }
        }
        return null;
    }


    /**
     * @return The players who are currently in the trade menu
     */
    public ArrayList<StakePlayer> getPlayersInStakeInventory() {
        ArrayList<StakePlayer> stakePlayers = new ArrayList<>();
        for (StakePlayer stakePlayer : playersInStake) {
            if (stakePlayer.getStakePhase() == StakePhase.TRADING) {
                stakePlayers.add(stakePlayer);
            }
        }
        return stakePlayers;
    }

    /**
     * @return The players who are currently dueling
     */
    public ArrayList<StakePlayer> getPlayersFighting() {
        ArrayList<StakePlayer> stakePlayers = new ArrayList<>();
        for (StakePlayer stakePlayer : playersInStake) {
            if (stakePlayer.getStakePhase() == StakePhase.FIGHTING) {
                stakePlayers.add(stakePlayer);
            }
        }
        return stakePlayers;
    }

    public ArrayList<StakePlayer> getAllPlayersStaking() {
        return playersInStake;
    }

    public Map<Player, Player> getRecentDuels() {
        return recentDuels;
    }

    public Map<Player, IInterfacePlayerSave> getInventorySaves() {
        return inventorySaves;
    }
}
