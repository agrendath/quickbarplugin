package com.gmail.leal.mendo.QuickbarPlugin.Boosters;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

public class ActiveBooster {
	Player player;
	PrimarySkillType skill;
	float remainingXp;
	
	public ActiveBooster(Player player, PrimarySkillType skill, float startingXp)  {
		this.player = player;
		this.skill = skill;
		this.remainingXp = startingXp;
	}
	
	public float getRemainingXp()  {
		return this.remainingXp;
	}
	
	public Player getPlayer()  {
		return this.player;
	}
	
	public PrimarySkillType getSkill()  {
		return this.skill;
	}
	
	public void removeXp(float xp)  {
		remainingXp -= xp;
	}
}
