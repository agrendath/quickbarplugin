package com.gmail.leal.mendo.QuickbarPlugin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class QuickbarSwitcher {
	
	public static ItemStack getQbs()  {
    	ItemStack item = new ItemStack(Material.STONE_HOE, 1);
    	ItemMeta meta = item.getItemMeta();
    	meta.setDisplayName("Quickbar Switcher");
    	item.setItemMeta(meta);
    	return item;
    }
	
	public static void switchItems(Player player)  {
    	PlayerInventory inv = player.getInventory();
    	for(int i = 0; i < 9; i++)  {
    		if(inv.getItem(i) == null)  {
				if(inv.getItem(i + 27) == null)  {
					// Do nothing since both slots are empty
				}
				else  {
					inv.setItem(i, inv.getItem(i + 27));
					inv.setItem(i + 27, null);
				}
			}
			else  {
				if(!inv.getItem(i).getItemMeta().getDisplayName().equalsIgnoreCase("Quickbar Switcher"))  {
					if(inv.getItem(i + 27) == null)  {
    					inv.setItem(i + 27, inv.getItem(i));
    					inv.setItem(i, null);
    				}
    				else {
    					ItemStack tempItem1 = inv.getItem(i);
    					inv.setItem(i, inv.getItem(i + 27));
    					inv.setItem(i + 27, tempItem1);
    				}
				}
			}
    	}
    }
}
