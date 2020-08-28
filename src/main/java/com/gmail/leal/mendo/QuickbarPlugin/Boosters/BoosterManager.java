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
