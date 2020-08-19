package com.gmail.leal.mendo.QuickbarPlugin;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.util.player.UserManager;

public class Listeners implements Listener{
	
	Plugin quickbarPlugin;
	
	public Listeners(Plugin plugin)  {
		this.quickbarPlugin = plugin;
	}
	
	@EventHandler
    public void onKill(PlayerDeathEvent e)  {
    	Player killed = e.getEntity();
    	Player killer = killed.getKiller();
    	if(killed.getUniqueId().toString().equalsIgnoreCase("b2a75ec7-c556-4f47-8b61-bfb1780b4ac5")) {  // killing tiago
    		killer.sendMessage("§6Congratulations! You have obtained a §5Tiago Soul");
    		SoulEnchantments.changeSouls(killer, 1, quickbarPlugin);
    	}
    	else if(killed.getUniqueId().toString().equalsIgnoreCase("df736569-ffed-40e7-9c92-074661b86b09"))  {  // killing lucas (10% chance of tiago soul)
    		int random = (int) (Math.random() * 10 + 1);  // random int in interval [0, 9] (inclusive)
    		if(random == 0)  {
    			killer.sendMessage("§6Congratulations! You have obtained a §5Tiago Soul");
    			SoulEnchantments.changeSouls(killer, 1, quickbarPlugin);
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
    		
    		if(SoulEnchantments.validDoublexpTypes.contains(weaponType) && SoulEnchantments.hasCustomEnchant(murderWeapon, SoulEnchantments.ENCHANTMENT_DOUBLEXP))  {
    			e.setDroppedExp(e.getDroppedExp() * 2);  // Double the xp dropped
    		}
    		
    		if(SoulEnchantments.validAbsorptionTypes.contains(weaponType) && SoulEnchantments.hasCustomEnchant(murderWeapon, SoulEnchantments.ENCHANTMENT_ABSORPTION))  {
    			Collection<ItemStack> drops = e.getDrops();
    			for(ItemStack is : drops)  {
    				GeneralUtil.giveItem(killer, is);
    			}
    			e.getDrops().clear();
    			
    			// Handle the xp dropped
    			int xpToGive = e.getDroppedExp();
    			e.setDroppedExp(0);
    			XPUtil.changeExp(killer, xpToGive);
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
        			QuickbarSwitcher.switchItems(player);
        			
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
    		if(GeneralUtil.isInConfig(player.getName(), "deaths.", quickbarPlugin.getConfig()))  { // Add death to config
    			quickbarPlugin.getConfig().set("deaths." + player.getUniqueId(), quickbarPlugin.getConfig().getInt("deaths." + player.getUniqueId()) + 1);
    			quickbarPlugin.saveConfig();
    		}
    		else  { // Create new entry for player, then add death to config
    			quickbarPlugin.getConfig().set("deaths." + player.getUniqueId(), 1);
    			quickbarPlugin.saveConfig();
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
    		World world = player.getWorld();
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
    			SoulEnchantments.changeSouls(player, 1, quickbarPlugin);
    			player.sendMessage("§6Congratulations! You have obtained a Tiago Soul by destroying his majestic statue.");
    		}
    	}
    	
    	if(SoulEnchantments.validAbsorptionTypes.contains(item.getType()) && SoulEnchantments.hasCustomEnchant(item, SoulEnchantments.ENCHANTMENT_ABSORPTION))  {
    		// The item with which the block is being broken is of a valid type and contains the absorption echantment
    		
    		// First handle the xp drops
    		int xpToGive = e.getExpToDrop();
    		e.setExpToDrop(0);
    		XPUtil.changeExp(player, xpToGive);
    		
    		// Then handle the loot drops
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
    				GeneralUtil.giveItem(player, is);
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
						GeneralUtil.giveItem(player, is);
						continue;
					}
					
					//If we suspect TEs might be duped only reward block
	                if(dontRewardTE) {
	                    if(!is.getType().isBlock()) {
	                        GeneralUtil.giveItem(player, is);
	                        continue;
	                    }
	                }
	                
	                if(mcMMO.getPlaceStore().isTrue(block.getState()))  {
	                	GeneralUtil.giveItem(player, is);
	                	continue;
	                }
	                
	                if (block.getMetadata(mcMMO.BONUS_DROPS_METAKEY).size() > 0) {
	                    BonusDropMeta bonusDropMeta = (BonusDropMeta) block.getMetadata(mcMMO.BONUS_DROPS_METAKEY).get(0);
	                    bonusCount = bonusDropMeta.asInt();
	                    	
	                    for (int i = 0; i < bonusCount + 1; i++) {
	                        GeneralUtil.giveItem(player, is);
	                    }
	                }
	                else  {
	                	GeneralUtil.giveItem(player, is);
	                }
				}
			}
			
			
			if(block.hasMetadata(mcMMO.BONUS_DROPS_METAKEY))
	            block.removeMetadata(mcMMO.BONUS_DROPS_METAKEY, mcMMOPlugin);
    	}
    	
