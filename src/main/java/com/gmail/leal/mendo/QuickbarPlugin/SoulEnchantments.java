package com.gmail.leal.mendo.QuickbarPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class SoulEnchantments {
	
	public static boolean soulEnchant(String enchantment, Player player, Plugin quickbarPlugin)  {
		ItemStack item = player.getInventory().getItemInMainHand();
		
		List<Material> validTypes = null;
		int soulCost = 0;
		int xpCost = 0;
		
		if(enchantment.equalsIgnoreCase(QuickbarPlugin.ENCHANTMENT_ABSORPTION))  {
			validTypes = QuickbarPlugin.validAbsorptionTypes;
			enchantment = QuickbarPlugin.ENCHANTMENT_ABSORPTION;
			soulCost = 1;
			xpCost = 1500;
		}
		else if(enchantment.equalsIgnoreCase(QuickbarPlugin.ENCHANTMENT_DOUBLEXP))  {
			validTypes = QuickbarPlugin.validDoublexpTypes;
			enchantment = QuickbarPlugin.ENCHANTMENT_DOUBLEXP;
			soulCost = 1;
			xpCost = 2500;
		}
		else if(enchantment.equalsIgnoreCase(QuickbarPlugin.ENCHANTMENT_INDESTRUCTIBILITY))  {
			validTypes = QuickbarPlugin.validIndestructibilityTypes;
			enchantment = QuickbarPlugin.ENCHANTMENT_INDESTRUCTIBILITY;
			soulCost = 1;
			xpCost = 1500;
		}
		else if(enchantment.equalsIgnoreCase(QuickbarPlugin.ENCHANTMENT_VAMPIRISM))  {
			validTypes = QuickbarPlugin.validVampirismTypes;
			enchantment = QuickbarPlugin.ENCHANTMENT_VAMPIRISM;
			soulCost = 1;
			xpCost = 3000;
		}
		else  {
			player.sendMessage("§4Invalid Enchantment");
			return true;
		}
		
		Material type = item.getType();
		
		if(validTypes == null || !validTypes.contains(type))  {
			player.sendMessage("§4The item is not of a valid type");
			return true;
		}
		
		if(hasCustomEnchant(item, enchantment))  {
			player.sendMessage("§4This item already has the given enchantment");
			return true;
		}
		
		// Item is of a valid type to be enchanted
		if(SoulEnchantments.getSouls(player, quickbarPlugin.getConfig()) >= soulCost)  {
			if(XPUtil.getPlayerExp(player) < xpCost)  {
				player.sendMessage("§4You do not have enough xp, you need " + xpCost + " exp (you have " + XPUtil.getPlayerExp(player) + ")");
				return true;
			}
			
			// Player has enough souls to make the enchantment
			customEnchant(player.getInventory().getItemInMainHand(), enchantment);
			changeSouls(player, -soulCost, quickbarPlugin);
			XPUtil.takeExp(player, xpCost);
			player.sendMessage("§5Enchantment Complete");
			
			// Indestructibility enchantment needs to change the item meta so this is a special case
			if(enchantment.equalsIgnoreCase(QuickbarPlugin.ENCHANTMENT_INDESTRUCTIBILITY))  {
				ItemMeta meta = item.getItemMeta();
				meta.setUnbreakable(true);
				item.setItemMeta(meta);
			}
		}
		else  {
			player.sendMessage("§4You need " + soulCost + " soul(s) to enchant this item, unfortunately you do not have enough");
		}
		return true;
	}
	
	public static boolean soulTransfer(int amount, Player sender, Player receiver, String receiverName, Plugin quickbarPlugin)  {
		if(amount > 0 && amount <= SoulEnchantments.getSouls(sender, quickbarPlugin.getConfig()))  {
			if(receiver != null)  {
				SoulEnchantments.changeSouls(sender, amount*-1, quickbarPlugin);
				SoulEnchantments.changeSouls(receiver, amount, quickbarPlugin);
				receiver.sendMessage("§5You received " + amount + " tiago soul(s) from " + sender.getName());
				sender.sendMessage("§5Transferred " + amount + " tiago soul(s) to " + receiverName);
				return true;
			}
			else  {
				if(Bukkit.getOfflinePlayer(receiverName).hasPlayedBefore())  {
					SoulEnchantments.changeSouls(sender, amount*-1, quickbarPlugin);
					SoulEnchantments.changeSoulsFromUUID(Bukkit.getOfflinePlayer(receiverName).getUniqueId(), amount, quickbarPlugin);
					sender.sendMessage("§5Transferred " + amount + " tiago soul(s) to " + receiverName);
					return true;
				}
				else  {
					sender.sendMessage("Cannot find player " + receiverName);
    				return false;
				}
			}
		}
		else  {
			sender.sendMessage("Invalid amount or you do not have enough souls to complete this transaction");
			return false;
		}
	}
	
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
