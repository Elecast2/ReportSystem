package net.minemora.reportsystem.util;

public final class Chat {
	
	private Chat() {}
	
	public static String format(String m) {
		return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', m);
	}

}
