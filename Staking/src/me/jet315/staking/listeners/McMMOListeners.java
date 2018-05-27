package me.jet315.staking.listeners;

import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.events.skills.secondaryabilities.SecondaryAbilityEvent;
import me.jet315.staking.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class McMMOListeners implements Listener {

    @EventHandler
    public void onMcMMOAbilityActivate(McMMOPlayerAbilityActivateEvent e){
        if(Core.getInstance().getStakingPlayerManager().getStakePlayer(e.getPlayer()) != null){
            if(Core.getInstance().getProperties().isDisableMcMMOAbilitiesDuringDuels()){
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onMcMMOSecondAbilityActivate(SecondaryAbilityEvent e){
        if(Core.getInstance().getStakingPlayerManager().getStakePlayer(e.getPlayer()) != null){
            if(Core.getInstance().getProperties().isDisableMcMMOAbilitiesDuringDuels()){
                e.setCancelled(true);
            }
        }
    }

}
