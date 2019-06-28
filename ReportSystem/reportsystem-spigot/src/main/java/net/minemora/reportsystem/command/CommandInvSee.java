package net.minemora.reportsystem.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minemora.reportsystem.VaultManager;

public class CommandInvSee implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!VaultManager.hasPermission(player, "sup")) {
				return true;
			}
			if(args.length == 0 || args.length > 1) {
				return true;
			}
			if(Bukkit.getPlayer(args[0]) == null) {
				return true;
			}
			Player target = Bukkit.getPlayer(args[0]);
			player.openInventory(target.getInventory());
		}
		return true;
	}
}
