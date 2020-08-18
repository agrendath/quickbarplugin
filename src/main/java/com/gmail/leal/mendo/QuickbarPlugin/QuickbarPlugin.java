package com.gmail.leal.mendo.QuickbarPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.util.player.UserManager;

public class QuickbarPlugin extends JavaPlugin implements Listener{
	
	@Override
    public void onEnable() {
        // TODO Insert logic to be performed when the plugin is enabled
		getLogger().info("Launching QuickbarPlugin...");
		Bukkit.getPluginManager().registerEvents(new Listeners(this),  this);
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
        				sender.sendMessage("§5Tiago Soul Balance: " + SoulEnchantments.getSouls(player, this.getConfig()));
        			}
        			else  {
        				sender.sendMessage("§4Invalid command use, you are not a player.");
        			}
        			return true;
        		}
        		else if(args.length == 3 && args[0].equalsIgnoreCase("give"))  {
        			Player receiver = Bukkit.getPlayer(args[1]);
        			try  {
        				int amount = Integer.parseInt(args[2]);
        				return SoulEnchantments.soulTransfer(amount, player, receiver, args[1], this);
        			}
        			catch(NumberFormatException nfe)  {
    					sender.sendMessage("§4" + args[2] + " is not a valid amount");
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
    		}
    		else  {
				List<String> leaderboard = Trackers.getLeaderboard("deaths", "Deaths", this.getConfig());
				for(String message : leaderboard)  {
					sender.sendMessage("§5" + message);
				}
			}
			return true;
    	}
    	
    	if(cmd.getName().equalsIgnoreCase("janita2top"))  {
    		if(!sender.hasPermission("quickbarplugin.janita2top"))  {
    			sender.sendMessage("§4You don't have permission to view this leaderboard");
    			return true;
    		}
    		else  {
    			List<String> leaderboard = Trackers.getLeaderboard("applesEaten", "Apples Eaten", this.getConfig());
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
        			if(Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore() && GeneralUtil.isInConfig(args[0], "deaths.", this.getConfig()))  {
        				sender.sendMessage("§d" + args[0].substring(0, 1).toUpperCase() + args[0].substring(1) + " has died " + this.getConfig().getString("deaths." + Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString()) + " time(s)");
        			}
        			else  {
        				sender.sendMessage("§4Couldn't find player or player has never died");
        			}
        			return true;
        		}
    		}
    		else  {
    			sender.sendMessage("§4You don't have permission to use the /janita command");
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
    			inventory.addItem(QuickbarSwitcher.getQbs());
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
    				if(GeneralUtil.isInteger(args[0]))  {
    					int amount = Integer.parseInt(args[0]);
    					XPUtil.convertToBottles((Player) sender, amount);
    					return true;
    				}
    				else  {
    					sender.sendMessage("§4Invalid amount of xp bottles");
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
    			else  {
    				return SoulEnchantments.soulEnchant(args[0], (Player) sender, this);
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
}


