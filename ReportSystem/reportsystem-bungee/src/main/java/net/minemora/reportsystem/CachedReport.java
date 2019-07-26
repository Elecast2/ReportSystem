package net.minemora.reportsystem;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.config.ServerInfo;

public class CachedReport {
	
	private static Map<String, CachedReport> cache = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	
	private Report report;
	private long time;
	private boolean legit = false;
	private String assigned;
	private int reportedTimes = 1;

	public CachedReport(Report report) {
		this.report = report;
		this.time = System.currentTimeMillis();
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isLegit() {
		return legit;
	}

	public void setLegit(boolean legit) {
		this.legit = legit;
	}
	
	public String getAssigned() {
		
		if(assigned == null) {
			return null;
		}
		
		UUID uid = RedisBungee.getApi().getUuidFromName(assigned);
		if(uid == null) {
			assigned = null;
			return null;
		}
		ServerInfo serverInfo = RedisBungee.getApi().getServerFor(uid);
		if(serverInfo == null) {
			assigned = null;
			return null;
		}
		
		UUID uid2 = RedisBungee.getApi().getUuidFromName(report.getReported());
		if(uid2 == null) {
			assigned = null;
			return null;
		}
		ServerInfo serverInfo2 = RedisBungee.getApi().getServerFor(uid2);
		if(serverInfo2 == null) {
			assigned = null;
			return null;
		}
		
		if(!serverInfo.getName().equals(serverInfo2.getName())) {
			assigned = null;
			return null;
		}

		return assigned;
	}

	public void setAssigned(String assigned) {
		this.assigned = assigned;
	}

	public static Map<String, CachedReport> getCache() {
		return cache;
	}

	public int getReportedTimes() {
		return reportedTimes;
	}

	public void setReportedTimes(int reportedTimes) {
		this.reportedTimes = reportedTimes;
	}

}
