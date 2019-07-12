package net.minemora.reportsystem;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.minemora.reportsystem.command.CommandReport;
import net.minemora.reportsystem.command.CommandStaffList;

public class ReportSystemListener implements Listener {
	
	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		CommandStaffList.queuePlayers.remove(event.getPlayer().getName());
		CommandReport.cooldown.remove(event.getPlayer().getName());
	}
	
	/*
	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		if(CommandGlobalSpy.getGlobalSpy().contains(event.getPlayer().getUniqueId())) {
			if(CommandGlobalSpy.getQueue().containsKey(event.getPlayer().getUniqueId())) {
				if(CommandGlobalSpy.getQueue().get(event.getPlayer().getUniqueId()).equals(event.getTarget().getName())) {
					CommandGlobalSpy.getQueue().remove(event.getPlayer().getUniqueId());
					return;
				}
			}
			if(event.getPlayer().getServer() == null) {
				return;
			}
			event.setCancelled(true);
			CommandGlobalSpy.getQueue().put(event.getPlayer().getUniqueId(), event.getTarget().getName());
			PluginMessageHandler.sendGoTo(event.getPlayer().getName(), event.getTarget(), true);
		}
	}
	*/

}
