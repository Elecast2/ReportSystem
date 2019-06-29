package net.minemora.reportsystem.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.minemora.reportsystem.StaffMessage;

public class CommandChatStaff extends Command {
	
	public CommandChatStaff() {
		super("chatstaff", "staff.chat", "s");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		if(args.length >= 1) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			String message = String.join(" ", args);
			new StaffMessage(player.getName(), message).send();
		}
	}
}