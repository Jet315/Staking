package me.jet315.staking.manager;

import me.jet315.staking.Core;
import me.jet315.staking.arenas.inventory.IInterfaceKitSave;
import me.jet315.staking.arenas.inventory.KitInventorySave;
import me.jet315.staking.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class KitManager {

    private HashMap<String,IInterfaceKitSave> availableKits = new HashMap<>();
    private int size = 9;
    private String inventoryName = "Kits";

    public KitManager(){
        createConfig();
        loadInventoryData();
        loadKitsFromConfig();
        //TODO loadInventory
    }

    private void loadInventoryData(){
        FileConfiguration config = getFileConfiguration();
        size = config.getInt("KitsGUI.Size");
        inventoryName = ChatColor.translateAlternateColorCodes('&',config.getString("KitsGUI.DisplayName"));
    }
    private void loadKitsFromConfig(){
        FileConfiguration config = getFileConfiguration();
        for (String kitName : config.getConfigurationSection("Kits").getKeys(false)) {
            try {
                //Get the path as a string, so it is easy to get future values from the config
                String path = "Kits." + kitName;
                //Display Item
                ItemStack displayItem = Utils.parseItemStackFromString(config.getString(path + ".DisplayIcon.DisplayMaterial"));
                if(config.getBoolean(path + ".DisplayIcon.HideEnchants")){
                    ItemMeta displayMeta = displayItem.getItemMeta();
                    displayMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    displayItem.setItemMeta(displayMeta);
                }
                ItemStack headSlot = Utils.parseItemStackFromString(config.getString(path + ".Items.HeadSlot"));
                ItemStack chestSlot = Utils.parseItemStackFromString(config.getString(path + ".Items.ChestSlot"));
                ItemStack legSlot = Utils.parseItemStackFromString(config.getString(path + ".Items.LegSlot"));
                ItemStack bootSlot = Utils.parseItemStackFromString(config.getString(path + ".Items.BootSlot"));
                ArrayList<ItemStack> inventoryItems = new ArrayList<>();

                for(String string : config.getStringList(path + ".Items.Inventory")){

                    inventoryItems.add(Utils.parseItemStackFromString(string));
                }
                IInterfaceKitSave kit = new KitInventorySave(displayItem,headSlot,chestSlot,legSlot,bootSlot,inventoryItems);
                availableKits.put(kitName,kit);

            }catch(Exception e){
                System.out.println("[Staking] Error loading kit: " + kitName);
            }
        }
        System.out.println("[Staking] " + availableKits.size() + " kits loaded!");
    }

    public Inventory getKitInventory(){
        Inventory inventory = Bukkit.createInventory(null,size,inventoryName);
        int loops = 0;
        for(IInterfaceKitSave kit : availableKits.values()){
            inventory.setItem(loops,kit.getDisplayItem());
            loops++;
        }

        return inventory;
    }


    private void createConfig() {
        try {
            if (!Core.getInstance().getDataFolder().exists()) {
                Core.getInstance().getDataFolder().mkdirs();
            }
            File file = new File(Core.getInstance().getDataFolder(), "kitsconfig.yml");
            if (!file.exists()) {
                Core.getInstance().getLogger().info("kitsconfig.yml not found, creating!");
                Core.getInstance().saveResource("kitsconfig.yml",false);
            } else {
                Core.getInstance().getLogger().info("kitsconfig.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FileConfiguration getFileConfiguration() {
         return YamlConfiguration.loadConfiguration(new File(Core.getInstance().getDataFolder(),"kitsconfig.yml"));
    }
    public HashMap<String, IInterfaceKitSave> getAvailableKits() {
        return availableKits;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public int getSize() {
        return size;
    }
}
