package net.minemora.reportsystem;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.minemora.reportsystem.command.CommandManager;

public class ReportSystem extends Plugin implements Listener {
	
	@Override
    public void onEnable() {
		CommandManager.registerCommands(this);
		RedisBungee.getApi().registerPubSubChannels("ReportSystem");
		getProxy().getPluginManager().registerListener(this, this);
	}
	
	@EventHandler
	public void onPubSubMessage(PubSubMessageEvent event) {
		if (!event.getChannel().equals("ReportSystem")) {
			return;
		}
		Report report = (Report)Report.getGson().fromJson(event.getMessage(), Report.class);
		for(ProxiedPlayer player : getProxy().getPlayers()) {
			if(player.hasPermission("staff")) {
				player.sendMessage(new TextComponent("[Mensaje de prueba] " + report.getReported() + " ha sido reportado por " 
						+ report.getPlayer() + ", razón: " + report.getReason()));
			}
		}
	}
}