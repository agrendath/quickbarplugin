package com.gmail.leal.mendo.QuickbarPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class SoulEnchantments {
	
	/**
	 * Enchants the given item with the given enchantment
	 * @param item | The item to enchant
	 * @param enchantment | The enchantment to apply to the given item
     * @pre The given enchantment is valid for the given item, must be checked beforehand
     */
    public static void customEnchant(ItemStack item, String enchantment) {
    	ItemMeta meta = item.getItemMeta();
    	if(hasCustomEnchant(item, enchantment) || meta == null)  {
    		return;
    	}
    	List<String> newLore = null;
    	if(meta.hasLore())  {
    		newLore = meta.getLore();
    	}
    	else  {
    		newLore = new ArrayList<String>();
    	}
    	newLore.add(enchantment);
    	meta.setLore(newLore);
    	item.setItemMeta(meta);
    }
	
	public static boolean hasCustomEnchant(ItemStack item, String enchantment)  {
    	ItemMeta meta = item.getItemMeta();
    	List<String> lore = meta.getLore();
    	if(meta != null && lore != null && lore.contains(enchantment))  {
    		return true;
    	}
    	else  {
    		return false;
    	}
    }
	
	public static int getSouls(Player player, FileConfiguration config)  {
    	if(soulsRegisteredInConfig(player, config))  {
    		return config.getInt("souls." + player.getUniqueId());
    	}
    	else  {
    		return 0;
    	}
    }
	
	/**
     * Will change a player's souls by [change] (can be positive or negative)
     * @param player | The player whose balance of souls is to be changed
     * @pre If the change is negative, it must not be smaller or equal to the amount of souls the player already has
     * 		| (change < 0) ? (change*-1 <= getSouls(player, quickbarPlugin.getConfig())) : true
     */
    public static void changeSouls(Player player, int change, Plugin quickbarPlugin)  {
    	String path = "souls." + player.getUniqueId();
    	if(soulsRegisteredInConfig(player, quickbarPlugin.getConfig()))  {
    		quickbarPlugin.getConfig().set(path, quickbarPlugin.getConfig().getInt(path) + change);
    		quickbarPlugin.saveConfig();
    	}
    	else  {
    		if(change > 0)  {
    			quickbarPlugin.getConfig().set(path, change);
    			quickbarPlugin.saveConfig();
    		}
    	}
    }
    
    public static void changeSoulsFromUUID(UUID uuid, int change, Plugin quickbarPlugin)  {
    	String id = uuid.toString();
    	String path = "souls." + id;
    	boolean registered = false;
    	if(quickbarPlugin.getConfig().isSet("souls." + id))  {
    		registered = true;
    	}
    	if(registered)  {
    		quickbarPlugin.getConfig().set(path, quickbarPlugin.getConfig().getInt(path) + change);
    		quickbarPlugin.saveConfig();
    	}
    	else  {
    		if(change > 0)  {
    			quickbarPlugin.getConfig().set(path, change);
    			quickbarPlugin.saveConfig();
    		}
    	}
    }
    
    /**
     * Checks if this player has a 'tiago souls' count registered in the config file
     */
    public static boolean soulsRegisteredInConfig(Player player, FileConfiguration config)  {
    	if(config.isSet("souls." + player.getUniqueId()))  {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
}
