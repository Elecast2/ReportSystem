package net.minemora.reportsystem.command;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.minemora.reportsystem.network.PluginMessageHandler;
import net.minemora.reportsystem.util.Chat;

public class CommandGoTo extends Command {
	
	private static Map<String, String> assignedCache = new HashMap<>();

	public CommandGoTo() {
		super("goto", "staff", "gt");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		if(args.length > 2 || args.length == 0) {
			return; //TODO correct usage
		}
		ProxiedPlayer player = (ProxiedPlayer) sender;
		if(args[0].startsWith("-")) {
			if(args.length == 1) {
				return; //TODO correct usage
			}
			if(args[0].equals("-v")) {
				if(assignedCache.containsKey(args[1])) {
					String assigned = assignedCache.get(args[1]);
					player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cEl staff &b" + assigned 
							+ " &cya se encuentra revisando a &4" + args[1] + " &e si aun asi deseas ir usa &a/goto -i " + args[1])));
					return;
				}
				PluginMessageHandler.sendGoTo(player.getName(), args[1], true);
				assignedCache.put(args[1], player.getName());
			}
			else if(args[0].equals("-i")) {
				PluginMessageHandler.sendGoTo(player.getName(), args[1], true);
			}
			else {
				return; //TODO correct usage
			}
		}
		else {
			PluginMessageHandler.sendGoTo(player.getName(), args[0], false);
		}
	}

	public static Map<String, String> getAssignedCache() {
		return assignedCache;
	}

}