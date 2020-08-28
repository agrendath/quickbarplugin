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
		this.startTime = System.currentTimeMillis()*1000;
	}
	
	public Player getPlayer()  {
		return this.player;
	}
	
	public PrimarySkillType getSkill()  {
		return this.skill;
	}
	
	/**
	 * Returns the remaining time of this booster in seconds
	 * @return The remaining time of this booster in seconds
	 */
	public int getRemainingTime()  {
		long current = System.currentTimeMillis()*1000;
		int elapsed = (int)(current - this.startTime);
		if(BoosterUtil.BOOSTER_DURATION < elapsed)  {
			return 0;
		}
		return BoosterUtil.BOOSTER_DURATION - elapsed;
	}
}
