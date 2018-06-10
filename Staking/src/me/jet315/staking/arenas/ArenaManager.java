package me.jet315.staking.arenas;

import me.jet315.staking.Core;
import me.jet315.staking.utils.LocUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class ArenaManager {

    //Stores arenas
    private HashMap<String,Arena> activeArenas = new HashMap<String,Arena>();

    /**
     * File stuff
     */
    private FileConfiguration config;
    private File configFile;

    public ArenaManager(){
        createConfig();
        loadArenas();
    }

      public void loadArenas(){
        //Total number of arenas
        int numberOfArenasSetup = 0;
        if(config.getConfigurationSection("Arenas") != null) {
            for (String arenaName : config.getConfigurationSection("Arenas").getKeys(false)) {
                Arena arena = new Arena(arenaName);
                arena.setSpawnLocation1(LocUtils.locationFromString(config.getString("Arenas." + arenaName + ".spawnLocation1")));
                arena.setSpawnLocation2(LocUtils.locationFromString(config.getString("Arenas." + arenaName + ".spawnLocation2")));
                activeArenas.put(arenaName, arena);
                if (arena.isArenaReady()) {
                    numberOfArenasSetup++;
                } else {
                    System.out.println("[Staking] the arena " + arenaName + " has not been fully setup");
                }
            }
        }
          System.out.println("[Staking] " + numberOfArenasSetup +" arenas have successfully fully loaded.");

      }

    private void createConfig() {
        try {
            if (!Core.getInstance().getDataFolder().exists()) {
                Core.getInstance().getDataFolder().mkdirs();
            }
            configFile = new File(Core.getInstance().getDataFolder(), "arenas.yml");
            if (!configFile.exists()) {
                Core.getInstance().getLogger().info("arenas.yml not found, creating!");
                Core.getInstance().saveResource("arenas.yml",false);
            } else {
                Core.getInstance().getLogger().info("arenas.yml found, loading!");
            }
            config = YamlConfiguration.loadConfiguration(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createArena(String gameID){
        //Putting into config
        Arena arena = new Arena(gameID);

        config.set("Arenas." + gameID + ".spawnLocation1","null");
        config.set("Arenas." + gameID + ".spawnLocation2","null");
        //Save to config
        saveConfig();

        activeArenas.put(gameID,arena);

    }
    public void deleteArena(String gameID){
        //Putting into config
        if(activeArenas.containsKey(gameID)){
            activeArenas.remove(gameID);
            config.set("Arenas." + gameID, null);

            saveConfig();
        }

    }
    public boolean doesArenaExist(String gameID){
        return activeArenas.containsKey(gameID);
    }

    /**
     *
     * @param locationOfPlayer The Location of the player performing the command
     * @param gameID The game Id
     */
    public void setSpawnLocation1(Location locationOfPlayer, String gameID){
        World world = locationOfPlayer.getWorld();
        double x = locationOfPlayer.getX() < 0 ? ((int) locationOfPlayer.getX())-0.5 : ((int) locationOfPlayer.getX())+0.5;
        double y = locationOfPlayer.getY() < 0 ? ((int) locationOfPlayer.getY())-0.5 : ((int) locationOfPlayer.getY())+0.5;
        double z = locationOfPlayer.getZ() < 0 ? ((int) locationOfPlayer.getZ())-0.5 : ((int) locationOfPlayer.getZ())+0.5;
        config.set("Arenas."+ gameID + ".spawnLocation1",world.getName()+","+x +","+ y+","+z +","+locationOfPlayer.getYaw() +","+locationOfPlayer.getPitch());

        activeArenas.get(gameID).setSpawnLocation1(new Location(world,x,y,z,locationOfPlayer.getYaw(),locationOfPlayer.getPitch()));
        saveConfig();

    }
    /**
     *
     * @param locationOfPlayer The Location of the player performing the command
     * @param gameID The game Id
     */
    public void setSpawnLocation2(Location locationOfPlayer, String gameID){

        World world = locationOfPlayer.getWorld();
        double x = locationOfPlayer.getX() < 0 ? ((int) locationOfPlayer.getX())-0.5 : ((int) locationOfPlayer.getX())+0.5;
        double y = locationOfPlayer.getY() < 0 ? ((int) locationOfPlayer.getY())-0.5 : ((int) locationOfPlayer.getY())+0.5;
        double z = locationOfPlayer.getZ() < 0 ? ((int) locationOfPlayer.getZ())-0.5 : ((int) locationOfPlayer.getZ())+0.5;
        config.set("Arenas."+ gameID + ".spawnLocation2",world.getName()+","+x +","+ y+","+z +","+locationOfPlayer.getYaw() +","+locationOfPlayer.getPitch());

        activeArenas.get(gameID).setSpawnLocation2(new Location(world,x,y,z,locationOfPlayer.getYaw(),locationOfPlayer.getPitch()));
        saveConfig();

    }

    private void saveConfig(){

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @return A free arena, null if none available
     */
    public Arena getFreeArena(){
        for(Arena arena : activeArenas.values()){
            if(arena.isArenaReady()){
                return arena;
            }
        } return null;
    }

    public HashMap<String, Arena> getActiveArenas() {
        return activeArenas;
    }
}
