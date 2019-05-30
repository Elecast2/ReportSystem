package net.minemora.reportsystem.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minemora.reportsystem.ReportSystem;

public class CommandSpy implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(ReportSystem.getSpectators().contains(player.getName())) {
				ReportSystem.getPlugin().getVisibilityManager().toggleSpy(player, false);
				ReportSystem.getSpectators().remove(player.getName());
			}
			else {
				ReportSystem.getPlugin().getVisibilityManager().toggleSpy(player, true);
				ReportSystem.getSpectators().add(player.getName());
			}
		}
		return true;
	}
}
