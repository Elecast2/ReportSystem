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

}
