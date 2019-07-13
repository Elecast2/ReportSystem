package net.minemora.reportsystem.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minemora.reportsystem.ReportSystem;
import net.minemora.reportsystem.VaultManager;
import net.minemora.reportsystem.util.ChatUtils;

public class CommandSpy implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!VaultManager.hasPermission(player, "sup")) {
				return true;
			}
			if(ReportSystem.getSpectators().contains(player.getName())) {
				set(player, false);
			}
			else {
				set(player, true);
			}
		}
		return true;
	}
	
	public static void set(Player player, boolean enable) {
		ReportSystem.getPlugin().getVisibilityManager().toggleSpy(player, enable);
		if(enable) {
			ReportSystem.getSpectators().add(player.getName());
			player.sendMessage(ChatUtils.format("&aEstas en modo &c&lEspiar")); //TODO LANG
		}
		else {
			ReportSystem.getSpectators().remove(player.getName());
			player.sendMessage(ChatUtils.format("&6Ya no estas en modo &c&lEspiar")); //TODO LANG
		}
	}
}