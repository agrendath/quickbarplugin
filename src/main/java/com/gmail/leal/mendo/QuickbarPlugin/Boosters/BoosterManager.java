package com.gmail.leal.mendo.QuickbarPlugin.Boosters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class BoosterManager {
	private List<ActiveBooster> activeBoosters;
	
	public BoosterManager()  {
		this.activeBoosters = new ArrayList<ActiveBooster>();
	}
	
	public List<ActiveBooster> getActiveBoosters()  {
		return this.activeBoosters;
	}
	
	public void addBooster(ActiveBooster booster)  {
		activeBoosters.add(booster);
	}
	
	public void removeXp(Player player, float xp)  {
		ActiveBooster booster = null;
		for(ActiveBooster activeBooster : activeBoosters)  {
			if(activeBooster.getPlayer().equals(player))  {
				booster = activeBooster;
			}
		}
		
		float beforeXp = booster.getRemainingXp();
		float halfway = 0.5f*BoosterUtil.BOOSTER_XP_AMOUNT;
		float quarterway = 0.75f*BoosterUtil.BOOSTER_XP_AMOUNT;
		float threequarterway = 0.25f*BoosterUtil.BOOSTER_XP_AMOUNT;
		booster.removeXp(xp);
		if(booster.getRemainingXp() <= 0)  {
			this.removeBooster(player);
			player.sendMessage("§5Sadly your McMMO XP Booster has expired!");
		}
		else if(booster.getRemainingXp() <= quarterway && beforeXp > quarterway)  {
			player.sendMessage("§5Notice: Your MxMMO XP Booster is 25% used");
		}
		else if(booster.getRemainingXp() <= halfway && beforeXp > halfway)  {
			player.sendMessage("§5Notice: Your McMMO XP Booster is 50% used");
		}
		else if(booster.getRemainingXp() <= threequarterway && beforeXp > threequarterway)  {
			player.sendMessage("§5Notice: Your McMMO XP Booster is 75% used");
		}
	}
	
	public void removeBooster(Player player)  {
		for(int i = 0; i < activeBoosters.size(); i++)  {
			ActiveBooster booster = activeBoosters.get(i);
			if(booster.getPlayer().equals(player))  {
				activeBoosters.remove(i);
				return;
			}
		}
	}
}
