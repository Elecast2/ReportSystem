package net.minemora.reportsystem;

import java.util.Set;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.minemora.reportsystem.database.Database;

public final class ReportBanManager {
	
	private static Set<UUID> bannedUuids;
	
	private ReportBanManager() {}
	
	public static void setup() {
		bannedUuids = Database.getDatabase().getBannedUuids();
	}
	
	public static boolean isBan(UUID uid) {
		return bannedUuids.contains(uid) ? true : false;
	}
	
	public static void ban(UUID uid) {
		RedisBungee.getApi().sendChannelMessage("ReportSystem", "Ban:" + uid);
		Database.getDatabase().addBan(uid);
	}
	
	public static void unban(UUID uid) {
		RedisBungee.getApi().sendChannelMessage("ReportSystem", "Unban:" + uid);
		Database.getDatabase().removeBan(uid);
	}

	public static Set<UUID> getBannedUuids() {
		return bannedUuids;
	}

}
