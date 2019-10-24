package net.minemora.reportsystem.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.minemora.reportsystem.Report;
import net.minemora.reportsystem.ReportSystem;
import net.minemora.reportsystem.database.Database;
import net.minemora.reportsystem.util.Chat;
import net.minemora.reportsystem.util.Util;

public class CommandREHistory extends Command implements TabExecutor {

	public CommandREHistory() {
		super("rehistory", "staff");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		if(args.length == 1) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			UUID uid = RedisBungee.getApi().getUuidFromName(args[0]);
			boolean online = true;
			if(uid == null) {
				online = false;
			}
			if(online) {
				ServerInfo serverInfo = RedisBungee.getApi().getServerFor(uid); //TODO reparar npe aqui
				if(serverInfo == null) {
					online = false;
				}
			}
			
			final boolean isOn = online;
			
			ReportSystem.getPlugin().getProxy().getScheduler().runAsync(ReportSystem.getPlugin(), new Runnable() {
				@Override
				public void run() {
					if(isOn) {
						showHistory(args[0], uid, player);
					}
					else {
						UUID uid2 = Database.getDatabase().getUuid(args[0]);
						if(uid2 == null) {
							player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cNo existe el jugador &4" + args[0])));
							return;
						}
						showHistory(args[0], uid2, player);
					}
	            }
			});
		}
	}
	
	private void showHistory(String playerName, UUID uid, ProxiedPlayer player) {
		LinkedHashMap<Report, Long> reports = Util.sort(Database.getDatabase().getReporterHistory(uid, playerName));
		
		if(reports.size() == 0) {
			player.sendMessage(TextComponent.fromLegacyText(Chat.format("&4" + playerName + " &cnunca ha reportado.")));
			return;
		}
		
		player.sendMessage(TextComponent.fromLegacyText(Chat.format("&eLista de veces que &c" + playerName + " &eha reportado:")));
		
		for(Report report : reports.keySet()) {
			Date datec = new Date(reports.get(report));
		    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		    String date = formatter.format(datec);
		    player.sendMessage(TextComponent.fromLegacyText(Chat.format(" &a" + date + "&7: Reportó a &3" + report.getReported() 
		    	+ " &7razón: &c" + report.getReason())));
		}

		//player.sendMessage(TextComponent.fromLegacyText(Chat.format("&c" + playerName + " &eha reportado un total de &6&l" 
		//		+ reports.size() + " &eveces")));
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