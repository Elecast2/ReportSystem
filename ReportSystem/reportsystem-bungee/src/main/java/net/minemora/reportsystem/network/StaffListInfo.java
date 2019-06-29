package net.minemora.reportsystem.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.minemora.reportsystem.ReportSystem;

public class StaffListInfo extends RequestPacket {
	
	private Map<String, Set<String>> staffList = new HashMap<>();

	public StaffListInfo(String origin, String destiny, Map<String, Set<String>> staffList) {
		super(origin, destiny);
		this.staffList = staffList;
	}

	public void send() {
		RedisBungee.getApi().sendChannelMessage("ReportSystem", "StaffListInfo:" + ReportSystem.getGson().toJson(this));
	}

	public Map<String, Set<String>> getStaffList() {
		return staffList;
	}

	public void setStaffList(Map<String, Set<String>> staffList) {
		this.staffList = staffList;
	}

}
