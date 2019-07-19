package net.minemora.reportsystem.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.minemora.reportsystem.CachedReport;
import net.minemora.reportsystem.util.Chat;

public class CommandLegit extends Command implements TabExecutor {

	public CommandLegit() {
		super("legitimate", "sup", "legit");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		if(args.length == 1) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			if(!CachedReport.getCache().containsKey(args[0])) {
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("&c¡El jugador no ha sido reportado recientemente!")));
				return;
			}
			if(CachedReport.getCache().get(args[0]).isLegit()) {
				unLegit(args[0]);
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("&e¡El jugador ha sido desmarcado!")));
			}
			else {
				legit(args[0]);
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("&a¡El jugador ha sido marcado como legítimo!")));
			}
		}
	}
	
	public static void legit(String playerName) {
		RedisBungee.getApi().sendChannelMessage("ReportSystem", "Legit:" + playerName);
	}
	
	public static void unLegit(String playerName) {
		RedisBungee.getApi().sendChannelMessage("ReportSystem", "UnLegit:" + playerName);
	}
	
	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return new ArrayList<String>();
		}		
		List<String> result = new ArrayList<>();  
		ProxiedPlayer player = (ProxiedPlayer) sender;
		if(args.length == 1) {
			for(UUID uid : RedisBungee.getApi().getPlayersOnServer(player.getServer().getInfo().getName())) {
				String playerName = RedisBungee.getApi().getNameFromUuid(uid);
				if(!playerName.equals(player.getName())) {
					result.add(playerName);
				}
			}
		}
		else if (args.length == 2) {
			for(UUID uid : RedisBungee.getApi().getPlayersOnServer(player.getServer().getInfo().getName())) {
				String playerName = RedisBungee.getApi().getNameFromUuid(uid);
				if(!playerName.equals(player.getName())) {
					if(playerName.toLowerCase().startsWith(args[1])) {
						result.add(playerName);
					}
				}
			}
		}
		return result;
	}
	
}