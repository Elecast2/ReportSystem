package net.minemora.reportsystem.command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.minemora.reportsystem.ReportSystem;
import net.minemora.reportsystem.util.Chat;

public class CommandStaffList extends Command {

	public static Map<String, Map<String, Set<String>>> currentCache;
	public static Set<String> queuePlayers = new HashSet<>();
	public static boolean requesting = false;
	private long lastUsage;
	
	public CommandStaffList() {
		super("stafflist", "staff.list", "slist");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		if(requesting) {
			if((System.currentTimeMillis() - lastUsage) < 5000) {
				queuePlayers.add(((ProxiedPlayer)sender).getName());
				return;
			}
		}
		requesting = true;
		lastUsage = System.currentTimeMillis();
		queuePlayers.add(((ProxiedPlayer)sender).getName());
		currentCache = new HashMap<>();
		RedisBungee.getApi().sendChannelMessage("ReportSystem", "StaffListRequest:" + RedisBungee.getApi().getServerId());
	}
	
	public static void addProxyInfo(String proxyId, Map<String, Set<String>> staffList) {
		currentCache.put(proxyId, staffList);
		if(currentCache.size() == RedisBungee.getApi().getAllServers().size()) {
			
			Map<String, Set<String>> result = new HashMap<>();
			
			for(Map<String, Set<String>> proxyInfo : currentCache.values()) {
				for(String serverName : proxyInfo.keySet()) {
					if(result.containsKey(serverName)) {
						result.get(serverName).addAll(proxyInfo.get(serverName));
					}
					else {
						result.put(serverName, proxyInfo.get(serverName));
					}
				}
			}
			
			BaseComponent[] header = TextComponent.fromLegacyText(Chat.format("&b&l&m-------------&9&l[&f&lSTAFF ONLINE&9&l]&b&l&m-------------"));
			BaseComponent[] footer = TextComponent.fromLegacyText(Chat.format("&b&l&m---------------------------------------"));
			
			for(String playerName : queuePlayers) {
				if(ReportSystem.getPlugin().getProxy().getPlayer(playerName) == null) {
					continue;
				}
				ProxiedPlayer player = ReportSystem.getPlugin().getProxy().getPlayer(playerName);
				player.sendMessage(header);
				for(String serverName : result.keySet()) {
					String connected = String.join(", ", result.get(serverName)); 
					BaseComponent[] serverConnected = TextComponent.fromLegacyText(Chat.format(" &9[&3" + serverName + "&9]: &f" + connected));
					player.sendMessage(serverConnected);
				}
				player.sendMessage(footer);
			}
			
			queuePlayers.clear();
			requesting = false;

		}
	}

}
