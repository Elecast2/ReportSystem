package net.minemora.reportsystem.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.minemora.reportsystem.Report;
import net.minemora.reportsystem.ReportBanManager;
import net.minemora.reportsystem.ReportSystem;
import net.minemora.reportsystem.StaffMessage;
import net.minemora.reportsystem.command.CommandGlobalSpy;
import net.minemora.reportsystem.command.CommandStaffList;
import net.minemora.reportsystem.command.CommandToggleReports;
import net.minemora.reportsystem.util.Chat;

public class PubSubMessageHandler implements Listener {
	
	@EventHandler
	public void onPubSubMessage(PubSubMessageEvent event) {
		if (!event.getChannel().equals("ReportSystem")) {
			return;
		}
		if(!event.getMessage().contains(":")) {
			return;
		}
		String[] splited = event.getMessage().split(":");
		
		if(splited[0].equals("Report")) {
			Report report = (Report)ReportSystem.getGson().fromJson(event.getMessage().split(":",2)[1], Report.class);
			UUID uid = RedisBungee.getApi().getUuidFromName(report.getReported());
			if(uid == null) {
				System.out.println("El reportado no se encuentra conectado 001");
				return;
			}
			ServerInfo serverInfo = RedisBungee.getApi().getServerFor(uid);
			if(serverInfo == null) {
				System.out.println("El reportado no se encuentra conectado 002");
				return;
			}
			
			Report.getReportedCache().add(uid);
			
			BaseComponent[] header = TextComponent.fromLegacyText(Chat.format("&6&l&m---------------&f&l[&c&lREPORTE&f&l]&6&l&m---------------"));
			BaseComponent[] nick = TextComponent.fromLegacyText(Chat.format(" &e&lNick: &a" + report.getPlayer()));
			BaseComponent[] reported = TextComponent.fromLegacyText(Chat.format(" &6&lReportado: &c" + report.getReported()));
			BaseComponent[] server = TextComponent.fromLegacyText(Chat.format(" &e&lServidor: &a" + serverInfo.getName()));
			BaseComponent[] reason = TextComponent.fromLegacyText(Chat.format(" &6&lRazón: &c") + report.getReason());
			BaseComponent[] footer = TextComponent.fromLegacyText(Chat.format("&6&l&m---------------------------------------"));
			
			TextComponent click = new TextComponent(" Click para ir -> ");
			click.setColor(ChatColor.GRAY);
			TextComponent visible = new TextComponent("VISIBLE");
			visible.setColor(ChatColor.GREEN);
			visible.setBold(true);
			visible.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/goto " + report.getReported()));
			TextComponent hidden = new TextComponent("OCULTO");
			hidden.setColor(ChatColor.RED);
			hidden.setBold(true);
			hidden.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/goto -v " + report.getReported()));
			click.addExtra(visible);
			click.addExtra(new TextComponent("  "));
			click.addExtra(hidden);
			
			
			for(ProxiedPlayer player : ReportSystem.getPlugin().getProxy().getPlayers()) {
				if(player.hasPermission("staff")) {
					if(CommandToggleReports.getHideReports().contains(player.getUniqueId())) {
						continue;
					}
					player.sendMessage(header);
					player.sendMessage(nick);
					player.sendMessage(reported);
					player.sendMessage(server);
					player.sendMessage(reason);
					player.sendMessage(new TextComponent(""));
					player.sendMessage(click);
					player.sendMessage(footer);
				}
			}
		}
		else if(splited[0].equals("StaffChat")) {
			StaffMessage smessage = (StaffMessage)ReportSystem.getGson().fromJson(event.getMessage().split(":",2)[1], StaffMessage.class);
			UUID uid = RedisBungee.getApi().getUuidFromName(smessage.getSender());
			if(uid == null) {
				System.out.println("El mensajero no se encuentra conectado 001");
				return;
			}
			ServerInfo serverInfo = RedisBungee.getApi().getServerFor(uid);
			if(serverInfo == null) {
				System.out.println("El mensajero no se encuentra conectado 002");
				return;
			}
			BaseComponent[] message = TextComponent.fromLegacyText(Chat
					.format("&1&lSM &9[&3" + serverInfo.getName() + "&9] &f" + smessage.getSender() + " &1&l> &b" + smessage.getMessage()));
			for(ProxiedPlayer player : ReportSystem.getPlugin().getProxy().getPlayers()) {
				if(player.hasPermission("staff.chat")) {
					player.sendMessage(message);
				}
			}
		}
		else if(splited[0].equals("StaffListRequest")) {
			String origin = splited[1];
			Map<String, Set<String>> staffList = new HashMap<>();
			for(ProxiedPlayer player : ReportSystem.getPlugin().getProxy().getPlayers()) {
				if(player.hasPermission("staff.list")) {
					String serverName = player.getServer().getInfo().getName();
					if(!staffList.containsKey(serverName)) {
						staffList.put(serverName, new HashSet<>());
					}
					staffList.get(serverName).add(player.getName());
				}
			}
			new StaffListInfo(RedisBungee.getApi().getServerId(), origin, staffList).send();
		}
		else if(splited[0].equals("StaffListInfo")) {
			StaffListInfo slinfo = (StaffListInfo)ReportSystem.getGson().fromJson(event.getMessage().split(":",2)[1], StaffListInfo.class);
			if(!RedisBungee.getApi().getServerId().equals(slinfo.getDestiny())) {
				return;
			}
			CommandStaffList.addProxyInfo(slinfo.getOrigin(), slinfo.getStaffList());
		}
		else if(splited[0].equals("GoTo")) {
			String playerName = splited[1];
			if(!PluginMessageHandler.getQueue().contains(playerName)) {
				System.out.println("GoTo recieved but " + playerName + " is not in queue");
				return;
			}
			String serverName = splited[2];
			if(ReportSystem.getPlugin().getProxy().getPlayer(playerName) != null) {
				ServerInfo server = ReportSystem.getPlugin().getProxy().getServerInfo(serverName);
				ReportSystem.getPlugin().getProxy().getPlayer(playerName).connect(server);
			}
			else {
				System.out.println("GoTo recieved but " + playerName + " is null");
			}
			PluginMessageHandler.getQueue().remove(playerName);
		}
		else if(splited[0].equals("SendGoTo")) {
			String targetProxy = splited[1];
			if(!RedisBungee.getApi().getServerId().equals(targetProxy)) {
				return;
			}
			String serverName = splited[2];
			String msg = event.getMessage().split(":",4)[3];
			PluginMessageHandler.sendMessage("GoTo", msg, ReportSystem.getPlugin().getProxy().getServerInfo(serverName));
		}
		else if(splited[0].equals("GlobalSpy")) {
			if(splited[1].equals("add")) {
				CommandGlobalSpy.getGlobalSpy().add(UUID.fromString(splited[2]));
			}
			else if(splited[1].equals("remove")) {
				CommandGlobalSpy.getGlobalSpy().remove(UUID.fromString(splited[2]));
			}
		}
		else if(splited[0].equals("ToggleReports")) {
			if(splited[1].equals("add")) {
				CommandToggleReports.getHideReports().add(UUID.fromString(splited[2]));
			}
			else if(splited[1].equals("remove")) {
				CommandToggleReports.getHideReports().remove(UUID.fromString(splited[2]));
			}
		}
		else if(splited[0].equals("Ban")) {
			ReportBanManager.getBannedUuids().add(UUID.fromString(splited[1]));
		}
		else if(splited[0].equals("Unban")) {
			ReportBanManager.getBannedUuids().remove(UUID.fromString(splited[1]));
		}
	}
}
