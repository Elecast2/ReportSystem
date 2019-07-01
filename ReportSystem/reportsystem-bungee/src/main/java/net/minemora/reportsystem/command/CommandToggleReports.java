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

public class CommandToggleReports extends Command {
	
	private static Set<UUID> hideReports = new HashSet<UUID>();

	public CommandToggleReports() {
		super("togglereports", "sup", "treports");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) sender;
		if(getHideReports().contains(player.getUniqueId())) {
			RedisBungee.getApi().sendChannelMessage("ReportSystem", "ToggleReports:remove:" + player.getUniqueId());
			player.sendMessage(TextComponent.fromLegacyText(Chat.format("&bHas &aactivado &bla visualización de reportes")));
		}
		else {
			RedisBungee.getApi().sendChannelMessage("ReportSystem", "ToggleReports:add:" + player.getUniqueId());
			player.sendMessage(TextComponent.fromLegacyText(Chat.format("&bHas &cdesactivado &bla visualización de reportes")));
		}
	}

	public static Set<UUID> getHideReports() {
		return hideReports;
	}
}
