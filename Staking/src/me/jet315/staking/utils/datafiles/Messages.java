package me.jet315.staking.utils.datafiles;

import me.jet315.staking.Core;
import me.jet315.staking.utils.titles.Title;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class Messages implements DataFile{

    /**
     * Stores the plugins instance
     */
    private Core instance;

    /**
     * Properties
     */
    private String noPermission = "No perms!";
    private String targetPlayerIsInDuel = "Player is in duel";
    private String playerInDuel = "You are already in duel!";
    private String duelExpired = "You duel has expired";
    private String invalidTarget = "That fella does not exist!";
    private String duelRequest = "%PLAYER% has challenged you to a duel!";
    private String duelCooldown = "please wait!";
    private String duelSent = "please wait!";
    private String duelSentToSelf = "No staking yourself!";
    private String notEnoughFunds = "No funds!";
    private String noArenaFound = "No arena found!";
    private String forceKickOutOfDuel = "You have been force kicked from the anena!";
    private String matchEndDueToTime = "Match ended!";
    private HashMap<Integer,Title> titleMatchCountDown = new HashMap<>();
    private HashMap<Integer,String> messageMatchCountDown = new HashMap<>();
    private String winnerMessage = "You won!";
    private String looserMessage = "You lost!";
    private String winnerMessageDueToOpponentDisconect = "You won, opponent lost";
    private String noKitSelected = "Come on! Select a kit!";
    private String invalidSyntax = "/stake <Player>";

    private String statsNotEnabled = "Stats not enabled";
    private String playerStats = "Your stats are great!";
    private String targetPlayerStats = "That persons stats are even better!";
    private String targetPlayerNotFound = "The person needs to be online..";
    private String blockedCommandMessage = "Blocked!";
    private String errorOccurred = "Oh dear, a bug!";

    /**
     * Stores the File Configuration
     */
    private FileConfiguration config;


    public Messages(Core instance){
        this.instance = instance;
        this.createConfig();
        this.reloadConfig();
        this.loadValuesFromConfig();
    }

    @Override
    public void loadValuesFromConfig() {
        noPermission = ChatColor.translateAlternateColorCodes('&',config.getString("NoPermission"));
        targetPlayerIsInDuel = ChatColor.translateAlternateColorCodes('&',config.getString("TargetPlayerIsInDuel"));
        playerInDuel = ChatColor.translateAlternateColorCodes('&',config.getString("PlayerInDuel"));
        duelExpired = ChatColor.translateAlternateColorCodes('&',config.getString("DuelExpired"));
        invalidTarget = ChatColor.translateAlternateColorCodes('&',config.getString("InvalidTarget"));
        duelRequest = ChatColor.translateAlternateColorCodes('&',config.getString("DuelRequest"));
        duelCooldown = ChatColor.translateAlternateColorCodes('&',config.getString("DuelCooldown"));
        duelSent = ChatColor.translateAlternateColorCodes('&',config.getString("DuelSent"));
        duelSentToSelf = ChatColor.translateAlternateColorCodes('&',config.getString("DuelSentToSelf"));
        notEnoughFunds = ChatColor.translateAlternateColorCodes('&',config.getString("NotEnoughFunds"));
        noArenaFound = ChatColor.translateAlternateColorCodes('&',config.getString("NoArenaFound"));
        forceKickOutOfDuel = ChatColor.translateAlternateColorCodes('&',config.getString("ForceKickOutOfDuel"));
        try {
            if (config.getStringList("TitleMatchCountDown") != null) {
                for (String s : config.getStringList("TitleMatchCountDown")) {
                    String timeSplit[] = s.split(":");
                    int timeToShowAt = Integer.valueOf(timeSplit[0]);
                    String title = timeSplit[1];

                    if(title.split("|").length > 0){
                        String titles[] = title.split("\\|");

                        titleMatchCountDown.put(timeToShowAt,new Title(titles[0],titles[1]));
                    }else{
                        titleMatchCountDown.put(timeToShowAt,new Title(title,null));
                    }
                }
            }
        }catch (Exception e){
            System.out.println("[Staking] Error occurred while processing the config value MatchCountDown (messages.yml)");
        }

        try {
            if (config.getStringList("MessageMatchCountDown") != null) {
                for (String s : config.getStringList("MessageMatchCountDown")) {
                    String timeSplit[] = s.split(":");
                    int timeToShowAt = Integer.valueOf(timeSplit[0]);
                    String message = ChatColor.translateAlternateColorCodes('&',timeSplit[1]);
                    messageMatchCountDown.put(timeToShowAt,message);
                }
            }
        }catch (Exception e){
            System.out.println("[Staking] Error occurred while processing the config value MessageMatchCountDown (messages.yml)");
        }

        matchEndDueToTime = ChatColor.translateAlternateColorCodes('&',config.getString("MatchEndDueToTime"));
        winnerMessage = ChatColor.translateAlternateColorCodes('&',config.getString("WinnerMessage"));
        looserMessage = ChatColor.translateAlternateColorCodes('&',config.getString("LooserMessage"));
        winnerMessageDueToOpponentDisconect = ChatColor.translateAlternateColorCodes('&',config.getString("WinnerMessageDueToOpponentDisconnect"));
        noKitSelected = ChatColor.translateAlternateColorCodes('&',config.getString("NoKitSelected"));
        invalidSyntax = ChatColor.translateAlternateColorCodes('&',config.getString("InvalidSyntax"));


        statsNotEnabled = ChatColor.translateAlternateColorCodes('&',config.getString("StatsNotEnabled"));
        playerStats = ChatColor.translateAlternateColorCodes('&',config.getString("PlayersStats"));
        targetPlayerStats = ChatColor.translateAlternateColorCodes('&',config.getString("TargetPlayerStats"));
        targetPlayerNotFound = ChatColor.translateAlternateColorCodes('&',config.getString("TargetPlayerNotFound"));
        blockedCommandMessage = ChatColor.translateAlternateColorCodes('&',config.getString("BlockedCommandMessage"));
        errorOccurred = ChatColor.translateAlternateColorCodes('&',config.getString("ErrorOccurred"));

    }

    @Override
    public void createConfig() {
        try {
            if (!instance.getDataFolder().exists()) {
                instance.getDataFolder().mkdirs();
            }
            File file = new File(instance.getDataFolder(), "messages.yml");
            if (!file.exists()) {
                instance.getLogger().info("messages.yml not found, creating!");
                instance.saveResource("messages.yml",false);
            } else {
                instance.getLogger().info("messages.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(),"messages.yml"));
    }

    public String getNoPermission() {
        return noPermission;
    }

    public String getTargetPlayerIsInDuel() {
        return targetPlayerIsInDuel;
    }

    public String getPlayerInDuel() {
        return playerInDuel;
    }

    public String getDuelExpired() {
        return duelExpired;
    }

    public String getInvalidTarget() {
        return invalidTarget;
    }

    public String getDuelRequest() {
        return duelRequest;
    }

    public String getDuelCooldown() {
        return duelCooldown;
    }

    public String getDuelSent() {
        return duelSent;
    }

    public String getDuelSentToSelf() {
        return duelSentToSelf;
    }

    public String getNotEnoughFunds() {
        return notEnoughFunds;
    }

    public String getNoArenaFound() {
        return noArenaFound;
    }

    public String getForceKickOutOfDuel() {
        return forceKickOutOfDuel;
    }

    public String getMatchEndDueToTime() {
        return matchEndDueToTime;
    }

    public String getLooserMessage() {
        return looserMessage;
    }

    public String getWinnerMessage() {
        return winnerMessage;
    }

    public HashMap<Integer, Title> getTitleMatchCountDown() {
        return titleMatchCountDown;
    }

    public HashMap<Integer, String> getMessageMatchCountDown() {
        return messageMatchCountDown;
    }

    public String getWinnerMessageDueToOpponentDisconect() {
        return winnerMessageDueToOpponentDisconect;
    }

    public String getNoKitSelected() {
        return noKitSelected;
    }

    public String getInvalidSyntax() {
        return invalidSyntax;
    }

    public String getStatsNotEnabled() {
        return statsNotEnabled;
    }

    public String getPlayerStats() {
        return playerStats;
    }

    public String getTargetPlayerStats() {
        return targetPlayerStats;
    }

    public String getTargetPlayerNotFound() {
        return targetPlayerNotFound;
    }

    public String getErrorOccurred() {
        return errorOccurred;
    }

    public String getBlockedCommandMessage() {
        return blockedCommandMessage;
    }
}
