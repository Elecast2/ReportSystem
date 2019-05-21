package net.minemora.reportsystem.bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.minemora.reportsystem.ReportSystem;

public final class BungeeHandler {
	
	private static Plugin plugin;
	
	private BungeeHandler() {}
	
	public static void setup(Plugin pl) {
		plugin = pl;
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "ReportSystem");
	}
	
	public static void sendGoTo(String playerName) {
		sendMessage("GoTo", playerName);
	}
	
	public static void sendMessage(String subChannel, String message) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
			out.writeUTF(subChannel);
			out.writeUTF(message);
			Player player = getPlayer();
			if(player == null) {
				return;
			}
			player.sendPluginMessage(ReportSystem.getPlugin(), "ReportSystem", stream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	private static Player getPlayer() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			return player;
		}
		return null;
	}
}