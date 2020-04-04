package net.minemora.reportsystem.command;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.minemora.reportsystem.util.Chat;

public class CommandToggleStaffChat extends Command {
	
	private static Set<UUID> disabledPlayers = new HashSet<UUID>();

	public CommandToggleStaffChat() {
		super("togglestaffchat", "staff.chat", "tsc");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) sender;
		if(getDisabledPlayers().contains(player.getUniqueId())) {
			RedisBungee.getApi().sendChannelMessage("ReportSystem", "ToggleStaffChat:remove:" + player.getUniqueId());
			player.sendMessage(TextComponent.fromLegacyText(Chat.format("&bHas &aactivado &bla visualización de chat de staff")));
		}
		else {
			RedisBungee.getApi().sendChannelMessage("ReportSystem", "ToggleStaffChat:add:" + player.getUniqueId());
			player.sendMessage(TextComponent.fromLegacyText(Chat.format("&bHas &cdesactivado &bla visualización de chat de staff")));
		}
	}

	public static Set<UUID> getDisabledPlayers() {
		return disabledPlayers;
	}
}
