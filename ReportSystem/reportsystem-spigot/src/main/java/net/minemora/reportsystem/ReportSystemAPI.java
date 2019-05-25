package net.minemora.reportsystem;

import net.minemora.reportsystem.bungee.BungeeListener;
import net.minemora.reportsystem.packet.PacketGoTo;

public final class ReportSystemAPI {
	
	private ReportSystemAPI() {}
	
	public static boolean isSpecting(String playerName) {
		if(BungeeListener.getInstance().getQueue().containsKey(playerName)) {
			return true;
		}
		return false;
	}
	
	public static boolean isVisible(String playerName) {
		if(BungeeListener.getInstance().getQueue().containsKey(playerName)) {
			PacketGoTo pgt = BungeeListener.getInstance().getQueue().get(playerName);
			if(!pgt.isVanish()) {
				return true;
			}
		}
		return false;
	}

}
