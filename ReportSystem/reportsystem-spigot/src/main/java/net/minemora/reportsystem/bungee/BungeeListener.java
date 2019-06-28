package net.minemora.reportsystem.bungee;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.minemora.reportsystem.ReportSystem;
import net.minemora.reportsystem.packet.PacketGoTo;

public class BungeeListener implements PluginMessageListener {
	
	private static BungeeListener instance;
	
	private Map<String,PacketGoTo> queue = new HashMap<>();
	
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
				System.out.println("goto recieved");
				PacketGoTo goTo = (PacketGoTo)ReportSystem.getGson().fromJson(in.readUTF(), PacketGoTo.class);
				if(Bukkit.getPlayer(goTo.getPlayer()) != null) {
					BungeeHandler.sendGoTo(goTo.getPlayer(), true);
					ReportSystem.performTeleport(goTo, player);
				}
				else {
					queue.put(goTo.getPlayer(), goTo);
					ReportSystem.getPlugin().getQueueAddEvent().onQueueAdd(goTo.getPlayer(), goTo.getTarget());
					BungeeHandler.sendGoTo(goTo.getPlayer(), false);
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
}