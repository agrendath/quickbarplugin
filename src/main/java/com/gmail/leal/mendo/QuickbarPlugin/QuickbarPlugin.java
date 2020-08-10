package com.gmail.leal.mendo.QuickbarPlugin;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.util.player.UserManager;

public class QuickbarPlugin extends JavaPlugin implements Listener{
	
	private final static List<Material> validAbsorptionTypes = new ArrayList<Material>(Arrays.asList(new Material[] {Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE, Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE, Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL, Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE, Material.BOW, Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD})); 
	private final static List<Material> validDoublexpTypes = new ArrayList<Material>(Arrays.asList(new Material[] {Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE, Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE, Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL, Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE, Material.BOW, Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD})); 
	final static String ENCHANTMENT_INDESTRUCTIBILITY = "Indestructibility";
	final static String ENCHANTMENT_ABSORPTION = "Magnetism";
	final static String ENCHANTMENT_DOUBLEXP = "Harvesting";
	
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
    			sender.sendMessage("§4You don't have permission to manipulate tiago souls");
    			return true;
    		}
    	}
    	
    	if(cmd.getName().equalsIgnoreCase("janitatop"))  {
    		if(!sender.hasPermission("quickbarplugin.janitatop"))  {
    			sender.sendMessage("§4You don't have permission to view this leaderboard");
    			return true;
    		}
    		else  {
    			List<String> leaderboard = this.getLeaderboard("deaths", "Deaths");
    			for(String message : leaderboard)  {
    				sender.sendMessage("§5" + message);
    			}
    			return true;
    		}
    	}
    	
    	if(cmd.getName().equalsIgnoreCase("janita2top"))  {
    		if(!sender.hasPermission("quickbarplugin.janita2top"))  {
    			sender.sendMessage("§4You don't have permission to view this leaderboard");
    			return true;
    		}
    		else  {
    			List<String> leaderboard = this.getLeaderboard("applesEaten", "Apples Eaten");
    			for(String message : leaderboard)  {
    				sender.sendMessage("§5" + message);
    			}
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
        			if(Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore() && isInConfig(args[0], "deaths."))  {
        				sender.sendMessage("§d" + args[0].substring(0, 1).toUpperCase() + args[0].substring(1) + " has died " + this.getConfig().getString("deaths." + Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString()) + " time(s)");
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
    		OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
    		if(p.hasPlayedBefore() && this.getConfig().isSet("applesEaten." + p.getUniqueId().toString()))  {
				sender.sendMessage("§d" + args[0].substring(0, 1).toUpperCase() + args[0].substring(1) + " has eaten " + this.getConfig().getString("applesEaten." + Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString()) + " apple(s)");
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
    			if(args[0].equalsIgnoreCase(QuickbarPlugin.ENCHANTMENT_ABSORPTION))  {
        			Material type = item.getType();
        			List<Material> validTypes = QuickbarPlugin.validAbsorptionTypes;
        			
        			if(validTypes.contains(type))  {
        				if(this.hasCustomEnchant(item, QuickbarPlugin.ENCHANTMENT_ABSORPTION))  {
            				sender.sendMessage("§4This item already has the given enchantment");
            				return true;
            			}
        				
        				// Item is of a valid type to be enchanted
        				if(this.getSouls(player) >= 1)  {
        					if(QuickbarPlugin.getPlayerExp(player) < 1500)  {
            					sender.sendMessage("§4You do not have enough xp, you need 1500 exp (you have " + QuickbarPlugin.getPlayerExp(player) + ")");
            					return true;
            				}
        					// Player has enough souls to make the enchantment
        					this.customEnchant(player.getInventory().getItemInMainHand(), QuickbarPlugin.ENCHANTMENT_ABSORPTION);
        					this.changeSouls(player, -1);
        					QuickbarPlugin.takeExp(player, 1500);
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
    			else if(args[0].equalsIgnoreCase(QuickbarPlugin.ENCHANTMENT_INDESTRUCTIBILITY))  {
    				Material type = item.getType();
    				if(!(type.equals(Material.DIAMOND_SWORD) || type.equals(Material.NETHERITE_SWORD)))  {
    					sender.sendMessage("§4This enchantment can only be applied to diamond or netherite swords");
    					return true;
    				}
    				if(this.getSouls(player) < 1)  {
    					sender.sendMessage("§4You do not have enough tiago souls to perform this enchantment (1 needed)");
    					return true;
    				}
    				if(QuickbarPlugin.getPlayerExp(player) < 1500)  {
    					sender.sendMessage("§4You do not have enough xp, you need 1500 exp (you have " + QuickbarPlugin.getPlayerExp(player) + ")");
    					return true;
    				}
    				this.customEnchant(item, QuickbarPlugin.ENCHANTMENT_INDESTRUCTIBILITY);
    				this.changeSouls(player, -1);
    				QuickbarPlugin.takeExp(player, 1500);
    				ItemMeta meta = item.getItemMeta();
    				meta.setUnbreakable(true);
    				item.setItemMeta(meta);
    				sender.sendMessage("§5Enchantment complete");
    				return true;
    			}
    			else if(args[0].equalsIgnoreCase(QuickbarPlugin.ENCHANTMENT_DOUBLEXP))  {
    				Material type = item.getType();
    				if(!QuickbarPlugin.validDoublexpTypes.contains(type))  {
    					sender.sendMessage("§4Cannot apply enchantment to this type of item");
    					return true;
    				}
    				if(this.getSouls(player) < 1)  {
    					sender.sendMessage("§4You do not have enough tiago souls to perform this enchantment (1 needed)");
    					return true;
    				}
    				if(QuickbarPlugin.getPlayerExp(player) < 2500)  {
    					sender.sendMessage("§4You do not have enough xp to perform this enchantment (2500 needed, you have " + QuickbarPlugin.getPlayerExp(player) + ")");
    					return true;
    				}
    				this.customEnchant(item, QuickbarPlugin.ENCHANTMENT_DOUBLEXP);
    				this.changeSouls(player, -1);
    				QuickbarPlugin.takeExp(player, 2500);
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
    public void onEntityKill(EntityDeathEvent e)  {
    	LivingEntity killed = e.getEntity();
    	if(killed.getKiller() != null)  {
    		Player killer = (Player) killed.getKiller();
    		ItemStack murderWeapon = killer.getInventory().getItemInMainHand();
    		Material weaponType = murderWeapon.getType();
    		if(QuickbarPlugin.validAbsorptionTypes.contains(weaponType) && this.hasCustomEnchant(murderWeapon, QuickbarPlugin.ENCHANTMENT_ABSORPTION))  {
    			Collection<ItemStack> drops = e.getDrops();
    			for(ItemStack is : drops)  {
    				this.giveItem(killer, is);
    			}
    			e.getDrops().clear();
    		}
    		if(QuickbarPlugin.validDoublexpTypes.contains(weaponType) && this.hasCustomEnchant(murderWeapon, QuickbarPlugin.ENCHANTMENT_DOUBLEXP))  {
    			e.setDroppedExp(e.getDroppedExp() * 2);  // Double the xp dropped
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
    		if(isInConfig(player.getName(), "deaths."))  { // Add death to config
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
    	if(e.getBlock().getType().equals(Material.CARVED_PUMPKIN))  {
    		boolean diamondBlocks = false;
    		boolean emeraldBlocks = false;
    		Location blockLocation = e.getBlock().getLocation();
    		int x = blockLocation.getBlockX();
    		int y = blockLocation.getBlockY();
    		int z = blockLocation.getBlockZ();
    		World world = Bukkit.getWorld("world");
    		Block topDiamond = world.getBlockAt(x, y - 1, z);
    		Block bottomDiamond = world.getBlockAt(x, y - 2, z);
    		Block emerald1X = null;
    		Block emerald2X = null;
    		boolean emeraldAlongXAxis = false;
    		if(topDiamond != null && bottomDiamond != null && topDiamond.getType().equals(Material.DIAMOND_BLOCK) && topDiamond.getType().equals(Material.DIAMOND_BLOCK))  {
    			diamondBlocks = true;
    		}
    		Block emerald1Z = world.getBlockAt(x, y - 1, z + 1);
    		Block emerald2Z = world.getBlockAt(x, y - 1, z - 1);
    		if(emerald1Z != null && emerald2Z != null && emerald1Z.getType().equals(Material.EMERALD_BLOCK) && emerald2Z.getType().equals(Material.EMERALD_BLOCK))  {
    			emeraldBlocks = true;
    		}
    		else {
    			emerald1X = world.getBlockAt(x + 1, y - 1, z);
    			emerald2X = world.getBlockAt(x - 1, y - 1, z);
    			if(emerald1X != null && emerald2X != null && emerald1X.getType().equals(Material.EMERALD_BLOCK) && emerald2X.getType().equals(Material.EMERALD_BLOCK))  {
    				emeraldBlocks = true;
    				emeraldAlongXAxis = true;
    			}
    		}
    		if(diamondBlocks && emeraldBlocks)  {
    			topDiamond.setType(Material.AIR);
    			bottomDiamond.setType(Material.AIR);
    			if(emeraldAlongXAxis)  {
    				emerald1X.setType(Material.AIR);
    				emerald2X.setType(Material.AIR);
    			}
    			else  {
    				emerald1Z.setType(Material.AIR);
    				emerald2Z.setType(Material.AIR);
    			}
    			world.createExplosion(x, y, z, 3F, false, false);
    			this.changeSouls(player, 1);
    			player.sendMessage("§6Congratulations! You have obtained a Tiago Soul by destroying his majestic statue.");
    		}
    	}
    	
    	if(QuickbarPlugin.validAbsorptionTypes.contains(item.getType()) && this.hasCustomEnchant(item, QuickbarPlugin.ENCHANTMENT_ABSORPTION))  {
    		// The item with which the block is being broken is of a valid type and contains the absorption echantment
    		boolean mcMMOEnabled = false;
    		Plugin mcmmo = Bukkit.getPluginManager().getPlugin("mcMMO");
    		mcMMO mcMMOPlugin = null;
    		if(mcmmo != null)  {
    			if(Bukkit.getPluginManager().isPluginEnabled(mcmmo))  {
    				mcMMOEnabled = true;
    				mcMMOPlugin = (mcMMO) mcmmo;
    			}
    		}
    		
    		Block block = e.getBlock();
    		Collection<ItemStack> drops = block.getDrops(item, player);  // Get a list of the drops that the block should provide
    		e.setDropItems(false);  // Prevent the block from dropping items
    		int bonusCount = 1;
    		
    		if(!mcMMOEnabled)  {
    			for(ItemStack is : drops)  {
    				this.giveItem(player, is);
    			}
    			return;
    		}
    		
    		// This code only runs if mcMMO is enabled
    		McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
			MiningManager mm = mcMMOPlayer.getMiningManager();
			mm.miningBlockCheck(block.getState());
			
			HashSet<Material> uniqueMaterials = new HashSet<>();
			boolean dontRewardTE = false; //If we suspect TEs are mixed in with other things don't reward bonus drops for anything that isn't a block
			int blockCount = 0;
			for(ItemStack itemStack : drops)  {
				//Track unique materials
				uniqueMaterials.add(itemStack.getType());
				
				// Count blocks as second failsafe
				if(itemStack.getType().isBlock())  {
					blockCount++;
				}
			}
			
			if(uniqueMaterials.size() > 1) {
	            //Too many things are dropping, assume tile entities might be duped
	            //Technically this would also prevent something like coal from being bonus dropped if you placed a TE above a coal ore when mining it but that's pretty edge case and this is a good solution for now
	            dontRewardTE = true;
	        }
			
			if(blockCount <= 1)  {
				for(ItemStack is : drops)  {
					if(is.getAmount() <= 0)  {
						this.giveItem(player, is);
						continue;
					}
					
					//If we suspect TEs might be duped only reward block
	                if(dontRewardTE) {
	                    if(!is.getType().isBlock()) {
	                        this.giveItem(player, is);
	                        continue;
	                    }
	                }
	                
	                if(mcMMO.getPlaceStore().isTrue(block.getState()))  {
	                	this.giveItem(player, is);
	                	continue;
	                }
	                
	                if (block.getMetadata(mcMMO.BONUS_DROPS_METAKEY).size() > 0) {
	                    BonusDropMeta bonusDropMeta = (BonusDropMeta) block.getMetadata(mcMMO.BONUS_DROPS_METAKEY).get(0);
	                    bonusCount = bonusDropMeta.asInt();
	                    	
	                    for (int i = 0; i < bonusCount + 1; i++) {
	                        this.giveItem(player, is);
	                    }
	                }
	                else  {
	                	this.giveItem(player, is);
	                }
				}
			}
			
			
			if(block.hasMetadata(mcMMO.BONUS_DROPS_METAKEY))
	            block.removeMetadata(mcMMO.BONUS_DROPS_METAKEY, mcMMOPlugin);
    	}
    	
    	if(QuickbarPlugin.validDoublexpTypes.contains(item.getType()) && this.hasCustomEnchant(item, QuickbarPlugin.ENCHANTMENT_DOUBLEXP))  {
    		e.setExpToDrop(e.getExpToDrop() * 2);
    	}
    }
    
    @EventHandler
    public void onPlayerFoodChange(FoodLevelChangeEvent e)  {
    	if(e.getEntity() == null || e.getItem() == null)  {
    		return;
    	}
    	HumanEntity entity = e.getEntity();
    	if(e.getItem().getType().equals(Material.APPLE) && entity instanceof Player)  {
    		Player player = (Player) entity;
    		this.addAppleCount(player.getUniqueId());
    	}
    }
    
    @EventHandler
    public void onPlayerLogIn(PlayerJoinEvent e)  {
    	Player p = e.getPlayer();
    	String pathApples = "applesEaten." + p.getUniqueId().toString();
    	String pathDeaths = "deaths." + p.getUniqueId().toString();
    	if(!this.getConfig().isSet(pathApples))  {
    		this.getConfig().set(pathApples, 0);
    	}
    	if(!this.getConfig().isSet(pathDeaths))  {
    		this.getConfig().set(pathDeaths, 0);
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
    	this.saveConfig();
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
    
    /**
     * Returns a list of strings representing the leaderboard for a certain stat that the plugin tracks
     * @param statPathPrefix The prefix to the stat's path e.g. "deaths" or "applesEaten", NOT INCLUDING THE '.'
     * @param title The title at the top of the leaderboard
     * @returna A list of strings representing the leaderboard for a certain stat that the plugin tracks
     */
    private List<String> getLeaderboard(String statPathPrefix, String title)  {
    	List<String> result = new ArrayList<String>();
    	result.add("---------- " + title + " Leaderboard ----------");
    	
    	if(this.getConfig().getConfigurationSection(statPathPrefix) == null || this.getConfig().getConfigurationSection(statPathPrefix).getKeys(false) == null)  {
    		result.add("No players on the leaderboard yet");
    		return result;
    	}
    	
    	Set<String> keys = this.getConfig().getConfigurationSection(statPathPrefix).getKeys(false);
    	String[] sortedUuids = new String[keys.size()];
    	int i = 0;
    	for(String uuid : keys)  {
    		sortedUuids[i] = uuid;
    		int j = i - 1;
    		int k = i;
    		while(j >= 0 && this.getConfig().getInt(statPathPrefix + "." + sortedUuids[k]) > this.getConfig().getInt(statPathPrefix + "." + sortedUuids[j]))  {
    			QuickbarPlugin.swapInStringList(sortedUuids, k, j);
    			k--;
    			j--;
    		}
    		i++;
    	}
    	
    	for(int x = 0; x < sortedUuids.length; x++)  {
    		if(x > 9)  {
    			break;
    		}
    		result.add((x + 1) + ". " + Bukkit.getOfflinePlayer(UUID.fromString(sortedUuids[x])).getName() + ": " + this.getConfig().getString(statPathPrefix + "." + sortedUuids[x]));
    	}
    	
    	return result;
    }
    
    private static void swapInStringList(String[] list, int i, int j)  {
    	String temp = list[j];
    	list[j] = list[i];
    	list[i] = temp;
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
    
    /**
     * Checks if the player is in the config file for the certain property
     * @param player The player
     * @param pathPrefix the prefix to the path of the proprty including the '.', e.g. "deaths." or "applesEaten."
     * @return true if the player has a value set for this property in the config file, false otherwise
     */
    private boolean isInConfig(String player, String pathPrefix)  {
    	if(this.getConfig().isSet(pathPrefix + Bukkit.getOfflinePlayer(player).getUniqueId()))  {
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


