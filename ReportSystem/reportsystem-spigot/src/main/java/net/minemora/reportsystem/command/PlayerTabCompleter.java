package net.minemora.reportsystem.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class PlayerTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(args.length > 1) {
			return null;
		}
		if(sender instanceof Player) {
			Player player = (Player) sender;
			List<String> result = new ArrayList<>();  
			if(args.length == 0) {
				for(Player lp : Bukkit.getOnlinePlayers()) {
					if(!lp.equals(player)) {
						result.add(lp.getName());
					}
				}
			}
			else {
				for(Player lp : Bukkit.getOnlinePlayers()) {
					if(!lp.equals(player)) {
						if(lp.getName().toLowerCase().startsWith(args[0])) {
							result.add(lp.getName());
						}
					}
				}
			}
			return result;
		}
		return null;
	}
}