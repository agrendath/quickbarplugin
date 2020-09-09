package com.gmail.leal.mendo.QuickbarPlugin.Boosters;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.leal.mendo.QuickbarPlugin.GeneralUtil;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

public class BoosterUtil {
	
	public static final String XP_BOOSTER_NAME = "McMMO XP Booster";
	public static final int BOOSTER_COST = 5; // cost of a booster in diamonds
	public static final int BOOSTER_DURATION = 15*60; // duration of a booster in seconds, keep this a multiple of 60 preferably
	public static final int EXTENDED_BOOSTER_DURATION = BOOSTER_DURATION*2; // duration of a booster for skills that are harder to farm
	
	public static ItemStack getBoosterItem(PrimarySkillType skill)  {
		ItemStack result = new ItemStack(Material.PAPER, 1);
		ItemMeta meta = result.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add(skill.getName());
		meta.setDisplayName(XP_BOOSTER_NAME);
		meta.setLore(lore);
		result.setItemMeta(meta);
		return result;
	}
	
	public static boolean isValidSkill(String skillName)  {
		PrimarySkillType[] validSkills = PrimarySkillType.values();
		List<String> validNames = new ArrayList<String>();
		for(int i = 0; i < validSkills.length; i++)  {
			validNames.add(validSkills[i].getName());
		}
		for(String name : validNames)  {
			if(name.equalsIgnoreCase(skillName)  ) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isBooster(ItemStack booster)  {
		ItemMeta meta = booster.getItemMeta();
		if(booster.getType() == Material.PAPER && meta.hasLore() && meta.getDisplayName().equals(XP_BOOSTER_NAME))  {
			return true;
		}
		return false;
	}
	
	public static boolean useBooster(Player player, ItemStack booster, BoosterManager manager, Plugin plugin)  {
		if(hasActiveBooster(player, manager))  {
			player.sendMessage("§5You already have a booster activated, use it fully or log out to start a new one");
			return false;
		}
		ItemMeta meta = booster.getItemMeta();
		PrimarySkillType skill = PrimarySkillType.getSkill(meta.getLore().get(0));
		ActiveBooster activeBooster = new ActiveBooster(player, skill);
		manager.addBooster(activeBooster);
		GeneralUtil.takeItemFromMainHand(player);
		player.sendMessage("§5You activated an McMMO XP Booster for " + skill.getName()+ ", it will last for " + BOOSTER_DURATION/60 + " minutes!");
		
		final Player playerFinal = player;
		final BoosterManager managerFinal = manager;
		
		int booster_duration = BOOSTER_DURATION;
		if(skill.equals(PrimarySkillType.ALCHEMY) || skill.equals(PrimarySkillType.FISHING) || skill.equals(PrimarySkillType.SMELTING) || skill.equals(PrimarySkillType.TAMING))  {
			booster_duration = EXTENDED_BOOSTER_DURATION;
		}
		
		// Schedule task to remove booster in 15 mins if not already removed by then (on logout for example)
		new BukkitRunnable()  {
			
			@Override
			public void run()  {
				if(BoosterUtil.hasActiveBooster(playerFinal, managerFinal))  {
					managerFinal.removeBooster(playerFinal);
					playerFinal.sendMessage("§5Your booster has expired!");
				}
			}
		}.runTaskLaterAsynchronously(plugin, booster_duration*20); // delay in ticks (20 ticks in 1 second)
		
		return true;
	}
	
	public static boolean hasActiveBooster(Player player, BoosterManager manager)  {
		for(ActiveBooster booster : manager.getActiveBoosters())  {
			if(booster.getPlayer().equals(player))  {
				return true;
			}
		}
		return false;
	}
	
	public static ActiveBooster getBooster(Player player, BoosterManager boosterManager)  {
		for(ActiveBooster booster : boosterManager.getActiveBoosters())  {
			if(booster.getPlayer().equals(player))  {
				return booster;
			}
		}
		return null;
	}
	
	public static boolean hasActiveBoosterForSkill(Player player, PrimarySkillType skill, BoosterManager manager)  {
		for(ActiveBooster booster : manager.getActiveBoosters())  {
			if(booster.getPlayer().equals(player) && booster.getSkill().equals(skill))  {
				return true;
			}
		}
		return false;
	}
}
