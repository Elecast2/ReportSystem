package net.minemora.reportsystem;

import org.bukkit.plugin.java.JavaPlugin;

import net.minemora.reportsystem.bungee.BungeeHandler;
import net.minemora.reportsystem.bungee.BungeeListener;

public class ReportSystem extends JavaPlugin {
	
	@Override
    public void onEnable() {
		BungeeHandler.setup(this);
		BungeeListener.setup(this);
	}
}