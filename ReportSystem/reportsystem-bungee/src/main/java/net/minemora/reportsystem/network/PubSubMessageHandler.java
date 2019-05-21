package net.minemora.reportsystem.network;

import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.minemora.reportsystem.Report;
import net.minemora.reportsystem.ReportSystem;

public class PubSubMessageHandler implements Listener {
	
	@EventHandler
	public void onPubSubMessage(PubSubMessageEvent event) {
		if (!event.getChannel().equals("ReportSystem")) {
			return;
		}
		Report report = (Report)ReportSystem.getGson().fromJson(event.getMessage(), Report.class);
		UUID uid = RedisBungee.getApi().getUuidFromName(report.getReported());
		if(uid == null) {
			System.out.println("El reportado no se encuentra conectado 001");
			return;
		}
		ServerInfo serverInfo = RedisBungee.getApi().getServerFor(uid);
		if(serverInfo == null) {
			System.out.println("El reportado no se encuentra conectado 002");
			return;
		}
		for(ProxiedPlayer player : ReportSystem.getPlugin().getProxy().getPlayers()) {
			if(player.hasPermission("staff")) {
				player.sendMessage(new TextComponent("[Mensaje de prueba] " + report.getReported() + " ha sido reportado por " 
						+ report.getPlayer() + ", razón: " + report.getReason() + ", servidor: " + serverInfo.getName()));
			}
		}
	}

}
