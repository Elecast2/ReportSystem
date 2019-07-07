package net.minemora.reportsystem.command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.minemora.reportsystem.util.Chat;

public class CommandGlobalSpy extends Command {
	
	private static Set<UUID> globalSpy = new HashSet<>();
	
	private static Map<UUID, String> queue = new HashMap<>();

	public CommandGlobalSpy() {
		super("globalspy", "sup", "gspy");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) sender;
		if(getGlobalSpy().contains(player.getUniqueId())) {
			RedisBungee.getApi().sendChannelMessage("ReportSystem", "GlobalSpy:remove:" + player.getUniqueId());
			player.sendMessage(TextComponent.fromLegacyText(Chat.format("&bHas &cdesactivado &bel modo: &c&lEspiar &9&lGlobal")));
		}
		else {
			RedisBungee.getApi().sendChannelMessage("ReportSystem", "GlobalSpy:add:" + player.getUniqueId());
			player.sendMessage(TextComponent.fromLegacyText(Chat.format("&bHas &aactivado &bel modo: &c&lEspiar &9&lGlobal")));
		}
	}

	public static Set<UUID> getGlobalSpy() {
		return globalSpy;
	}

	public static Map<UUID, String> getQueue() {
		return queue;
	}

}
