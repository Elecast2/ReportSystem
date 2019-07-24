package net.minemora.reportsystem.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.minemora.reportsystem.ReportSystem;
import net.minemora.reportsystem.packet.PacketGoTo;
import net.minemora.reportsystem.util.Util;

public class PluginMessageHandler implements Listener {
	
	private static Set<String> queue = new HashSet<>();
	
	@EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("legacy:reportsystem")) {
            return;
        }
        if (!(event.getSender() instanceof Server)) {
        	System.out.println("Message recieved from spigot but sender not instance of Server!");
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
					if(splited[1].equals("online")) {
						return;
					}
				}
				else {
					playerName = message;
				}
				RedisBungee.getApi().sendChannelMessage("ReportSystem", "GoTo:" + playerName + ":" + server.getInfo().getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendGlobalSpy(UUID uid, boolean add) {
		for(ServerInfo serverInfo : ReportSystem.getPlugin().getProxy().getServers().values()) {
			sendMessage("GlobalSpy", uid + ":" + (add ? "add" : "remove"), serverInfo);
		}
	}
	
	public static void sendGoTo(String player, String target, boolean vanish) {
		PacketGoTo pgt = new PacketGoTo(player, target, vanish);
		String msg = ReportSystem.getGson().toJson(pgt);
		UUID uid = RedisBungee.getApi().getUuidFromName(target);
		if(uid == null) {
			System.out.println("El objetivo no se encuentra conectado 001" + target);
			return;
		}
		System.out.println(uid);
		ServerInfo serverInfo = RedisBungee.getApi().getServerFor(uid);
		if(serverInfo == null) {
			System.out.println("El objetivo no se encuentra conectado 002 " + target);
			return;
		}
		queue.add(player);
		if(RedisBungee.getApi().getPlayersOnServer(serverInfo.getName()).size() == 0) {
			System.out.println("No target GoTo sended because players on server = 0");
			RedisBungee.getApi().sendChannelMessage("ReportSystem", "GoTo:" + player + ":" + serverInfo.getName());
			return;
		}
		if(serverInfo.getPlayers().size() == 0) {
			String targetProxy = null;
			for(String proxyName : RedisBungee.getApi().getAllServers()) {
				if(!proxyName.equals(RedisBungee.getApi().getServerId())) {
					if(Util.getPlayersOnServer(serverInfo.getName(), proxyName) > 0) {
						targetProxy = proxyName;
						break;
					}
				}
			}
			if(targetProxy != null) {
				RedisBungee.getApi().sendChannelMessage("ReportSystem", "SendGoTo:" + targetProxy + ":" + serverInfo.getName() + ":" + msg);
				System.out.println("Global GoTo sended because players on server-proxy = 0");
			}
			else {
				System.out.println("targetProxy null on SendGoTo target");
			}
			return;
		}
		sendMessage("GoTo", msg, serverInfo);
		System.out.println("Sending message on same proxy to server: " + serverInfo.getName());
	}
	
	public static void sendReport(ProxiedPlayer player) {
		sendMessage("Report", player.getName(), player.getServer().getInfo());
	}
	
	public static void sendMessage(String subchannel, String message, ServerInfo server) {
		if(RedisBungee.getApi().getPlayersOnServer(server.getName()).size() == 0) {
			System.out.println("players 0 on sending message to server to server");
			return;
		}
		ReportSystem.getPlugin().getProxy().getScheduler().runAsync(ReportSystem.getPlugin(), new Runnable() {
            @Override
            public void run() {
            	ByteArrayDataOutput out = ByteStreams.newDataOutput();
            	out.writeUTF(subchannel);
        		out.writeUTF(message);
                server.sendData("legacy:reportsystem", out.toByteArray());
            }
		});
		
	}
	
	public static Set<String> getQueue() {
		return queue;
	}
}