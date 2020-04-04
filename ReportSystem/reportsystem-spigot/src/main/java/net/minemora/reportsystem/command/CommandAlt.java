package net.minemora.reportsystem.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minemora.reportsystem.ReportSystem;
import net.minemora.reportsystem.VaultManager;
import net.minemora.reportsystem.alt.AltManager;
import net.minemora.reportsystem.util.ChatUtils;

public class CommandAlt implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!VaultManager.hasPermission(player, "rs.alt")) {
				return true;
			}
			if(ReportSystem.getSpectators().contains(player.getName())) {
				player.sendMessage(ChatUtils.format("&c¡No puedes usar este comando mientras estas en espiar!"));
			}
			else {
				set(player);
			}
		}
		return true;
	}
	
	private void set(Player player) {
		AltManager.getInstance().altPlayer(player);
	}
}