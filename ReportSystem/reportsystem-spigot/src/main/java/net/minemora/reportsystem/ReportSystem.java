package net.minemora.reportsystem;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

import net.minemora.reportsystem.bungee.BungeeHandler;
import net.minemora.reportsystem.bungee.BungeeListener;
import net.minemora.reportsystem.command.CommandSpy;
import net.minemora.reportsystem.packet.PacketGoTo;
import net.minemora.reportsystem.util.ChatUtils;

public class ReportSystem extends JavaPlugin {
	
	private static ReportSystem plugin;
	
	private static Set<String> spectators = new HashSet<>(); 
	
	private VisibilityManager visibilityManager;
	
	private static final Gson gson;
	
	static {
	    gson = new Gson();
	}
	
	@Override
    public void onEnable() {
		plugin = this;
		BungeeHandler.setup(this);
		BungeeListener.setup(this);
		VaultManager.setup(this);
		getServer().getPluginManager().registerEvents(new ReportSystemListener(), this);
		this.getCommand("spy").setExecutor(new CommandSpy());
		
		//TODO command invsee
		this.visibilityManager = new VisibilityManager() {

			@Override
			public void toggleSpy(Player player, boolean enable) {
				if(enable) {
					player.setGameMode(GameMode.SPECTATOR);
					for(Player lp : Bukkit.getOnlinePlayers()) {
						lp.hidePlayer(player);
					}
				}
				else {
					player.setGameMode(GameMode.ADVENTURE);
					for(Player lp : Bukkit.getOnlinePlayers()) {
						lp.showPlayer(player);
					}
				}
			}
			
		};
	}
	
	public static void performTeleport(PacketGoTo pgt, Player player) {
		Player target = Bukkit.getPlayer(pgt.getTarget());
		if(target == null) {
			return;
		}
		if(pgt.isVanish()) {
			getPlugin().getVisibilityManager().toggleSpy(player, true);
			ReportSystem.getSpectators().add(player.getName());
			player.sendMessage(ChatUtils.format("&aEstas en modo &c&lEspiar")); //TODO LANG
			player.showPlayer(target);
		}
		player.teleport(Bukkit.getPlayer(pgt.getTarget()));
	}
	
	public static Gson getGson() {
		return gson;
	}

	public static ReportSystem getPlugin() {
		return plugin;
	}
	
	public static Set<String> getSpectators() {
		return spectators;
	}

	public VisibilityManager getVisibilityManager() {
		return visibilityManager;
	}

	public void setVisibilityManager(VisibilityManager visibilityManager) {
		this.visibilityManager = visibilityManager;
	}
}