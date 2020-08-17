package com.gmail.leal.mendo.QuickbarPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Trackers {
	
	public static void addAppleCount(UUID playerId, Plugin quickbarPlugin)  {
    	String path = "applesEaten." + playerId.toString();
    	if(quickbarPlugin.getConfig().isSet(path))  {
    		quickbarPlugin.getConfig().set(path, quickbarPlugin.getConfig().getInt(path) + 1);
    	}
    	else  {
    		quickbarPlugin.getConfig().set(path, 1);
    	}
    	quickbarPlugin.saveConfig();
    }
	
	/**
     * Returns a list of strings representing the leaderboard for a certain stat that the plugin tracks
     * @param statPathPrefix The prefix to the stat's path e.g. "deaths" or "applesEaten", NOT INCLUDING THE '.'
     * @param title The title at the top of the leaderboard
     * @returna A list of strings representing the leaderboard for a certain stat that the plugin tracks
     */
    public static List<String> getLeaderboard(String statPathPrefix, String title, FileConfiguration config)  {
    	List<String> result = new ArrayList<String>();
    	result.add("---------- " + title + " Leaderboard ----------");
    	
    	if(config.getConfigurationSection(statPathPrefix) == null || config.getConfigurationSection(statPathPrefix).getKeys(false) == null)  {
    		result.add("No players on the leaderboard yet");
    		return result;
    	}
    	
    	Set<String> keys = config.getConfigurationSection(statPathPrefix).getKeys(false);
    	String[] sortedUuids = new String[keys.size()];
    	int i = 0;
    	for(String uuid : keys)  {
    		sortedUuids[i] = uuid;
    		int j = i - 1;
    		int k = i;
    		while(j >= 0 && config.getInt(statPathPrefix + "." + sortedUuids[k]) > config.getInt(statPathPrefix + "." + sortedUuids[j]))  {
    			GeneralUtil.swapInStringList(sortedUuids, k, j);
    			k--;
    			j--;
    		}
    		i++;
    	}
    	
    	for(int x = 0; x < sortedUuids.length; x++)  {
    		if(x > 9)  {
    			break;
    		}
    		result.add((x + 1) + ". " + Bukkit.getOfflinePlayer(UUID.fromString(sortedUuids[x])).getName() + ": " + config.getString(statPathPrefix + "." + sortedUuids[x]));
    	}
    	
    	return result;
    }
    
    
}
