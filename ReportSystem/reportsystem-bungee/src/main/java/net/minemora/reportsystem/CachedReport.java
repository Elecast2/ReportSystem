package net.minemora.reportsystem;

import java.util.HashMap;
import java.util.Map;

public class CachedReport {
	
	private static Map<String, CachedReport> cache = new HashMap<>();
	
	private Report report;
	private long time;
	private boolean legit = false;
	private String assigned;

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
		return assigned;
	}

	public void setAssigned(String assigned) {
		this.assigned = assigned;
	}

	public static Map<String, CachedReport> getCache() {
		return cache;
	}

}
