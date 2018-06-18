package me.jet315.staking;

import me.jet315.staking.arenas.Arena;
import me.jet315.staking.arenas.ArenaManager;
import me.jet315.staking.commands.CommandHandler;
import me.jet315.staking.database.Database;
import me.jet315.staking.database.SQLite;
import me.jet315.staking.listeners.*;
import me.jet315.staking.manager.KitManager;
import me.jet315.staking.manager.StakeManager;
import me.jet315.staking.manager.StakingPlayerManager;
import me.jet315.staking.manager.StatsManager;
import me.jet315.staking.utils.datafiles.GUI;
import me.jet315.staking.utils.datafiles.Messages;
import me.jet315.staking.utils.datafiles.Properties;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {

    /**
     * Stores the plugins instance
     */
    private static Core instance;
    /**
     * Stores properties of the plugin
     */
    private Properties properties;
    /**
     * Stores the messages of the plugin
     */
    private Messages messages;

    /**
     * Stores GUI options of the plugin
     */
    private GUI gui;

    /**
     * Player Managers
     */
    private StakeManager stakeManager;
    private StakingPlayerManager stakingPlayerManager;
    private StatsManager statsManager;
    /**
     * Arena Managers
     */
    private ArenaManager arenaManager;
    private KitManager kitManager;

    /**
     * Vault
     */
    public static Economy economy = null;

    public static String serverVersion;

    /**
     * Database
     */
    private Database db;

    @Override
    public void onEnable() {
        //Just cool knowing how long the plugin takes to enable
        long startTime = System.currentTimeMillis();
        System.out.println("\n[Staking] Initializing Plugin");

        instance = this;
        properties = new Properties(this);
        messages = new Messages(this);
        gui = new GUI(this);
        stakeManager = new StakeManager();
        stakingPlayerManager = new StakingPlayerManager();
        arenaManager = new ArenaManager();
        kitManager = new KitManager();
        statsManager = new StatsManager();

        this.db = new SQLite(properties.getDbName());
        this.db.load();

        //Register Command
        getCommand("stake").setExecutor(new CommandHandler());
        serverVersion = Bukkit.getServer().getClass().getPackage().getName();
        serverVersion = serverVersion.substring(serverVersion.lastIndexOf(".") + 1);
        setupEconomy();
        registerEvents();

        System.out.println("[Staking] Initializing Complete - Time took " + String.valueOf(System.currentTimeMillis()-startTime) +"Ms\n");

    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getScheduler().cancelTasks(this);
        stakingPlayerManager.forceKickAllPlayersFromDuels();

        for (Arena arena : arenaManager.getActiveArenas().values()) {
            for (Block b : arena.getBlocksChangedToBarriers()) {
                b.setType(Material.AIR);
            }
            arena.setResetArena(true);
        }

        //save player data
        for(StatsPlayer statsPlayer : statsManager.getPlayerStats().values()){
            db.forceSyncPlayersData(statsPlayer);
        }
        arenaManager = null;
        kitManager = null;
        stakingPlayerManager = null;
        statsManager = null;
        gui = null;
        db = null;
        messages = null;
        properties = null;

        instance = null;
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new StakeInventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new KitsInventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new StakingInventoryDrag(), this);
        Bukkit.getPluginManager().registerEvents(new StakeInventoryClose(), this);
        Bukkit.getPluginManager().registerEvents(new StakeKitInventoryClose(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
        Bukkit.getPluginManager().registerEvents(new McMMOListeners(), this);
        Bukkit.getPluginManager().registerEvents(new DuelQuit(), this);
        Bukkit.getPluginManager().registerEvents(new StatsListeners(), this);
        Bukkit.getPluginManager().registerEvents(new StakeCommandListener(), this);
        Bukkit.getPluginManager().registerEvents(new StakeItemDrop(), this);
        if(properties.isEnableQuickStatsCommand()){
            Bukkit.getPluginManager().registerEvents(new StatsCommandListener(),this);
        }
    }

    public void reloadPlugin() {
        Bukkit.getServer().getScheduler().cancelTasks(this);
        stakingPlayerManager.forceKickAllPlayersFromDuels();
        //Reset barriers
        for (Arena arena : arenaManager.getActiveArenas().values()) {
            for (Block b : arena.getBlocksChangedToBarriers()) {
                b.setType(Material.AIR);
            }
            arena.setResetArena(true);
        }

        arenaManager = null;
        kitManager = null;
        stakingPlayerManager = null;
        gui = null;
        messages = null;
        properties = null;

        properties = new Properties(this);
        messages = new Messages(this);
        gui = new GUI(this);
        stakingPlayerManager = new StakingPlayerManager();
        arenaManager = new ArenaManager();
        kitManager = new KitManager();


    }

    public Database getDb() {
        return db;
    }

    /**
     * Vault - https://www.spigotmc.org/resources/vault.34315/
     *
     * @return
     */
    private boolean setupEconomy() {
        try {
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
            }

            return (economy != null);
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }


    public Properties getProperties() {
        return properties;
    }

    public Messages getMessages() {
        return messages;
    }

    public static Core getInstance() {
        return instance;
    }

    public GUI getGui() {
        return gui;
    }

    public StakingPlayerManager getStakingPlayerManager() {
        return stakingPlayerManager;
    }

    public StakeManager getStakeManager() {
        return stakeManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }

    //TODO
    //Add permission for kits
    //Add exp to inventory
}
