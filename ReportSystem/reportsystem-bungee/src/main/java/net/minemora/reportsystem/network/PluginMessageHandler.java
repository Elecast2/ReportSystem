package net.minemora.reportsystem.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.minemora.reportsystem.ReportSystem;
import net.minemora.reportsystem.packet.PacketGoTo;

public class PluginMessageHandler implements Listener {
	
	private static Set<String> queue = new HashSet<>();
	
	@EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("ReportSystem")) {
            return;
        }
        if (!(event.getSender() instanceof Server)) {
            return;
        }
        Server server = (Server) event.getSender();
        ByteArrayInputStream stream = new ByteArrayInputStream(event.getData());
        DataInputStream in = new DataInputStream(stream);
        try {
        	String subchannel = in.readUTF();
			if(subchannel.equals("GoTo")) {
				String message = in.readUTF();
				String playerName;
				if(message.contains(":")) {
					String[] splited = message.split(":");
					playerName = splited[0];
				}
				else {
					playerName = message;
					ReportSystem.getPlugin().getProxy().getPlayer(playerName).connect(server.getInfo());
				}
				queue.remove(playerName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendGoTo(String player, String target, boolean vanish) {
		PacketGoTo pgt = new PacketGoTo(player, target, vanish);
		String msg = ReportSystem.getGson().toJson(pgt);
		UUID uid = RedisBungee.getApi().getUuidFromName(target);
		if(uid == null) {
			System.out.println("El objetivo no se encuentra conectado 001");
			return;
		}
		System.out.println(uid);
		ServerInfo serverInfo = RedisBungee.getApi().getServerFor(uid);
		if(serverInfo == null) {
			System.out.println("El objetivo no se encuentra conectado 002");
			return;
		}
		queue.add(player);
		sendMessage("GoTo", msg, serverInfo);
	}
	
	private static void sendMessage(String subchannel, String message, ServerInfo server) {
		if(server.getPlayers().size() == 0) {
			return; //TODO accciones aca
		}
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
        	out.writeUTF(subchannel);
			out.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
        server.sendData("ReportSystem", stream.toByteArray());
	}
}