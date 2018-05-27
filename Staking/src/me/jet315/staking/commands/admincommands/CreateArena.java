package me.jet315.staking.commands.admincommands;

import me.jet315.staking.Core;
import me.jet315.staking.commands.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateArena extends CommandExecutor {

    /**
     * Find house command
     */

    public CreateArena() {
        setCommand("createarena");
        setPermission("staking.admin.createarena");
        setLength(2);
        setPlayer();
        setUsage("/stake createarena <name>");
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        //Can cast as was already checked
        Player p = (Player) sender;
        String arenaID = args[1];
        if(Core.getInstance().getArenaManager().doesArenaExist(arenaID)){
            p.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() +ChatColor.RED +"The arena " + ChatColor.GREEN + arenaID + ChatColor.RED + " already exists!");
            return;
        }

        Core.getInstance().getArenaManager().createArena(arenaID);
        p.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() +ChatColor.GREEN + arenaID + ChatColor.GOLD + " has been created!");
        p.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() +ChatColor.GOLD +"Now set the first spawnpoint using " + ChatColor.GREEN + "/stake setspawn <1 or 2> " + arenaID);

    }
}
