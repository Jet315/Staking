package me.jet315.staking.commands.defaultcommands;

import me.jet315.staking.Core;
import me.jet315.staking.StatsPlayer;
import me.jet315.staking.commands.CommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Stats extends CommandExecutor {

    /**
     * Find house command
     */

    public Stats() {
        setCommand("stats");
        setPermission("staking.player.stats");// && staking.player.statsother
        setLength(1);
        setPlayer();
        setUsage("/stake stats <player>");
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        //Can cast as was already checked
        Player p = (Player) sender;
        if(!Core.getInstance().getProperties().isSaveStatsInformation()){
            p.sendMessage(Core.getInstance().getMessages().getStatsNotEnabled().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
            return;
        }
        if(args.length == 1){
            StatsPlayer statsPlayer = Core.getInstance().getStatsManager().getStatsPlayer(p);
            if(statsPlayer != null){
                NumberFormat formatter = new DecimalFormat("#.##");
                p.sendMessage(Core.getInstance().getMessages().getPlayerStats().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()).replaceAll("%WR%",formatter.format(statsPlayer.getWinLooseRatio())).replaceAll("%WINS%",String.valueOf(statsPlayer.getMatchesWon())).replaceAll("%LOOSES%",String.valueOf(statsPlayer.getMatchesLost())));
            }else{
                p.sendMessage(Core.getInstance().getMessages().getErrorOccurred().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
            }
            return;
        }
        if(args.length > 1){
            if(!p.hasPermission("staking.player.statsother")){
                p.sendMessage(Core.getInstance().getMessages().getNoPermission().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
                return;
            }
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if(targetPlayer == null){
                p.sendMessage(Core.getInstance().getMessages().getTargetPlayerNotFound().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()).replaceAll("%TARGETPLAYER%",args[1]));
                return;
            }
            StatsPlayer statsPlayer = Core.getInstance().getStatsManager().getStatsPlayer(targetPlayer);
            if(statsPlayer != null){
                NumberFormat formatter = new DecimalFormat("#.##");
                p.sendMessage(Core.getInstance().getMessages().getTargetPlayerStats().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()).replaceAll("%TARGETPLAYER%",args[1]).replaceAll("%WR%",formatter.format(statsPlayer.getWinLooseRatio())).replaceAll("%WINS%",String.valueOf(statsPlayer.getMatchesWon())).replaceAll("%LOOSES%",String.valueOf(statsPlayer.getMatchesLost())));
            }else{
                p.sendMessage(Core.getInstance().getMessages().getErrorOccurred().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
            }
            return;
        }

        //Check to see if sending the request to him/her self
        if(p.getName().equalsIgnoreCase(args[0])){
            p.sendMessage(Core.getInstance().getMessages().getDuelSentToSelf().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
            return;
        }


    }

}