package me.jet315.staking.utils.titles;

import me.jet315.staking.Core;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Title implements ITitle{

    private String title;
    private String subTitle;

    public Title(String title, String subTitle) {
        this.title = ChatColor.translateAlternateColorCodes('&',title);
        this.subTitle = ChatColor.translateAlternateColorCodes('&',subTitle);
    }


    @Override
    public void playTitle(Player p) {

        if(Core.serverVersion.startsWith("v1_12")) {
            p.sendTitle(title, subTitle == null ? " " : subTitle, 5, 25, 5);
        }else{
            p.sendTitle(title, subTitle == null ? " " : subTitle);
        }
    }
}
