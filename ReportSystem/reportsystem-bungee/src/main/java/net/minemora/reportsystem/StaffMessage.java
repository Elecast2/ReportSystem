package net.minemora.reportsystem;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

public class StaffMessage {
	
	private String sender;
	private String message;
	
	public StaffMessage(String sender, String message) {
		this.sender = sender;
		this.message = message;
	}
	
	public void send() {
		RedisBungee.getApi().sendChannelMessage("ReportSystem", "StaffChat:" + ReportSystem.getGson().toJson(this));
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
