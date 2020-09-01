package com.gmail.leal.mendo.QuickbarPlugin.Boosters;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

public class ActiveBooster {
	Player player;
	PrimarySkillType skill;
	long startTime;
	
	public ActiveBooster(Player player, PrimarySkillType skill)  {
		this.player = player;
		this.skill = skill;
		this.startTime = System.currentTimeMillis();
	}
	
	public Player getPlayer()  {
		return this.player;
	}
	
	public PrimarySkillType getSkill()  {
		return this.skill;
	}
	
	/**
	 * Returns the remaining time of this booster in minutes
	 * @return The remaining time of this booster in minutes
	 */
	public int getRemainingTime()  {
		long current = System.currentTimeMillis();
		int elapsed = Math.toIntExact(Math.subtractExact(current, this.startTime))/1000; // elapsed time in seconds
		if(elapsed > BoosterUtil.BOOSTER_DURATION)  {
			return 0;
		}
		return (BoosterUtil.BOOSTER_DURATION - elapsed)/60;
	}
}
