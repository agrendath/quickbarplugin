# Quickbarplugin
Minecraft Quickbarplugin

softdepend: mcMMO [absorption enchantment works with double drops]

Functionality/Commands:
- /emma - Gives the player a special tool that lets you switch your quickbar with the first line of your inventory by right-clicking
- /emma2 \<amount\> - Converts your xp to a certain amount of experience bottles, experience bottles
- /janita \<playername\> - Shows how many time a player has died since installation of the plugin
- /janita2 \<playername\> - Shows how many apples a player has eaten since installation of the plugin
- /tiago - Displays useful information about tiago
- /lucas - Displays useful information about lucas
- /souls - Shows a player's balance of Tiago Souls
- /souls give \[playername\] \[amount\] - Give the specified amount of souls to another player (this will deduct them from your balance, obviously)
- /soulenchant \<enchantment\> - Enchants the item the player is holding with a soulenchantment, see available enchantments below
- /janitatop - Shows the leaderboard of the top 10 players with most deaths
- /janita2top - Shows the leaderboard of the top 10 players with most apples eaten
- /extract \<enchantment\> - Extracts an enchantment from the item in the player's hand by giving it to them in the form of an enchanted book and removing it from the item, this costs 2000 xp and does not work for soul enchantments

Soul Enchantments:
- Magnetism: usable on all tools and weapons, when you destroy blocks or kill mobs/players the loot drops and xp drops get absorbed right into your inventory/xp bar
- Indestructibility: usable on diamond and netherite swords, makes the item indestructible/unbreakable
- Harvesting: usable on all tools and weapons, will drop double xp from any entity you kill or any block you break
- Vampirism: usable on swords, bows and axes, will heal the attacker for 10% of damage dealt, bows will only heal for 5%
- Swiftness: usable on all boots, will make the player faster when equipped with it
- Looting: usable on bows, this is just a way to put the looting (only 3) enchantment on bows which is not possible in vanilla minecraft
- Thunderlord: usable on all weapons and bows, gives a 10 % chance to summon lightning doing 25% of the original hit's damage

Soul Enchantments Costs:
- Magnetism: 1 Tiago Soul and 2000 xp
- Indestructibility: 1 Tiago Soul and 2500 xp
- Harvesting: 1 Tiago Soul and 3000 xp
- Vampirism: 1 Tiago Soul and 4000 xp
- Swiftness: 1 Tiago Soul and 4000 xp
- Looting: 0 Tiago Souls and 2000 xp
- Thunderlord: 1 Tiago Souls and 3500 xp

Permission Nodes:
- quickbarplugin.tiago: Grants access to the /tiago command
- quickbarplugin.emma: Grants access to the /emma command and the quickbar switching tool
- quickbarplugin.emma2: Grants access to the /emma2 command to convert xp into xp bottles
- quickbarplugin.janita: Grants access to the /janita command
- quickbarplugin.janita2: Grants access to the /janita2 command
- quickbarplugin.lucas: Grants access to the /lucas command
- quickbarplugin.souls: Grants access to the /souls command and the manipulating of tiago souls
- quickbarplugin.soulenchant: Grants access to the /soulenchant command
- quickbarplugin.janitatop: Grants access to the /janitatop command
- quickbarplugin.janita2top: Grants access to the /janita2top command
- quickbarplugin.extract: Grants access to the /extract command
