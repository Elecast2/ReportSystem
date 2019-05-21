package net.minemora.reportsystem.bungee;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public final class BungeeHandler {
	
	private static Plugin plugin;
	
	private BungeeHandler() {}
	
	public static void setup(Plugin pl) {
		plugin = pl;
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
	}
	
	public static void sendReport(Player player, String reported, String reason) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ReportSystem");
		out.writeUTF(reported);
		out.writeUTF(reason);
		player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
	}
}