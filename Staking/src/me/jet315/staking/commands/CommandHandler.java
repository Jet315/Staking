package me.jet315.staking.commands;

import me.jet315.staking.Core;
import me.jet315.staking.commands.admincommands.CreateArena;
import me.jet315.staking.commands.admincommands.DeleteArena;
import me.jet315.staking.commands.admincommands.Reload;
import me.jet315.staking.commands.admincommands.SetSpawn;
import me.jet315.staking.commands.defaultcommands.Stake;
import me.jet315.staking.commands.defaultcommands.Stats;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler implements org.bukkit.command.CommandExecutor {

    private Map<String, CommandExecutor> commands = new HashMap<String, CommandExecutor>();

    public CommandHandler() {
        //Player commands
        commands.put("*",new Stake());
        commands.put("createarena",new CreateArena());
        commands.put("deletearena",new DeleteArena());
        commands.put("setspawn",new SetSpawn());
        commands.put("reload",new Reload());
        commands.put("stats",new Stats());

    }
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (cmd.getName().equalsIgnoreCase("stake")) {
            if (args.length == 0) {

                if(sender instanceof Player) {

                    sender.sendMessage(Core.getInstance().getMessages().getInvalidSyntax().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
                    return true;
                }else{
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',Core.getInstance().getProperties().getPluginsPrefix() + "&cMust be a Player to do the command!!"));
                    return true;
                }
            }


            if (args[0] != null) {
                String name = args[0];
                    CommandExecutor command = commands.get(name);

                    if(command == null) command = commands.get("*");

                    if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
                        sender.sendMessage(Core.getInstance().getProperties().getPluginsPrefix() + Core.getInstance().getMessages().getNoPermission().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
                        return true;

                    }

                    if (!command.isBoth()) {
                        if (command.isConsole() && sender instanceof Player) {
                            sender.sendMessage(ChatColor.RED + "Only console can use that command!");
                            return true;
                        }
                        if (command.isPlayer() && sender instanceof ConsoleCommandSender) {
                            sender.sendMessage(ChatColor.RED + "Only players can use that command!");
                            return true;
                        }
                    }
                    if(!name.equalsIgnoreCase("stats")) {
                        if (command.getLength() > args.length) {
                            sender.sendMessage(ChatColor.RED + "Usage: " + command.getUsage());
                            return true;
                        }
                    }

                    command.execute(sender, args);
                    return true;
                }

            sender.sendMessage(Core.getInstance().getMessages().getInvalidSyntax().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
        }
        return true;
    }
}
