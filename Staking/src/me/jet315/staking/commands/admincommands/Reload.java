package me.jet315.staking.commands.admincommands;

import me.jet315.staking.Core;
import me.jet315.staking.commands.CommandExecutor;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Reload extends CommandExecutor {

    public Reload(){
        setCommand("reload");
        setPermission("staking.admin.reload");
        setLength(1);
        setBoth();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        long startTime = System.currentTimeMillis();
        sender.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() + ChatColor.GREEN +"Reloading plugin...");
        Core.getInstance().reloadPlugin();
        sender.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() + ChatColor.GREEN +"Reload complete! Time taken: " + String.valueOf(System.currentTimeMillis()-startTime) + "Ms");

    }
}
