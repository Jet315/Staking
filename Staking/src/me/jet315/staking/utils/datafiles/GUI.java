package me.jet315.staking.utils.datafiles;

import me.jet315.staking.Core;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;

public class GUI implements DataFile {

    /**
     * Stores the plugins instance
     */
    private Core instance;

    /**
     * Properties
     */
    private String stakeInventoryName = "< >";

    private ItemStack dividerItem;

    //accept/not accepted items
    private ItemStack acceptItem;
    private String acceptDisplayName;
    private ItemStack notAcceptedItem;
    private String notAcceptedDisplayName;


    //Money
    private boolean isMoneyEnabledInGUI = true;
    private ItemStack playersMoneyItem;
    private ItemStack opponentsMoneyItem;
    private double rightClickAmount = 0;
    private double leftClickAmount = 0;
    private double shiftRightClickAmount = 0;
    private double shiftLeftClickAmount = 0;
    private String playersMoneyDisplayName = "&7Your current stake is &e$%AMOUNT%";
    private ArrayList<String> playersMoneyLore = new ArrayList<>();
    private String opponentsMoneyDisplayName = "&7Their current stake is &e$%OPPONENTSAMMOUNT%";
    private ArrayList<String> opponentsMoneyLore = new ArrayList<>();

    //dud item
    private ItemStack dudItem;

    //sounds
    private Sound changeMoneyNoise;
    private Sound addItemNoise;
    private Sound countDownNoise;
    private Sound endCountDownNoise;


    /**
     * Stores the File Configuration
     */
    private FileConfiguration config;


    public GUI(Core instance) {
        this.instance = instance;
        this.createConfig();
        this.reloadConfig();
        this.loadValuesFromConfig();
    }

    @Override
    public void loadValuesFromConfig() {
        stakeInventoryName = ChatColor.translateAlternateColorCodes('&', config.getString("StakeInventoryName"));

        String dividerItemStack = config.getString("DividerItem");
        String dividerItemStackSplit[] = dividerItemStack.split(":");
        if (dividerItemStackSplit.length > 1) {
            dividerItem = new ItemStack(Material.valueOf(dividerItemStackSplit[0]), 1, Short.valueOf(dividerItemStackSplit[1]));
        } else {
            dividerItem = new ItemStack(Material.valueOf(dividerItemStackSplit[0]));
        }
        ItemMeta dividerMeta = dividerItem.getItemMeta();
        dividerMeta.setDisplayName(" ");
        dividerItem.setItemMeta(dividerMeta);

        isMoneyEnabledInGUI = config.getBoolean("EnableMoneyInGUI");

        String moneyItemStack = config.getString("MoneyItem");
        String dividerMoneyItemStack[] = moneyItemStack.split(":");
        Material material = Material.valueOf(dividerMoneyItemStack[0]);
        if (material != null) {
            if (dividerMoneyItemStack.length > 1) {
                playersMoneyItem = new ItemStack(material, 1, Short.valueOf(dividerMoneyItemStack[1]));
                opponentsMoneyItem = new ItemStack(material, 1, Short.valueOf(dividerMoneyItemStack[1]));
            } else {
                playersMoneyItem = new ItemStack(material);
                opponentsMoneyItem = new ItemStack(material);
            }
        } else {
            playersMoneyItem = null;
            opponentsMoneyItem = null;
            for (int i = 0; i < 10; i++)
                System.out.println("Money Item in the guiconfig.yml is invalid. This will cause errors");
        }
        //options for money
        rightClickAmount = config.getDouble("RightClick");
        leftClickAmount = config.getDouble("LeftClick");
        shiftRightClickAmount = config.getDouble("ShiftRightClick");
        shiftLeftClickAmount = config.getDouble("ShiftLeftClick");

        //Players item name + lore
        playersMoneyDisplayName = ChatColor.translateAlternateColorCodes('&', config.getString("PlayersMoneyDisplayName"));
        for (String s : config.getStringList("PlayersMoneyLore")) {
            playersMoneyLore.add(ChatColor.translateAlternateColorCodes('&', s).replaceAll("%LEFTCLICK%", String.valueOf(leftClickAmount)).replaceAll("%RIGHTCLICK%", String.valueOf(rightClickAmount)).replaceAll("%SHIFTLEFTCLICK%", String.valueOf(shiftLeftClickAmount)).replaceAll("%SHIFTRIGHTCLICK%", String.valueOf(shiftRightClickAmount)));
        }
        //Opponents item name + lore
        opponentsMoneyDisplayName = ChatColor.translateAlternateColorCodes('&', config.getString("OpponentsMoneyDisplayName"));
        for (String s : config.getStringList("OpponentsLore")) {
            opponentsMoneyLore.add(ChatColor.translateAlternateColorCodes('&', s).replaceAll("%LEFTCLICK%", String.valueOf(leftClickAmount)).replaceAll("%RIGHTCLICK%", String.valueOf(rightClickAmount)).replaceAll("%SHIFTLEFTCLICK%", String.valueOf(shiftLeftClickAmount)).replaceAll("%SHIFTRIGHTCLICK%", String.valueOf(shiftRightClickAmount)));
        }

        //load money option
        if (playersMoneyItem != null) {
            //both must not be null
            ItemMeta theirMoneyMeta = playersMoneyItem.getItemMeta();
            theirMoneyMeta.setDisplayName(playersMoneyDisplayName);
            theirMoneyMeta.setLore(playersMoneyLore);
            playersMoneyItem.setItemMeta(theirMoneyMeta);

            ItemMeta opponentsMoneyMeta = opponentsMoneyItem.getItemMeta();
            opponentsMoneyMeta.setDisplayName(opponentsMoneyDisplayName);
            opponentsMoneyMeta.setLore(opponentsMoneyLore);
            opponentsMoneyItem.setItemMeta(opponentsMoneyMeta);


        }

        //dud item
        String dudItemStack = config.getString("DudItem");
        String dudItemStackDivider[] = dudItemStack.split(":");
        Material dudMaterial = Material.valueOf(dudItemStackDivider[0]);
        if (dudMaterial != null) {
            if (dudItemStackDivider.length > 1) {
                dudItem = new ItemStack(dudMaterial, 1, Short.valueOf(dividerMoneyItemStack[1]));
            } else {
                dudItem = new ItemStack(dudMaterial);
            }
        } else {
            for (int i = 0; i < 10; i++)
                System.out.println("The dud material found in the guiconfig.yml is invalid. This will cause errors");
        }
        ItemMeta dudMeta = dudItem.getItemMeta();
        dudMeta.setDisplayName(" ");
        dudItem.setItemMeta(dudMeta);

        //accept/not accept item
        acceptDisplayName = ChatColor.translateAlternateColorCodes('&', config.getString("AcceptedDisplayName"));
        notAcceptedDisplayName = ChatColor.translateAlternateColorCodes('&', config.getString("NotAcceptedDisplayName"));


        String acceptedItemStack = config.getString("AcceptedItem");
        String acceptedItemStackSplit[] = acceptedItemStack.split(":");
        if (acceptedItemStackSplit.length > 1) {
            acceptItem = new ItemStack(Material.valueOf(acceptedItemStackSplit[0]), 1, Short.valueOf(acceptedItemStackSplit[1]));
        } else {
            acceptItem = new ItemStack(Material.valueOf(acceptedItemStackSplit[0]));
        }
        ItemMeta acceptedMeta = acceptItem.getItemMeta();
        acceptedMeta.setDisplayName(acceptDisplayName);
        acceptItem.setItemMeta(acceptedMeta);

        String notAcceptedItemStack = config.getString("NotAcceptedItem");
        String notAcceptedItemStackSplit[] = notAcceptedItemStack.split(":");
        if (notAcceptedItemStackSplit.length > 1) {
            notAcceptedItem = new ItemStack(Material.valueOf(notAcceptedItemStackSplit[0]), 1, Short.valueOf(notAcceptedItemStackSplit[1]));
        } else {
            notAcceptedItem = new ItemStack(Material.valueOf(notAcceptedItemStackSplit[0]));
        }
        ItemMeta notAcceptedMeta = notAcceptedItem.getItemMeta();
        notAcceptedMeta.setDisplayName(notAcceptedDisplayName);
        notAcceptedItem.setItemMeta(notAcceptedMeta);

        try {
            changeMoneyNoise = Sound.valueOf(config.getString("ChangeMoneyNoise"));
        } catch (IllegalArgumentException e) {
            changeMoneyNoise = null;
        }
        try {
            addItemNoise = Sound.valueOf(config.getString("AddItemNoise"));
        } catch (IllegalArgumentException e) {
            addItemNoise = null;
        }
        try {
            countDownNoise = Sound.valueOf(config.getString("CountDownNoise"));
        } catch (IllegalArgumentException e) {
            countDownNoise = null;
        }
        try {
            endCountDownNoise = Sound.valueOf(config.getString("EndCountDownNoise"));
        } catch (IllegalArgumentException e) {
            endCountDownNoise = null;
        }


    }

