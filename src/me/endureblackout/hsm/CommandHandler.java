
package me.endureblackout.hsm;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandHandler implements CommandExecutor {

	HSM						core;

	public static ItemStack	missile		= new ItemStack(Material.FIREWORK);
	ItemMeta				missileMeta	= missile.getItemMeta();

	public CommandHandler(HSM instance) {
		this.core = instance;

		missileMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', core.getConfig().getString("Missile Name")));
		missile.setItemMeta(missileMeta);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("hsm")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("get")) {
					if (sender instanceof Player) {
						((Player) sender).getInventory().addItem(missile);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "Missile received"));
					} else {
						sender.sendMessage(ChatColor.RED + "Only ingame players can use this command");
					}
				}

				if (args[0].equalsIgnoreCase("remove")) {
					if (sender instanceof Player) {
						if (((Player) sender).getInventory().contains(missile)) {
							((Player) sender).getInventory().remove(missile);
						}
					}
				}
			}
		}

		return true;
	}

}
