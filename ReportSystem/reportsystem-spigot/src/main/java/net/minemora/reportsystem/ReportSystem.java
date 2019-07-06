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
import net.minemora.reportsystem.command.CommandClearChat;
import net.minemora.reportsystem.command.CommandInvSee;
import net.minemora.reportsystem.command.CommandPing;
import net.minemora.reportsystem.command.CommandSpy;
import net.minemora.reportsystem.command.PlayerTabCompleter;
import net.minemora.reportsystem.packet.PacketGoTo;
import net.minemora.reportsystem.util.ChatUtils;

public class ReportSystem extends JavaPlugin {
	
	private static ReportSystem plugin;
	
	private static Set<String> spectators = new HashSet<>(); 
	
	private VisibilityManager visibilityManager;
	
	private QueueAddEvent queueAddEvent;
	
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
		this.getCommand("invsee").setExecutor(new CommandInvSee());
		this.getCommand("ping").setExecutor(new CommandPing());
		this.getCommand("invsee").setTabCompleter(new PlayerTabCompleter());
		this.getCommand("ping").setTabCompleter(new PlayerTabCompleter());
		this.getCommand("clearchat").setExecutor(new CommandClearChat());
		
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
					player.setGameMode(GameMode.SURVIVAL);
					for(Player lp : Bukkit.getOnlinePlayers()) {
						lp.showPlayer(player);
					}
				}
			}
			
		};
		
		this.queueAddEvent = new QueueAddEvent() {
			@Override
			public void onQueueAdd(String playerName, String targetName) {
				return;
			}
		};
	}
	
	public static void performTeleport(PacketGoTo pgt, Player player) {
		Player target = null;
		if(pgt.getTarget() != null) {
			target = Bukkit.getPlayer(pgt.getTarget());
		}
		if(pgt.isVanish()) {
			if(!ReportSystemAPI.isSpy(player.getName())) {
				getPlugin().getVisibilityManager().toggleSpy(player, true);
				ReportSystem.getSpectators().add(player.getName());
				player.sendMessage(ChatUtils.format("&aEstas en modo &c&lEspiar")); //TODO LANG
				player.showPlayer(target);
			}
		}
		if(target != null) {
			player.teleport(target);
		}
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

	public QueueAddEvent getQueueAddEvent() {
		return queueAddEvent;
	}

	public void setQueueAddEvent(QueueAddEvent queueAddEvent) {
		this.queueAddEvent = queueAddEvent;
	}
}