package net.minemora.reportsystem.bungee;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.minemora.reportsystem.ReportSystem;
import net.minemora.reportsystem.ReportSystemAPI;
import net.minemora.reportsystem.command.CommandSpy;
import net.minemora.reportsystem.packet.PacketGoTo;

public class BungeeListener implements PluginMessageListener {
	
	private static BungeeListener instance;
	
	private Map<String,PacketGoTo> queue = new HashMap<>();
	
	private Set<UUID> globalSpy = new HashSet<>();
	
	private BungeeListener() {}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("ReportSystem")) {
			  return;
		}
		try {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();
			if(subchannel.equals("GoTo")) {
				PacketGoTo goTo = (PacketGoTo)ReportSystem.getGson().fromJson(in.readUTF(), PacketGoTo.class);
				System.out.println("goto recieved: " + goTo.getPlayer() + " -> " + (goTo.getTarget() == null ? "null" : goTo.getTarget()));
				if(Bukkit.getPlayer(goTo.getPlayer()) != null) {
					BungeeHandler.sendGoTo(goTo.getPlayer(), true);
					ReportSystem.performTeleport(goTo, Bukkit.getPlayer(goTo.getPlayer()));
				}
				else {
					queue.put(goTo.getPlayer(), goTo);
					ReportSystem.getPlugin().getQueueAddEvent().onQueueAdd(goTo.getPlayer(), goTo.getTarget());
					BungeeHandler.sendGoTo(goTo.getPlayer(), false);
				}
			}
			else if(subchannel.equals("GlobalSpy")) {
				String[] splited = in.readUTF().split(":");
				UUID uid = UUID.fromString(splited[0]);
				if(splited[1].equals("add")) {
					globalSpy.add(uid);
					if(Bukkit.getPlayer(uid) != null) {
						if(!ReportSystemAPI.isSpy(Bukkit.getPlayer(uid).getName())) {
							CommandSpy.set(Bukkit.getPlayer(uid), true);
						}
					}
				}
				else if(splited[1].equals("remove")) {
					globalSpy.remove(uid);
					if(Bukkit.getPlayer(uid) != null) {
						if(ReportSystemAPI.isSpy(Bukkit.getPlayer(uid).getName())) {
							CommandSpy.set(Bukkit.getPlayer(uid), false);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setup(Plugin plugin) {
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "ReportSystem", getInstance());
	}

	public static BungeeListener getInstance() {
		if(instance == null) {
			instance = new BungeeListener();
		}
		return instance;
	}

	public Map<String,PacketGoTo> getQueue() {
		return queue;
	}
	
	public Set<UUID> getGlobalSpy() {
		return globalSpy;
	}
}