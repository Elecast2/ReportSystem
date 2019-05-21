package net.minemora.reportsystem.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.minemora.reportsystem.network.PluginMessageHandler;

public class CommandGoTo extends Command {

	public CommandGoTo() {
		super("goto");
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
				PluginMessageHandler.sendGoTo(player.getName(), args[1], true);
			}
		}
		else {
			PluginMessageHandler.sendGoTo(player.getName(), args[0], false);
		}
	}
}