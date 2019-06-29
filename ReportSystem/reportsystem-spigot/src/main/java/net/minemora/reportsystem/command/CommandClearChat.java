package net.minemora.reportsystem.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minemora.reportsystem.VaultManager;
import net.minemora.reportsystem.util.ChatUtils;

public class CommandClearChat implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(!VaultManager.hasPermission(player, "sup")) {
				return true;
			}
			if(args.length > 0) {
				return true;
			}
			for(int i = 0; i<20; i++) {
				Bukkit.broadcastMessage("");
			}
			Bukkit.broadcastMessage(ChatUtils.format("&cChat limpiado por: &4" + player.getName()));
		}
		return true;
	}
}
