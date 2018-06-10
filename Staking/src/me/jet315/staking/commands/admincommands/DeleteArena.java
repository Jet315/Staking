package me.jet315.staking.commands.admincommands;

import me.jet315.staking.Core;
import me.jet315.staking.arenas.Arena;
import me.jet315.staking.commands.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteArena extends CommandExecutor {

    /**
     * Find house command
     */

    public DeleteArena() {
        setCommand("deletearena");
        setPermission("staking.admin.deletearena");
        setLength(2);
        setPlayer();
        setUsage("/stake deletearena <name>");
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        //Can cast as was already checked
        Player p = (Player) sender;
        String arenaID = args[1];

        if(!Core.getInstance().getArenaManager().doesArenaExist(arenaID)){
            p.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() +ChatColor.RED +"The arena " + ChatColor.GREEN + arenaID + ChatColor.RED + " does not exist!");
            return;
        }
        Arena arena = Core.getInstance().getArenaManager().getActiveArenas().get(arenaID);
        if(!arena.isArenaReady()){
            p.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() +ChatColor.RED +"The arena is currently in use, please wait!");
        }

        Core.getInstance().getArenaManager().deleteArena(arenaID);
        p.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() +ChatColor.GREEN + arenaID + ChatColor.GOLD + " has been deleted!");

    }
}
