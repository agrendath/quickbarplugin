package com.gmail.leal.mendo.QuickbarPlugin;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class QuickbarPlugin extends JavaPlugin implements Listener{
	
	final static List<Material> validAbsorptionTypes = new ArrayList<Material>(Arrays.asList(new Material[] {Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE, Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE, Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL, Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE})); 
	final static String enchantmentIndestructibility = "Indestructibility";
	final static String enchantmentAbsorption = "Absorption";
	
	@Override
    public void onEnable() {
        // TODO Insert logic to be performed when the plugin is enabled
		getLogger().info("Launching QuickbarPlugin...");
		Bukkit.getPluginManager().registerEvents(this,  this);
		this.saveDefaultConfig(); // Create config file if it doesn't exist already
		
		reloadConfig();
		
		// in case of /reload used and storage about players in a hashmap or PlayerJoinEvent
		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
		//    playerList.put(player.getName(), playerData(player));
		//}
    }
    
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    	getLogger().info("Disabling QuickbarPlugin...");
    }
    
    
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	// the /souls command that shows a player's tiago souls
    	if(cmd.getName().equalsIgnoreCase("souls"))  {
    		if(sender.hasPermission("quickbarplugin.souls") && sender instanceof Player)  {
    			Player player = Bukkit.getPlayer(sender.getName());
    			if(args.length == 0)  {
        			if(player != null)  {
        				sender.sendMessage("§5Tiago Soul Balance: " + this.getSouls(player));
        				return true;
        			}
        			else  {
        				sender.sendMessage("§4Invalid command use, you are not a player.");
        				return true;
        			}
        		}
        		else if(args.length == 3 && args[0].equalsIgnoreCase("give"))  {
        			Player receiver = Bukkit.getPlayer(args[1]);
        			try  {
        				int amount = Integer.parseInt(args[2]);
        				if(amount > 0 && amount <= this.getSouls(player))  {
        					if(receiver != null)  {
        						this.changeSouls(player, amount*-1);
        						this.changeSouls(receiver, amount);
        						receiver.sendMessage("§5You received " + amount + " tiago soul(s) from " + player.getName());
        						sender.sendMessage("§5Transferred " + amount + " tiago soul(s) to " + args[1]);
        						return true;
        					}
        					else  {
        						if(Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore())  {
        							this.changeSouls(player, amount*-1);
                					this.changeSoulsFromUUID(Bukkit.getOfflinePlayer(args[1]).getUniqueId(), amount);
                					sender.sendMessage("§5Transferred " + amount + " tiago soul(s) to " + args[1]);
                					return true;
                				}
                				else  {
                					sender.sendMessage("Cannot find player " + args[1]);
                    				return false;
                				}
        					}
        				}
        				else  {
    						sender.sendMessage("Invalid amount or you do not have enough souls to complete this transaction");
    						return false;
    					}
        			}
        			catch(NumberFormatException nfe)  {
    					sender.sendMessage(args[2] + " is not a valid amount");
    					return false;
    				}
        		}
        		else   {
        			return false;
        		}
        	}
    		else  {
    			sender.sendMessage("You don't have permission to manipulate tiago souls");
    			return true;
    		}
    	}
    	
    	// The /janita command that shows a player's death count
    	if(cmd.getName().equalsIgnoreCase("janita"))  {
    		
    		// The /janita command that returns a player's amount of deaths
    		if(sender.hasPermission("quickbarplugin.janita"))  {
    			if(args.length != 1)  {
        			return false;
        		}
        		else {
        			if(Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore() && isInConfig(args[0]))  {
        				sender.sendMessage("§d" + args[0].substring(0, 1).toUpperCase() + args[0].substring(1) + " has died " + this.getConfig().getString("deaths." + Bukkit.getOfflinePlayer(args[0]).getUniqueId()) + " time(s)");
        			}
        			else  {
        				sender.sendMessage("§4Couldn't find player or player has never died");
        			}
        			return true;
        		}
    		}
    		else  {
    			sender.sendMessage("You don't have permission to use the /janita command");
    			return true;
    		}
    	}
    	
    	// The /janita2 command that shows a player's amount of apples eaten
    	if(cmd.getName().equalsIgnoreCase("janita2"))  {
    		if(!sender.hasPermission("quickbarplugin.janita2"))  {
    			sender.sendMessage("§4You do not have permission for this");
    			return true;
    		}
    		if(args.length != 1)  {
    			sender.sendMessage("§4Invalid amount of arguments");
    			return false;
    		}
    		if(!(sender instanceof Player))  {
    			sender.sendMessage("§4Only players can use this command");
    			return true;
    		}
    		if(Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore() && this.getConfig().isSet("applesEaten." + ((Player)sender).getUniqueId().toString()))  {
				sender.sendMessage("§d" + args[0].substring(0, 1).toUpperCase() + args[0].substring(1) + " has eaten " + this.getConfig().getString("applesEaten." + Bukkit.getOfflinePlayer(args[0]).getUniqueId()) + " apple(s)");
				return true;
    		}
			else  {
				sender.sendMessage("§4Couldn't find player or player has never eaten an apple");
				return true;
			}
    		
    	}
    	
    	// The /tiago command that just returns gay in pink
    	if(cmd.getName().equalsIgnoreCase("tiago"))  {
    		if(sender.hasPermission("quickbarplugin.tiago"))  {
    			// display message "gay"
    			sender.sendMessage("§dgay");
    		}
    		else  {
    			// inform the user that they don't have the permission to use this command
    			sender.sendMessage("§4You don't have permission to use this command.");
    		}
    		return true;
    	}
    	
    	// The /lucas command that just returns "-> is useless" in pink
    	if(cmd.getName().equalsIgnoreCase("lucas"))  {
    		if(sender.hasPermission("quickbarplugin.lucas"))  {
    			// display message "gay"
    			sender.sendMessage("§d-> is useless");
    		}
    		else  {
    			// inform the user that they don't have the permission to use this command
    			sender.sendMessage("§4You don't have permission to use this command.");
    		}
    		return true;
    	} 
    	
    	// The /emma command to give the quickbar switching tool
    	if(cmd.getName().equalsIgnoreCase("emma"))  {
    		if(sender.hasPermission("quickbarplugin.emma") && sender instanceof Player)  {
    			Player player = (Player) sender;
    			// give the quickbar switching tool
    			PlayerInventory inventory = player.getInventory();
    			inventory.addItem(qbs());
    			return true;
    		}
    		else  {
    			sender.sendMessage("§4You are not allowed to do this.");
    			return true;
    		}
    	}
    	
    	// The /emma2 command to convert xp to xp bottles
    	if(cmd.getName().equalsIgnoreCase("emma2"))  {
    		if(sender.hasPermission("quickbarplugin.emma2") && sender instanceof Player)  {
    			
    			// Check if the command has enough arguments
    			if(args.length != 1)  {
    				return false;
    			}
    			else  {
    				if(isInteger(args[0]))  {
    					int amount = Integer.parseInt(args[0]);
    					int xpAmount = amount*100;
    					Player p = (Player) sender;
    					//Inventory inv = p.getInventory();
    					
    					// Check if the player has enough xp to get the required amount of bottles
    					if(xpAmount > getPlayerExp(p))  {
    						// Get maximum amount of bottles you can get
    						xpAmount = getPlayerExp(p);
    						amount = (int) Math.floor(xpAmount/100);
    					}
    					int extraAmount = xpAmount%100;
    					
    					
    					if(amount > 64)  {
							int stacks = (int) Math.floor(amount/64);
							int remainder = amount%64;
							for(int n = 0; n < stacks; n++)  {
								giveItem(p, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
							}
							giveItem(p, new ItemStack(Material.EXPERIENCE_BOTTLE, remainder));
						}
						else  {
							giveItem(p, new ItemStack(Material.EXPERIENCE_BOTTLE, amount));
						}
    					
    					takeExp(p, xpAmount);
    					takeExp(p, -extraAmount);
    					return true;
    				}
    			}
    			
    		}
    		else  {
    			sender.sendMessage("§4You are not allowed to do this.");
    			return true;
    		}
    	}
    	
    	if(cmd.getName().equalsIgnoreCase("soulenchant"))  {
    		if(sender.hasPermission("quickbarplugin.soulenchant") && sender instanceof Player)  {
    			if(args.length != 1)  {
    				sender.sendMessage("§4Invalid arguments");
    				return false;
    			}
    			
    			Player player = (Player)sender;
    			ItemStack item = player.getInventory().getItemInMainHand();
    			if(args[0].equalsIgnoreCase(QuickbarPlugin.enchantmentAbsorption))  {
        			Material type = item.getType();
        			List<Material> validTypes = QuickbarPlugin.validAbsorptionTypes;
        			
        			if(validTypes.contains(type))  {
        				if(this.hasCustomEnchant(item, QuickbarPlugin.enchantmentAbsorption))  {
            				sender.sendMessage("§4This item already has the given enchantment");
            				return true;
            			}
        				
        				// Item is of a valid type to be enchanted
        				if(this.getSouls(player) >= 1)  {
        					if(player.getLevel() < 30)  {
            					sender.sendMessage("§4You do not have enough xp, you need 1395 exp (= the first 30 levels)");
            					return true;
            				}
        					// Player has enough souls to make the enchantment
        					this.customEnchant(player.getInventory().getItemInMainHand(), QuickbarPlugin.enchantmentAbsorption);
        					this.changeSouls(player, -1);
        					QuickbarPlugin.takeExp(player, 1395);
        					player.sendMessage("§5Enchantment Complete");
        				}
        				else  {
        					player.sendMessage("§4You need 1 soul to enchant this item, unfortunately you are soulless");
        				}
        				return true;
        			}
        			else  {
        				sender.sendMessage("§4The given enchantment cannot be applied to this item");
        				return true;
        			}
    			}
    			else if(args[0].equalsIgnoreCase(QuickbarPlugin.enchantmentIndestructibility))  {
    				Material type = item.getType();
    				if(!(type.equals(Material.DIAMOND_SWORD) || type.equals(Material.NETHERITE_SWORD)))  {
    					sender.sendMessage("§4This enchantment can only be applied to diamond or netherite swords");
    					return true;
    				}
    				if(this.getSouls(player) < 1)  {
    					sender.sendMessage("§4You do not have enough tiago souls to perform this enchantment (1 needed)");
    					return true;
    				}
    				if(player.getLevel() < 30)  {
    					sender.sendMessage("§4You do not have enough xp, you need 1395 exp (= the first 30 levels)");
    					return true;
    				}
    				this.customEnchant(item, QuickbarPlugin.enchantmentIndestructibility);
    				this.changeSouls(player, -1);
    				QuickbarPlugin.takeExp(player, 1395);
    				ItemMeta meta = item.getItemMeta();
    				meta.setUnbreakable(true);
    				item.setItemMeta(meta);
    				sender.sendMessage("§5Enchantment complete");
    				return true;
    			}
    			else  {
    				sender.sendMessage("§4Invalid enchantment");
    				return true;
    			}
    		}
    		else  {
    			sender.sendMessage("§4You are not allowed to do this.");
    			return true;
    		}
    	}
    	
    	// If this has happened the function will return true. 
        // If this hasn't happened the value of false will be returned.
    	return false; 
    }
    
    @EventHandler
    public void onKill(PlayerDeathEvent e)  {
    	Player killed = e.getEntity();
    	Player killer = killed.getKiller();
    	if(killed.getUniqueId().toString().equals("b2a75ec7-c556-4f47-8b61-bfb1780b4ac5")) {  // killing tiago
    		killer.sendMessage("§6Congratulations! You have obtained a §5Tiago Soul");
    		this.changeSouls(killer, 1);
    	}
    	else if(killed.getUniqueId().toString().equals("df736569-ffed-40e7-9c92-074661b86b09"))  {  // killing lucas (10% chance of tiago soul)
    		int random = (int) (Math.random() * 10 + 1);  // random int in interval [0, 9] (inclusive)
    		if(random == 0)  {
    			killer.sendMessage("§6Congratulations! You have obtained a §5Tiago Soul");
    			this.changeSouls(killer, 1);
    		}
    	}
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)  {
    	//getLogger().info("PlayerInteract even triggered...");
    	if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))  {
    		Player player = e.getPlayer();
    		if(e.getItem() != null)  {
    			if(e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Quickbar Switcher"))  {
        			// Switch Quickbar
        			switchItems(player);
        			
        		}
    		}
    	}
    }
    
    @EventHandler
    public void onExpBottle(ExpBottleEvent e)  {
    	e.setExperience(100);
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent e)  {
    	Player player = (Player) e.getEntity();
    	if(player instanceof Player)  {
    		if(isInConfig(player.getName()))  { // Add death to config
    			this.getConfig().set("deaths." + player.getUniqueId(), this.getConfig().getInt("deaths." + player.getUniqueId()) + 1);
    			saveConfig();
    		}
    		else  { // Create new entry for player, then add death to config
    			this.getConfig().set("deaths." + player.getUniqueId(), 1);
    			saveConfig();
    		}
    	}
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)  {
    	Player player = e.getPlayer();
    	ItemStack item = player.getInventory().getItemInMainHand();
    	if(QuickbarPlugin.validAbsorptionTypes.contains(item.getType()) && item.getItemMeta() != null && item.getItemMeta().hasLore() && item.getItemMeta().getLore().contains(QuickbarPlugin.enchantmentAbsorption))  {
    		// The item with which the block is being broken is of a valid type and contains the absorption echantment
    		Block block = e.getBlock();
    		Collection<ItemStack> drops = block.getDrops(item, player);  // Get a list of the drops that the block should provide
    		e.setDropItems(false);  // Prevent the block from dropping items
    		for(ItemStack i : drops)  {
    			this.giveItem(player, i);  // Give all the drops straight to the player's inventory
    		}
    	}
    }
    
    @EventHandler
    public void onPlayerFoodChange(FoodLevelChangeEvent e)  {
    	HumanEntity entity = e.getEntity();
    	if(e.getItem().getType().equals(Material.APPLE) && entity instanceof Player)  {
    		Player player = (Player) entity;
    		this.addAppleCount(player.getUniqueId());
    	}
    }
    
    /**
     * @pre The given enchantment is valid for the given item, must be checked beforehand
     */
    private void customEnchant(ItemStack item, String enchantment) {
    	ItemMeta meta = item.getItemMeta();
    	if(this.hasCustomEnchant(item, enchantment) || meta == null)  {
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
    
    private void addAppleCount(UUID playerId)  {
    	String path = "applesEaten." + playerId.toString();
    	if(this.getConfig().isSet(path))  {
    		this.getConfig().set(path, this.getConfig().getInt(path) + 1);
    	}
    	else  {
    		this.getConfig().set(path, 1);
    	}
    }
    
    private boolean hasCustomEnchant(ItemStack item, String enchantment)  {
    	ItemMeta meta = item.getItemMeta();
    	List<String> lore = meta.getLore();
    	if(meta != null && lore != null && lore.contains(enchantment))  {
    		return true;
    	}
    	else  {
    		return false;
    	}
    }
    
    private int getSouls(Player player)  {
    	if(this.soulsRegisteredInConfig(player))  {
    		return this.getConfig().getInt("souls." + player.getUniqueId());
    	}
    	else  {
    		return 0;
    	}
    }
    
    /**
     * Will change a player's souls by [change]
     * @pre If the change is negative, it must not be smaller or equal to the amount of souls the player already has
     * 		| (change < 0) ? (change*-1 <= this.getSouls(player)) : true
     */
    private void changeSouls(Player player, int change)  {
    	String path = "souls." + player.getUniqueId();
    	if(this.soulsRegisteredInConfig(player))  {
    		this.getConfig().set(path, this.getConfig().getInt(path) + change);
    		saveConfig();
    	}
    	else  {
    		if(change > 0)  {
    			this.getConfig().set(path, change);
    			saveConfig();
    		}
    	}
    }
    
    private void changeSoulsFromUUID(UUID uuid, int change)  {
    	String id = uuid.toString();
    	String path = "souls." + id;
    	boolean registered = false;
    	if(this.getConfig().isSet("souls." + id))  {
    		registered = true;
    	}
    	if(registered)  {
    		this.getConfig().set(path, this.getConfig().getInt(path) + change);
    		saveConfig();
    	}
    	else  {
    		if(change > 0)  {
    			this.getConfig().set(path, change);
    			saveConfig();
    		}
    	}
    }
    
    public void giveItem(Player p, ItemStack item)  {
    	Inventory inv = p.getInventory();
    	if(inv.firstEmpty() == -1)  {
    		p.getWorld().dropItem(p.getLocation(), item);
    	}
    	else  {
    		inv.addItem(item);
    	}
    }
    
    
    
    // Calculate amount of EXP needed to level up
    public static int getExpToLevelUp(int level)  {
    	if(level <= 15)  {
    		return 2*level+7;
    	} else if(level <= 30)  {
    		return 5*level-38;
    	}  else  {
    		return 9*level-158;
    	}
    }
    
    // Calculate total experience up to a level
    public static int getExpAtLevel(int level)  {
    	if(level <= 16)  {
    		return (int) (Math.pow(level, 2) + 6*level);
    	}  else if(level <= 31)  {
    		return (int) (2.5*Math.pow(level, 2) - 40.5*level + 360.0);
    	} else  {
    		return (int) (4.5*Math.pow(level, 2) - 162.5*level + 2200.0);
    	}
    }
    
    // Calculate the player's current EXP amount
    public static int getPlayerExp(Player player)  {
    	int exp = 0;
    	int level = player.getLevel();
    	
    	// Get amount of XP in past levels
    	exp += getExpAtLevel(level);
    	
    	// Get amount of XP towards next level
    	exp += Math.round(getExpToLevelUp(level) * player.getExp());
    	
    	return exp;
    }
    
    // Take from player's exp
    public static int takeExp(Player player, int exp)  {
    	// Get player's current exp
    	int currentExp = getPlayerExp(player);
    	
    	// Reset player's current exp to 0
    	player.setExp(0);
    	player.setLevel(0);
    	
    	// Give the player their exp back, with the difference
    	int newExp = currentExp - exp;
    	player.giveExp(newExp);
    	
    	// Return the player's new exp amount
    	return newExp;
    }
    
    
    
    public boolean isInteger(String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public void switchItems(Player player)  {
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
    
    private boolean isInConfig(String player)  {
    	if(this.getConfig().isSet("deaths." + Bukkit.getOfflinePlayer(player).getUniqueId()))  {
    		return true;
    	}
    	else  {
    		return false;
    	}
    }
    
    /**
     * Checks if this player has a 'tiago souls' count registered in the config file
     */
    private boolean soulsRegisteredInConfig(Player player)  {
    	if(this.getConfig().isSet("souls." + player.getUniqueId()))  {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    private ItemStack qbs()  {
    	ItemStack item = new ItemStack(Material.STONE_HOE, 1);
    	ItemMeta meta = item.getItemMeta();
    	meta.setDisplayName("Quickbar Switcher");
    	item.setItemMeta(meta);
    	return item;
    }
}
