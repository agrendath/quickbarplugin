package com.gmail.leal.mendo.QuickbarPlugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

public class TabCompletion implements TabCompleter{
	@Override
    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args)  {
		List<String> result = new ArrayList<String>();
		if(cmd.getName().equalsIgnoreCase("soulenchant") && args.length == 1 && sender instanceof Player)  {
			for(String enchantment : SoulEnchantments.getSoulEnchantments())  {
				result.add(enchantment);
			}
			return StringUtil.copyPartialMatches(args[0], result, new ArrayList<String>());
		}
		else if(cmd.getName().equalsIgnoreCase("booster") && args.length == 1 && sender instanceof Player)  {
			for(PrimarySkillType skill : PrimarySkillType.values())  {
				result.add(skill.getName());
			}
			return StringUtil.copyPartialMatches(args[0], result, new ArrayList<String>());
		}
		return null;
	}
}
