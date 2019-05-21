package net.minemora.reportsystem;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

import net.minemora.reportsystem.bungee.BungeeHandler;
import net.minemora.reportsystem.bungee.BungeeListener;

public class ReportSystem extends JavaPlugin {
	
	private static ReportSystem plugin;
	
	private static final Gson gson;
	
	static {
	    gson = new Gson();
	}
	
	@Override
    public void onEnable() {
		plugin = this;
		BungeeHandler.setup(this);
		BungeeListener.setup(this);
		getServer().getPluginManager().registerEvents(new ReportSystemListener(), this);
	}
	
	public static Gson getGson() {
		return gson;
	}

	public static ReportSystem getPlugin() {
		return plugin;
	}
}