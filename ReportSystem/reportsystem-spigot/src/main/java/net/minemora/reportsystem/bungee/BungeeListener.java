package net.minemora.reportsystem.bungee;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class BungeeListener implements PluginMessageListener {
	
	private static BungeeListener instance;
	
	private String channel;
	
	private BungeeListener() {}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals(channel)) {
			  return;
		}
		try {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();
			
			if (subchannel.equals("PlayerCount")) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setup(Plugin plugin) {
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", getInstance());
	}

	public static BungeeListener getInstance() {
		if(instance == null) {
			instance = new BungeeListener();
		}
		return instance;
	}

	public String getChannel() {
		return channel;
	}

}