    @Override
    public void createConfig() {
        try {
            if (!instance.getDataFolder().exists()) {
                instance.getDataFolder().mkdirs();
            }
            File file = new File(instance.getDataFolder(), "guiconfig.yml");
            if (!file.exists()) {
                instance.getLogger().info("guiconfig.yml not found, creating!");
                instance.saveResource("guiconfig.yml", false);
            } else {
                instance.getLogger().info("guiconfig.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "guiconfig.yml"));
    }

    public String getStakeInventoryName() {
        return stakeInventoryName;
    }

    public ItemStack getDividerItem() {
        return dividerItem;
    }

    public boolean isMoneyEnabledInGUI() {
        return isMoneyEnabledInGUI;
    }


    public double getRightClickAmount() {
        return rightClickAmount;
    }

    public double getLeftClickAmount() {
        return leftClickAmount;
    }

    public double getShiftRightClickAmount() {
        return shiftRightClickAmount;
    }

    public double getShiftLeftClickAmount() {
        return shiftLeftClickAmount;
    }


    public ItemStack getOpponentsMoneyItem() {
        return opponentsMoneyItem;
    }

    public ItemStack getPlayersMoneyItem() {
        return playersMoneyItem;
    }


    public ItemStack getDudItem() {
        return dudItem;
    }

    public ItemStack getAcceptItem() {
        return acceptItem;
    }

    public ItemStack getNotAcceptedItem() {
        return notAcceptedItem;
    }

    public Sound getChangeMoneyNoise() {
        return changeMoneyNoise;
    }

    public Sound getAddItemNoise() {
        return addItemNoise;
    }

    public Sound getCountDownNoise() {
        return countDownNoise;
    }

    public Sound getEndCountDownNoise() {
        return endCountDownNoise;
    }
}
