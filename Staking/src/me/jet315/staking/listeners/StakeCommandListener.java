package me.jet315.staking.listeners;

import me.jet315.staking.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class StakeCommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if(Core.getInstance().getStakingPlayerManager().getStakePlayer(e.getPlayer()) != null){
            if(e.getPlayer().hasPermission("staking.admin.bypasscommand")) return;
            String message[] = e.getMessage().split(" ");
            String firstCommand;
            if (message.length > 0) {
                firstCommand = message[0];
            } else {
                firstCommand = e.getMessage();
            }
            for (String s : Core.getInstance().getProperties().getCommandsToBlock()) {
                if (firstCommand.equalsIgnoreCase(s)) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(Core.getInstance().getMessages().getBlockedCommandMessage().replaceAll("%PLUGINPREFIX%",Core.getInstance().getProperties().getPluginsPrefix()));
                    return;
                }

            }
        }

    }

    }