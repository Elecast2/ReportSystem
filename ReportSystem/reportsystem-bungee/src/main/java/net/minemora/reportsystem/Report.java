package net.minemora.reportsystem;

import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.minemora.reportsystem.database.Database;

public class Report {
	
	private String player;
	private String reported;
	private String reason;
	
	public Report(String player, String reported, String reason) {
		this.player = player;
		this.reported = reported;
		this.reason = reason;
	}
	
	public void send() {
		RedisBungee.getApi().sendChannelMessage("ReportSystem", "Report:" + ReportSystem.getGson().toJson(this));
		UUID reporterUuid = RedisBungee.getApi().getUuidFromName(player);
		UUID reportedUuid = RedisBungee.getApi().getUuidFromName(reported);
		if(reporterUuid != null && reportedUuid != null) {
			Database.getDatabase().addReport(reporterUuid, reportedUuid, player, reported, reason);
		}
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getReported() {
		return reported;
	}

	public void setReported(String reported) {
		this.reported = reported;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
