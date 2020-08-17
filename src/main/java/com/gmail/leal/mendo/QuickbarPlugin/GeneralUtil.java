package com.gmail.leal.mendo.QuickbarPlugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GeneralUtil {
	public static void giveItem(Player p, ItemStack item)  {
    	Inventory inv = p.getInventory();
    	if(inv.firstEmpty() == -1)  {
    		p.getWorld().dropItem(p.getLocation(), item);
    	}
    	else  {
    		inv.addItem(item);
    	}
    }
	
	public static void swapInStringList(String[] list, int i, int j)  {
		String temp = list[j];
		list[j] = list[i];
		list[i] = temp;
	}
	
	public static boolean isInteger(String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
	
	/**
     * Checks if the player is in the config file for the certain property
     * @param player The player
     * @param pathPrefix the prefix to the path of the proprty including the '.', e.g. "deaths." or "applesEaten."
     * @return true if the player has a value set for this property in the config file, false otherwise
     */
    public static boolean isInConfig(String player, String pathPrefix, FileConfiguration config)  {
    	if(config.isSet(pathPrefix + Bukkit.getOfflinePlayer(player).getUniqueId()))  {
    		return true;
    	}
    	else  {
    		return false;
    	}
    }
}
