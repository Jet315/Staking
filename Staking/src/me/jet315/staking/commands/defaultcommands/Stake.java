package me.jet315.staking.commands.defaultcommands;

import me.jet315.staking.Core;
import me.jet315.staking.StakePlayer;
import me.jet315.staking.arenas.Arena;
import me.jet315.staking.commands.CommandExecutor;
import me.jet315.staking.manager.StakeManager;
import me.jet315.staking.utils.StakePhase;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class Stake extends CommandExecutor {

    /**
     * Find house command
     */

    public Stake() {
        setCommand("stake");
        setPermission("staking.player.stake");
        setLength(1);
        setPlayer();
        setUsage("/stake <player>");
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        //Can cast as was already checked
        Player p = (Player) sender;
        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null) {
            p.sendMessage(Core.getInstance().getMessages().getInvalidTarget().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()).replaceAll("%PLAYER%",args[0]));
            return;
        }

        //Check to see if sending the request to him/her self
        if(p.getName().equalsIgnoreCase(args[0]) || targetPlayer == p){
            p.sendMessage(Core.getInstance().getMessages().getDuelSentToSelf().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
            return;
        }



        //check if the player performing the command already is in a duel
        if(Core.getInstance().getStakingPlayerManager().getStakePlayer(p) != null){
            p.sendMessage(Core.getInstance().getMessages().getPlayerInDuel().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
            return;
        }

        //check if opponent is in duel
        if(Core.getInstance().getStakingPlayerManager().getStakePlayer(targetPlayer) != null){
            p.sendMessage(Core.getInstance().getMessages().getTargetPlayerIsInDuel().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()).replaceAll("%PLAYER%",targetPlayer.getName()));
            return;
        }
        //check to see if the target player has sent a duel
        if(Core.getInstance().getStakingPlayerManager().getRecentDuels().containsKey(targetPlayer)){
            Player whoRequestWasFor = Core.getInstance().getStakingPlayerManager().getRecentDuels().get(targetPlayer);
            if(whoRequestWasFor == p){
                Core.getInstance().getStakingPlayerManager().getRecentDuels().remove(targetPlayer);
               Core.getInstance().getStakeManager().startStake(p,targetPlayer);
                return;
            }
        }

        //check to see if they are in duel cooldown
        if(Core.getInstance().getStakingPlayerManager().getRecentDuels().containsKey(p)){
            p.sendMessage(Core.getInstance().getMessages().getDuelCooldown().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
            return;
        }

        //Check to see if arena available
        if(Core.getInstance().getArenaManager().getFreeArena() == null){
            p.sendMessage(Core.getInstance().getMessages().getNoArenaFound().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
            return;
        }

        //send the person a request to fight
        Core.getInstance().getStakingPlayerManager().getRecentDuels().put(p,targetPlayer);

        TextComponent message = new TextComponent(Core.getInstance().getMessages().getDuelRequest().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()).replaceAll("%PLAYER%",p.getName()));

        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stake " + p.getName()));
        targetPlayer.spigot().sendMessage(message);
        //targetPlayer.sendMessage(Core.getInstance().getMessages().getDuelRequest().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()).replaceAll("%PLAYER%",p.getName()));


        p.sendMessage(Core.getInstance().getMessages().getDuelSent().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()).replaceAll("%PLAYER%",targetPlayer.getName()));
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                if(Core.getInstance().getStakingPlayerManager().getRecentDuels().containsKey(p)){
                    if(p.isOnline()){
                        p.sendMessage(Core.getInstance().getMessages().getDuelExpired().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()).replaceAll("%PLAYER%",targetPlayer.getName()));
                        Core.getInstance().getStakingPlayerManager().getRecentDuels().remove(p);
                    }
                }
            }

        }, 20L * Core.getInstance().getProperties().getTimeForDuelRequestToExpire());

    }

}