    	if(SoulEnchantments.validDoublexpTypes.contains(item.getType()) && SoulEnchantments.hasCustomEnchant(item, SoulEnchantments.ENCHANTMENT_DOUBLEXP))  {
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
    		Trackers.addAppleCount(player.getUniqueId(), quickbarPlugin);
    	}
    }
    
    @EventHandler
    public void onPlayerLogIn(PlayerJoinEvent e)  {
    	Player p = e.getPlayer();
    	String pathApples = "applesEaten." + p.getUniqueId().toString();
    	String pathDeaths = "deaths." + p.getUniqueId().toString();
    	if(!quickbarPlugin.getConfig().isSet(pathApples))  {
    		quickbarPlugin.getConfig().set(pathApples, 0);
    	}
    	if(!quickbarPlugin.getConfig().isSet(pathDeaths))  {
    		quickbarPlugin.getConfig().set(pathDeaths, 0);
    	}
    }
	
	@EventHandler
    public void onDamage(EntityDamageByEntityEvent e)  {
    	Entity damagerEntity = e.getDamager();
    	Player damager = null;
    	if(damagerEntity instanceof Player)  {
    		damager = (Player) damagerEntity;
    	}
    	else if(damagerEntity instanceof Arrow)  {
    		if(((Arrow)damagerEntity).getShooter() instanceof Player)  {
    			damager = (Player)((Arrow)damagerEntity).getShooter();
    		}
    	}
    	
    	if(damager != null && damager.getInventory() != null && damager.getInventory().getItemInMainHand() != null)  {
    		ItemStack item = damager.getInventory().getItemInMainHand();
    		if(SoulEnchantments.validThunderlordTypes.contains(item.getType()) && SoulEnchantments.hasCustomEnchant(item, SoulEnchantments.ENCHANTMENT_THUNDERLORD))  {
				// Apply potential thunderlord damage and lightning, 10% chance
				int random = (int) (Math.random() * 10 + 1);
				if(random == 1)  {
					e.setDamage(SoulEnchantments.getDamageWithThunderlord(e.getDamage()));
    				World world = e.getEntity().getWorld();
    				world.strikeLightningEffect(e.getEntity().getLocation());
				}
			}
    		if(SoulEnchantments.validVampirismTypes.contains(item.getType()) && SoulEnchantments.hasCustomEnchant(item, SoulEnchantments.ENCHANTMENT_VAMPIRISM))  {
    			// Item has the enchantment vampirism
    			// heal for 10 % of damage dealt, or 5% if its a bow
    			double multiplier = 0.1;
    			if(item.getType().equals(Material.BOW))  {
    				multiplier = 0.05;
    			}
    			
    			double newHealth = damager.getHealth() + multiplier*e.getDamage();
    			if(newHealth > damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())  {
    				// to prevent setting the health above 20.0 which is the maximum
    				damager.setHealth(damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    			}
    			else  {
    				damager.setHealth(newHealth);
    			}
    		}
    	}
    	
    }
}
