package net.minemora.reportsystem;

import com.google.gson.Gson;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;

public class Report {
	
	private String player;
	private String reported;
	private String reason;
	
	private static final Gson gson;
	
	static {
	    gson = new Gson();
	}
	
	public Report(String player, String reported, String reason) {
		this.player = player;
		this.reported = reported;
		this.reason = reason;
	}
	
	public void send() {
		RedisBungee.getApi().sendChannelMessage("ReportSystem", gson.toJson(this));
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
	
	public static Gson getGson() {
		return gson;
	}

}
