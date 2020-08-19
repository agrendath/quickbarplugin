package com.gmail.leal.mendo.QuickbarPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class SoulEnchantments {
	
	public final static List<Material> validAbsorptionTypes = new ArrayList<Material>(Arrays.asList(new Material[] {Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE, Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE, Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL, Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE, Material.BOW, Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD})); 
	public final static List<Material> validDoublexpTypes = new ArrayList<Material>(Arrays.asList(new Material[] {Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE, Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE, Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL, Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE, Material.BOW, Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD})); 
	public final static List<Material> validVampirismTypes = new ArrayList<Material>(Arrays.asList(new Material[] {Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD, Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE, Material.BOW}));
	public final static List<Material> validIndestructibilityTypes = new ArrayList<Material>(Arrays.asList(new Material[] {Material.DIAMOND_SWORD, Material.NETHERITE_SWORD}));
	public final static List<Material> validMovespeedTypes = new ArrayList<Material>(Arrays.asList(new Material[] {Material.LEATHER_BOOTS, Material.IRON_BOOTS, Material.CHAINMAIL_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS}));
	public final static List<Material> validThunderlordTypes = new ArrayList<Material>(Arrays.asList(new Material[] {Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD, Material.BOW}));
	public final static List<Material> validToughnessTypes = new ArrayList<Material>(Arrays.asList(new Material[] {Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET}));
	public final static String ENCHANTMENT_INDESTRUCTIBILITY = "Indestructibility";
	public final static String ENCHANTMENT_ABSORPTION = "Magnetism";
	public final static String ENCHANTMENT_DOUBLEXP = "Harvesting";
	public final static String ENCHANTMENT_VAMPIRISM = "Vampirism";
	public final static String ENCHANTMENT_MOVESPEED = "Swiftness";
	public final static String ENCHANTMENT_THUNDERLORD = "Thunderlord";
	public final static String ENCHANTMENT_TOUGHNESS = "Toughness";
	
	public static boolean soulEnchant(String enchantment, Player player, Plugin quickbarPlugin)  {
		ItemStack item = player.getInventory().getItemInMainHand();
		
		List<Material> validTypes = null;
		int soulCost = 0;
		int xpCost = 0;
		
		if(enchantment.equalsIgnoreCase(ENCHANTMENT_ABSORPTION))  {
			validTypes = validAbsorptionTypes;
			enchantment = ENCHANTMENT_ABSORPTION;
			soulCost = 1;
			xpCost = 2000;
		}
		else if(enchantment.equalsIgnoreCase(ENCHANTMENT_DOUBLEXP))  {
			validTypes = validDoublexpTypes;
			enchantment = ENCHANTMENT_DOUBLEXP;
			soulCost = 1;
			xpCost = 3000;
		}
		else if(enchantment.equalsIgnoreCase(ENCHANTMENT_INDESTRUCTIBILITY))  {
			validTypes = validIndestructibilityTypes;
			enchantment = ENCHANTMENT_INDESTRUCTIBILITY;
			soulCost = 1;
			xpCost = 2500;
		}
		else if(enchantment.equalsIgnoreCase(ENCHANTMENT_VAMPIRISM))  {
			validTypes = validVampirismTypes;
			enchantment = ENCHANTMENT_VAMPIRISM;
			soulCost = 1;
			xpCost = 4000;
		}
		else if(enchantment.equalsIgnoreCase(ENCHANTMENT_MOVESPEED))  {
			validTypes = validMovespeedTypes;
			enchantment = ENCHANTMENT_MOVESPEED;
			soulCost = 1;
			xpCost = 4000;
		}
		else if(enchantment.equalsIgnoreCase(ENCHANTMENT_THUNDERLORD))  {
			validTypes = validThunderlordTypes;
			enchantment = ENCHANTMENT_THUNDERLORD;
			soulCost = 1;
			xpCost = 3500;
		}
		else if(enchantment.equalsIgnoreCase(ENCHANTMENT_TOUGHNESS))  {
			validTypes = validToughnessTypes;
			enchantment = ENCHANTMENT_TOUGHNESS;
			soulCost = 1;
			xpCost = 4000;
		}
		else if(enchantment.equalsIgnoreCase("looting"))  {
			// Special looting for bows soul enchantment
			validTypes = new ArrayList<Material>();
			validTypes.add(Material.BOW);
			enchantment = "Looting";
			soulCost = 0;
			xpCost = 2000;
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
		if(enchantment.equalsIgnoreCase("looting") && item.containsEnchantment(Enchantment.LOOT_BONUS_MOBS))  {
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
			if(!enchantment.equalsIgnoreCase("looting"))  {
				customEnchant(player.getInventory().getItemInMainHand(), enchantment);
				changeSouls(player, -soulCost, quickbarPlugin);
			}
			XPUtil.takeExp(player, xpCost);
			
			// Enchantments that change item meta/or attributes
			if(enchantment.equalsIgnoreCase(ENCHANTMENT_INDESTRUCTIBILITY))  {
				ItemMeta meta = item.getItemMeta();
				meta.setUnbreakable(true);
				item.setItemMeta(meta);
			}
			else if(enchantment.equalsIgnoreCase(ENCHANTMENT_MOVESPEED))  {
				ItemMeta meta = item.getItemMeta();
				meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "Extra Speed", 0.2, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlot.FEET));
				item.setItemMeta(meta);
			}
			else if(enchantment.equalsIgnoreCase("looting"))  {
				item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);
			}
			else if(enchantment.equalsIgnoreCase(ENCHANTMENT_TOUGHNESS))  {
				ItemMeta meta = item.getItemMeta();
				meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "Extra Health", 0.2, AttributeModifier.Operation.ADD_SCALAR, GeneralUtil.getEquipmentSlot(item)));
				item.setItemMeta(meta);
			}
			
			player.sendMessage("§5Enchantment Complete");
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
					sender.sendMessage("§4Cannot find player " + receiverName);
    				return false;
				}
			}
		}
		else  {
			sender.sendMessage("§4Invalid amount or you do not have enough souls to complete this transaction");
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
    
    /**
     * Adds the thunderlord bonus damage to the original damage and returns it
     * @param damager The player who is damaging the entity
     * @param damaged The entity being damaged
     * @param originalDamage The original damage of the attack
     * @return The final damage of the attack with thunderlord bonus added
     */
    public static double getDamageWithThunderlord(double originalDamage)  {
    	double finalDamage = originalDamage;
    	finalDamage += 0.25 * originalDamage;
    	return finalDamage;
    }
}
