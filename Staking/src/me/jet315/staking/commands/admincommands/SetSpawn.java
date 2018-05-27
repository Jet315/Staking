package me.jet315.staking.commands.admincommands;

import me.jet315.staking.Core;
import me.jet315.staking.commands.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawn extends CommandExecutor {

    /**
     * Find house command
     */

    public SetSpawn() {
        setCommand("setspawn");
        setPermission("staking.admin.setspawn");
        setLength(3);
        setPlayer();
        setUsage("/stake setspawn <1 or 2> <arena>");
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        //Can cast as was already checked
        Player p = (Player) sender;
        String arenaID = args[2];
        if (Core.getInstance().getArenaManager().doesArenaExist(arenaID)) {
            try{
                int number = Integer.parseInt(args[1]);
                if(number != 1 && number != 2){
                    p.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() + ChatColor.RED + "The spawn point must either be 1 or 2, not " + number);
                    return;
                }
                if(number == 1){
                    Core.getInstance().getArenaManager().setSpawnLocation1(p.getLocation(),arenaID);
                }else{
                    Core.getInstance().getArenaManager().setSpawnLocation2(p.getLocation(),arenaID);
                }
                p.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() + ChatColor.GREEN + "The spawn location has successfully been set!");

                if(Core.getInstance().getArenaManager().getActiveArenas().get(arenaID).isArenaReady()){
                    p.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() + ChatColor.GREEN  +"The arena is now active!");
                }

            }catch (NumberFormatException e){
                p.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() + ChatColor.RED + "The argument " + ChatColor.GREEN + args[1] + ChatColor.RED + " is not an integer");
            }

            return;
        } else {
            p.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() + ChatColor.RED + "The arena " + ChatColor.GREEN + arenaID + ChatColor.RED + " does not exist!");
        }
    }

    }