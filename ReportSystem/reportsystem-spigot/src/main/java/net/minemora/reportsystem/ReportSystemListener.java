package net.minemora.reportsystem;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.reportsystem.alt.AltManager;
import net.minemora.reportsystem.bungee.BungeeListener;
import net.minemora.reportsystem.packet.PacketGoTo;

public class ReportSystemListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoinLow(PlayerJoinEvent event) {
		if(BungeeListener.getInstance().getGlobalSpy().contains(event.getPlayer().getUniqueId())) {
			BungeeListener.getInstance().getQueue().put(event.getPlayer().getName(), new PacketGoTo(event.getPlayer().getName(), null, true));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		for(String pn : ReportSystem.getSpectators()) {
			if(Bukkit.getPlayer(pn) != null) {
				event.getPlayer().hidePlayer(Bukkit.getPlayer(pn));
			}
		}
		if(BungeeListener.getInstance().getQueue().containsKey(event.getPlayer().getName())) {
			event.setJoinMessage("");
		}
		if(ReportSystemAPI.isProcessQueueOnJoin()) {
			ReportSystemAPI.processQueue(event.getPlayer());
		}
		if(!AltManager.getInstance().getAltPlayers().isEmpty()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if(!event.getPlayer().isOnline()) {
						return;
					}
					AltManager.getInstance().updateAltPlayers(event.getPlayer());
				}
			}.runTaskLaterAsynchronously(ReportSystem.getPlugin(), 30);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		ReportSystem.getSpectators().remove(event.getPlayer().getName());
		AltManager.getInstance().getAltPlayers().remove(event.getPlayer().getUniqueId());
	}
}