package me.jet315.staking.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

public class Utils {


    /**
     * Parses an ItemStack from a String, a valid format being:
     * "DIAMOND_SWORD:0 1 displayname:&7Fighter_&7Sword lore:&7Trusty_Sword\n&7New_line! DAMAGE_ALL:5 "
     * @return An itemstack
     */
    public static ItemStack parseItemStackFromString(String itemData){
        if(!itemData.equalsIgnoreCase("none")) {
            String itemDataSplit[] = itemData.split(" ");
            if (itemDataSplit.length > 0) {
                String itemMaterial[] = itemDataSplit[0].split(":");
                short data = 0;
                if (itemMaterial.length == 2) {
                    data = Short.valueOf(itemMaterial[1]);
                }
                Material material = Material.valueOf(itemMaterial[0]);
                if (material != null) {
                    ItemStack theItem = new ItemStack(material, 1, data);
                    if (itemDataSplit.length > 1) {
                        /**
                         * Amount of the item
                         */
                        int amount = 1;
                        try {
                            amount = Integer.parseInt(itemDataSplit[1]);
                        } catch (NumberFormatException e) {
                            System.out.println("[STAKING] INVALID AMOUNT FOR THE LINE (the amount should follow after the material)" + itemData);
                        }
                        theItem.setAmount(amount);

                        /**
                         * Reset of the meta data
                         */
                        if (itemDataSplit.length > 2) {
                            ItemMeta itemMeta = theItem.getItemMeta();
                            int loops = 0;
                            for (String metaData : itemDataSplit) {
                                loops++;
                                /**
                                 * Prevents the Material or amount being picked up
                                 */
                                if(loops <= 2){
                                    continue;
                                }

                                /**
                                 * Checks for displayname
                                 */
                                if (metaData.toLowerCase().contains("displayname:")) {
                                    String displayName = metaData.substring(12); //Removes the displayname: bit
                                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName.replaceAll("_", " ")));
                                    continue;
                                }

                                /**
                                 * Checks for lore
                                 */
                                if (metaData.toLowerCase().contains("lore:")) {
                                    String lore = ChatColor.translateAlternateColorCodes('&', metaData.substring(5)); //Removes the lore: bit
                                    ArrayList<String> actualLore = new ArrayList<>();
                                    for (String line : lore.split("|")) {
                                        actualLore.add(line.replaceAll("_", " "));
                                    }
                                    itemMeta.setLore(actualLore);
                                    continue;
                                }
                                /**
                                 * Checks for Enchant
                                 */
                                String[] possibleEnchant = metaData.split(":");
                                if (possibleEnchant.length > 0) {
                                    try {
                                        int enchantLevel = Integer.parseInt(possibleEnchant[1]);

                                        Enchantment enchant = Enchantment.getByName(possibleEnchant[0]);
                                        if (enchant != null) {
                                            itemMeta.addEnchant(enchant, enchantLevel, true);
                                        } else {
                                            System.out.println("[STAKING] Invalid enchant for item " + itemData + " (Specific enchant: " + possibleEnchant[0] + ")");
                                        }
                                        continue;
                                    } catch (NumberFormatException e) {

                                    }
                                }
                                System.out.println("[STAKING] Invalid meta data for the item " + itemData + " (Specific metaData: " + metaData + ")");
                            }
                            theItem.setItemMeta(itemMeta);
                        }


                        return theItem;
                    }

                } else {
                    System.out.println("[STAKING] INVALID KIT MATERIAL : " + itemMaterial[0]);
                }
            }
        }
        return new ItemStack(Material.AIR);
    }

    /**
     * Heals Players
     */
    public static void healPlayers(Player... p) {
        for (Player player : p) {
            player.setHealth(20);
            player.setSaturation(20);
            player.setFoodLevel(20);
            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
    }
}
