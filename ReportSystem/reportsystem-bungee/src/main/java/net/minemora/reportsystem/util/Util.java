package net.minemora.reportsystem.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

public final class Util {
	
	private Util() {}
	
	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sort(Map<K, V> map) {
		return map.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}
	
	public static int getPlayersOnServer(String serverName, String proxyName) {
		int count = 0;
		for(UUID uid : RedisBungee.getApi().getPlayersOnServer(serverName)) {
			if(RedisBungee.getApi().getProxy(uid).equals(proxyName)) {
				count++;
			}
		}
		return count;
	}

}
