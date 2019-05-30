package net.minemora.reportsystem;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.minemora.reportsystem.bungee.BungeeListener;
import net.minemora.reportsystem.packet.PacketGoTo;

public class ReportSystemListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(BungeeListener.getInstance().getQueue().containsKey(event.getPlayer().getName())) {
			event.setJoinMessage("");
			PacketGoTo pgt = BungeeListener.getInstance().getQueue().get(event.getPlayer().getName());
			BungeeListener.getInstance().getQueue().remove(event.getPlayer().getName());
			ReportSystem.performTeleport(pgt, event.getPlayer());
		}
		for(String pn : ReportSystem.getSpectators()) {
			if(Bukkit.getPlayer(pn) != null) {
				event.getPlayer().hidePlayer(Bukkit.getPlayer(pn));
			}
		}
	}
}