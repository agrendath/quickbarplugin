package com.gmail.leal.mendo.QuickbarPlugin;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentExtraction {
	
	/**
	 * Extracts the given enchantment from the given item and gives it in book form to the given player
	 * @pre item must not be null
	 *  | item != null
	 * @pre player must not be null
	 * 	| player != null
	 * @pre enchantment must not be null
	 * 	| enchantment != null
	 * @post The given item no longer has the given enchantment on it, but is the same otherwise
	 * @post The given player now has an enchanted book with the given enchantment on it
	 * @param player The player that will receive the enchanted book with the given enchantment
	 * @param item The item from which to extract the enchantment
	 * @param enchantment The given enchantment to extract
	 */
	public static void extract(Player player, ItemStack item, Enchantment enchantment, int xpCost)  {
		if(XPUtil.getPlayerExp(player) < xpCost)  {
			player.sendMessage("§4You do not have enough xp for this, you need " + xpCost + " but you have " + XPUtil.getPlayerExp(player));
			return;
		}
		
		ItemMeta meta = item.getItemMeta();
		if(!meta.hasEnchant(enchantment))  {
			player.sendMessage("§4The item does not have this enchantment.");
			return;
		}
		
		int enchantLevel = meta.getEnchantLevel(enchantment);
		meta.removeEnchant(enchantment);
		item.setItemMeta(meta);
		
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
		book.addUnsafeEnchantment(enchantment, enchantLevel);
		GeneralUtil.giveItem(player, book);
		XPUtil.takeExp(player, xpCost);
		
		player.sendMessage("§5Successfully extracted the enchantment!");
	}
}
