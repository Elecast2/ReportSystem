package net.minemora.reportsystem.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.minemora.reportsystem.network.PluginMessageHandler;
import net.minemora.reportsystem.util.Chat;

public class CommandRepeatGoTo extends Command {

	public CommandRepeatGoTo() {
		super("repeatgoto", "staff", "rgt");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) sender;
		if(!CommandGoTo.getLastGoTo().containsKey(player.getName())) {
			player.sendMessage(TextComponent.fromLegacyText(Chat.format("&c¡No has espiado a alguien recientemente!")));
			return;
		}
		PluginMessageHandler.sendGoTo(player.getName(), CommandGoTo.getLastGoTo().get(player.getName()), true);
	}
}
