package com.gmail.leal.mendo.QuickbarPlugin;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExpBottleEvent;
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
    		if(sender.hasPermission("quickbarplugin.souls"))  {
    			Player player = Bukkit.getPlayer(sender.getName());
    			if(args.length == 0)  {
        			if(player != null)  {
        				sender.sendMessage("§5Tiago Soul Balance: " + this.getSouls(player));
        				return true;
        			}
        			else  {
        				sender.sendMessage("§4Invalid command use, you are not a player.");
        				return false;
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
        						receiver.sendMessage("§5You received " + amount + " tiago souls from " + player.getName());
        						sender.sendMessage("§5Transferred " + amount + " tiago souls to " + args[1]);
        						return true;
        					}
        					else  {
        						if(Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore())  {
        							this.changeSouls(player, amount*-1);
                					this.changeSoulsFromUUID(Bukkit.getOfflinePlayer(args[1]).getUniqueId(), amount);
                					sender.sendMessage("§5Transferred " + amount + " tiago souls to " + args[1]);
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
    			return false;
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
        				sender.sendMessage("§d" + args[0].substring(0, 1).toUpperCase() + args[0].substring(1) + " has died " + this.getConfig().getString("deaths." + Bukkit.getOfflinePlayer(args[0]).getUniqueId()) + " time(s).");
        			}
        			else  {
        				sender.sendMessage("§4Couldn't find player or player has never died.");
        			}
        			return true;
        		}
    		}
    		else  {
    			sender.sendMessage("You don't have permission to use the /janita command");
    			return false;
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
    			return false;
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
    			return false;
    		}
    	}
    	
    	//If this has happened the function will return true. 
        // If this hasn't happened the value of false will be returned.
    	return false; 
    }
    
    @EventHandler
    public void onKill(PlayerDeathEvent e)  {
    	Player killed = e.getEntity();
    	Player killer = killed.getKiller();
    	if(killed.getUniqueId() == UUID.fromString("b2a75ec7-c556-4f47-8b61-bfb1780b4ac5"))  {  // killing tiago
    		killer.sendMessage("§6Congratulations! You have obtained a §5Tiago Soul");
    		this.changeSouls(killer, 1);
    	}
    	else if(killed.getUniqueId() == UUID.fromString("df736569-ffed-40e7-9c92-074661b86b09"))  {  // killing lucas (10% chance of tiago soul)
    		int random = (int) (Math.random() * 10 + 1);  // random int in interval [0, 9] (inclusive)
    		if(random == 0)  {
    			killer.sendMessage("§6Congratulations! You have obtained a §5Tiago Soul");
    			this.changeSouls(killer, 1);
    		}
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
    
    public static void giveItem(Player p, ItemStack item)  {
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
