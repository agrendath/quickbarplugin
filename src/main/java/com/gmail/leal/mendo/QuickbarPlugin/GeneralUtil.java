package com.gmail.leal.mendo.QuickbarPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GeneralUtil {
	
	/**
	 * Removes item drops of the given type in the chunk at the given location after the given delay
	 * @param type The type of the item drops to remove
	 * @param delay The delay in ticks after which to remove the items
	 * @param loc The location in the chunk in which to remove the items
	 * @param plugin The plugin running this task
	 */
	public static void removeItemsInChunkAfterDelay(final Material type, long delay, final Location loc, Plugin plugin)  {
		new BukkitRunnable()  {
			
			@Override
			public void run()  {
				Chunk chunk = loc.getChunk();
				Entity[] entities = chunk.getEntities();
				for(Entity ent : entities)  {
					if(ent instanceof Item)  {
						Item item = (Item) ent;
						if(item.getItemStack().getType() == type)  {
							item.remove();
						}
					}
				}
			}
		}.runTaskLaterAsynchronously(plugin, delay);
	}
	
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
     * @param pathPrefix the prefix to the path of the property including the '.', e.g. "deaths." or "applesEaten."
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
    
    /**
     * Gets the equipment slot for a certain item or null if the item is not armor or a shield
     * @param item The item to find an equipment slot for
     * @return The equipment slot that the given item corresponds to
     */
    public static EquipmentSlot getEquipmentSlot(ItemStack item)  {
    	Material t = item.getType();
    	if(t.equals(Material.SHIELD))  {
    		return EquipmentSlot.OFF_HAND;
    	}
    	else if(t.equals(Material.LEATHER_HELMET) || t.equals(Material.IRON_HELMET) || t.equals(Material.CHAINMAIL_HELMET) || t.equals(Material.GOLDEN_HELMET) || t.equals(Material.DIAMOND_HELMET) || t.equals(Material.NETHERITE_HELMET))  {
    		return EquipmentSlot.HEAD;
    	}
    	else if(t.equals(Material.LEATHER_CHESTPLATE) || t.equals(Material.IRON_CHESTPLATE) || t.equals(Material.CHAINMAIL_CHESTPLATE) || t.equals(Material.GOLDEN_CHESTPLATE) || t.equals(Material.DIAMOND_CHESTPLATE) || t.equals(Material.NETHERITE_CHESTPLATE))  {
    		return EquipmentSlot.CHEST;
    	}
    	else if(t.equals(Material.LEATHER_LEGGINGS) || t.equals(Material.IRON_LEGGINGS) || t.equals(Material.CHAINMAIL_LEGGINGS) || t.equals(Material.GOLDEN_LEGGINGS) || t.equals(Material.DIAMOND_LEGGINGS) || t.equals(Material.NETHERITE_LEGGINGS))  {
    		return EquipmentSlot.LEGS;
    	}
    	else if(t.equals(Material.LEATHER_BOOTS) || t.equals(Material.IRON_BOOTS) || t.equals(Material.CHAINMAIL_BOOTS) || t.equals(Material.GOLDEN_BOOTS) || t.equals(Material.DIAMOND_BOOTS) || t.equals(Material.NETHERITE_BOOTS))  {
    		return EquipmentSlot.FEET;
    	}
    	else  {
    		return null;
    	}
    }
}
