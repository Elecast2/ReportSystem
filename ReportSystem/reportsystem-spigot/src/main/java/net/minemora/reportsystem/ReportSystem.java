package net.minemora.reportsystem;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

import net.minemora.reportsystem.bungee.BungeeHandler;
import net.minemora.reportsystem.bungee.BungeeListener;
import net.minemora.reportsystem.packet.PacketGoTo;

public class ReportSystem extends JavaPlugin {
	
	private static ReportSystem plugin;
	
	private static final Gson gson;
	
	static {
	    gson = new Gson();
	}
	
	@Override
    public void onEnable() {
		plugin = this;
		BungeeHandler.setup(this);
		BungeeListener.setup(this);
		getServer().getPluginManager().registerEvents(new ReportSystemListener(), this);
	}
	
	public static void performTeleport(PacketGoTo pgt, Player player) {
		Player target = Bukkit.getPlayer(pgt.getTarget());
		if(target == null) {
			return;
		}
		if(pgt.isVanish()) {
			player.setGameMode(GameMode.SPECTATOR);
			player.showPlayer(target);
			for(Player lp : Bukkit.getOnlinePlayers()) {
				lp.hidePlayer(player);
			}
		}
		player.teleport(Bukkit.getPlayer(pgt.getTarget()));
	}
	
	public static Gson getGson() {
		return gson;
	}

	public static ReportSystem getPlugin() {
		return plugin;
	}
}