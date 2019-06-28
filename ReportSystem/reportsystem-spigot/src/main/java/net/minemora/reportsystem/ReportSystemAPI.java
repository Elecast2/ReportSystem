package net.minemora.reportsystem;

import org.bukkit.entity.Player;

import net.minemora.reportsystem.bungee.BungeeListener;
import net.minemora.reportsystem.packet.PacketGoTo;

public final class ReportSystemAPI {
	
	private static boolean processQueueOnJoin = true;
	
	private ReportSystemAPI() {}
	
	public static boolean isInQueue(String playerName) {
		if(BungeeListener.getInstance().getQueue().containsKey(playerName)) {
			return true;
		}
		return false;
	}
	
	public static boolean isQueueVisible(String playerName) {
		if(BungeeListener.getInstance().getQueue().containsKey(playerName)) {
			PacketGoTo pgt = BungeeListener.getInstance().getQueue().get(playerName);
			if(!pgt.isVanish()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isSpy(String playerName) {
		return ReportSystem.getSpectators().contains(playerName);
	}
	
	public static void setVisibilityManager(VisibilityManager vmanager) {
		ReportSystem.getPlugin().setVisibilityManager(vmanager);
	}
	
	public static void setQueueAddEvent(QueueAddEvent qaevent) {
		ReportSystem.getPlugin().setQueueAddEvent(qaevent);
	}
	
	public static boolean processQueue(Player player) {
		if(BungeeListener.getInstance().getQueue().containsKey(player.getName())) {
			PacketGoTo pgt = BungeeListener.getInstance().getQueue().get(player.getName());
			BungeeListener.getInstance().getQueue().remove(player.getName());
			ReportSystem.performTeleport(pgt, player);
			return true;
		}
		return false;
	}

	public static boolean isProcessQueueOnJoin() {
		return processQueueOnJoin;
	}

	public static void setProcessQueueOnJoin(boolean processQueueOnJoin) {
		ReportSystemAPI.processQueueOnJoin = processQueueOnJoin;
	}

}
