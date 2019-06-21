package net.minemora.reportsystem.util;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;

public class ChatUtils {
	
	public static String format(String m) {
		return ChatColor.translateAlternateColorCodes('&', m);
	}

	public static String[] format(String[] m) {
		String[] result;
		result = new String[m.length];
		for (int i = 0; i < m.length; i++) {
			result[i] = ChatColor.translateAlternateColorCodes('&', m[i]);
		}
		return result;
	}

	public static String[] format(List<String> m) {
		String[] result;
		result = new String[m.size()];
		for (int i = 0; i < m.size(); i++) {
			result[i] = ChatColor.translateAlternateColorCodes('&', m.get(i));
		}
		return result;
	}
	
	public static List<String> formatList(List<String> m) {
		return m.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
	}

}
