package me.jet315.staking.utils.datafiles;

import me.jet315.staking.Core;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Properties implements DataFile{

    /**
     * Stores the plugins instance
     */
    private Core instance;

    /**
     * Stores the plugins prefix
     */
    private String pluginsPrefix = "Staking: ";

    private int timeForDuelRequestToExpire = 15;

    private int confirmTime = 3;


    /**
     * Stats options
     */
    private boolean saveStatsInformation = true;
    private String dbName = "stats";
    private boolean enableQuickStatsCommand = true;

    /**
     * Pvp options
     */

    private int matchCountDownTime = 15;
    private int matchTime = 300;
    private boolean disableMcMMOAbilitiesDuringDuels = true;
    private String whereToTeleportAfterDuel;


    /**
     * Stores the File Configuration
     */
    private FileConfiguration config;

    public Properties(Core instance){
        this.instance = instance;
        this.createConfig();
        this.reloadConfig();
        this.loadValuesFromConfig();
    }


    @Override
    public void loadValuesFromConfig() {
        pluginsPrefix = ChatColor.translateAlternateColorCodes('&',config.getString("PluginsPrefix"));
        timeForDuelRequestToExpire = config.getInt("TimeForDuelRequestToExpire");
        confirmTime = config.getInt("ConfirmTime");
        disableMcMMOAbilitiesDuringDuels = config.getBoolean("DisableMcMMOAbilitiesDuringDuels");
        matchCountDownTime = config.getInt("MatchCountDownTime");
        if(matchCountDownTime < 0){
            matchCountDownTime = 0;
        }

        matchTime = config.getInt("MatchTime");
        if(matchTime <= 0){
            matchTime = 30;
            System.out.println("[Staking] MatchTime (found within config.yml) cannot be < or = 0");
        }

        whereToTeleportAfterDuel = config.getString("WhereToTeleportAfterDuel");

        //stats
        dbName = config.getString("SQLiteTableName");
        saveStatsInformation = config.getBoolean("SaveStatInformation");
        enableQuickStatsCommand = config.getBoolean("EnableQuickStatsCommand");

    }


    @Override
    public void createConfig() {
        try {
            if (!instance.getDataFolder().exists()) {
                instance.getDataFolder().mkdirs();
            }
            File file = new File(instance.getDataFolder(), "config.yml");
            if (!file.exists()) {
                instance.getLogger().info("Config.yml not found, creating!");
                instance.saveDefaultConfig();
            } else {
                instance.getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(),"config.yml"));
    }

    public String getPluginsPrefix() {
        return pluginsPrefix;
    }

    public int getTimeForDuelRequestToExpire() {
        return timeForDuelRequestToExpire;
    }

    public int getConfirmTime() {
        return confirmTime;
    }

    public int getMatchCountDownTime() {
        return matchCountDownTime;
    }

    public int getMatchTime() {
        return matchTime;
    }

    public String getWhereToTeleportAfterDuel() {
        return whereToTeleportAfterDuel;
    }

    public boolean isDisableMcMMOAbilitiesDuringDuels() {
        return disableMcMMOAbilitiesDuringDuels;
    }

    public boolean isSaveStatsInformation() {
        return saveStatsInformation;
    }

    public String getDbName() {
        return dbName;
    }

    public boolean isEnableQuickStatsCommand() {
        return enableQuickStatsCommand;
    }
}
