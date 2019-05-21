package net.minemora.reportsystem.bungee;

import java.util.HashMap;
import java.util.Map;

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
				PacketGoTo goTo = (PacketGoTo)ReportSystem.getGson().fromJson(in.readUTF(), PacketGoTo.class);
				queue.put(goTo.getPlayer(), goTo);
				BungeeHandler.sendGoTo(goTo.getPlayer());
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