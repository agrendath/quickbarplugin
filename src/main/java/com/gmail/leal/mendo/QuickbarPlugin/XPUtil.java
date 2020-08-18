package com.gmail.leal.mendo.QuickbarPlugin;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class XPUtil {
	
	public static void convertToBottles(Player player, int amount)  {
		int xpAmount = amount*100;
		
		// Check if the player has enough xp to get the required amount of bottles
		if(xpAmount > XPUtil.getPlayerExp(player))  {
			// Get maximum amount of bottles you can get
			xpAmount = XPUtil.getPlayerExp(player);
			amount = (int) Math.floor(xpAmount/100);
		}
		int extraAmount = xpAmount%100;
		
		
		if(amount > 64)  {
			int stacks = (int) Math.floor(amount/64);
			int remainder = amount%64;
			for(int n = 0; n < stacks; n++)  {
				GeneralUtil.giveItem(player, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
			}
			GeneralUtil.giveItem(player, new ItemStack(Material.EXPERIENCE_BOTTLE, remainder));
		}
		else  {
			GeneralUtil.giveItem(player, new ItemStack(Material.EXPERIENCE_BOTTLE, amount));
		}
		
		XPUtil.takeExp(player, xpAmount);
		XPUtil.takeExp(player, -extraAmount);
	}
	
	/**
	 * Change a Player's exp.
	 * <p>
	 * This method should be used in place of {@link Player#giveExp(int)}, which does not properly
	 * account for different levels requiring different amounts of experience.
	 * 
	 * @param player the Player affected
	 * @param exp the amount of experience to add or remove
	 */
	public static void changeExp(Player player, int exp) {
		exp += getPlayerExp(player);

		if (exp < 0) {
			exp = 0;
		}

		double levelAndExp = getLevelFromExp(exp);

		int level = (int) levelAndExp;
		player.setLevel(level);
		player.setExp((float) (levelAndExp - level));
	}
	
	/**
	 * Calculates level based on total experience.
	 * 
	 * @param exp the total experience
	 * 
	 * @return the level calculated
	 */
	public static double getLevelFromExp(long exp) {
		if (exp > 1395) {
			return (Math.sqrt(72 * exp - 54215) + 325) / 18;
		}
		if (exp > 315) {
			return Math.sqrt(40 * exp - 7839) / 10 + 8.1;
		}
		if (exp > 0) {
			return Math.sqrt(exp + 9) - 3;
		}
		return 0;
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
}
