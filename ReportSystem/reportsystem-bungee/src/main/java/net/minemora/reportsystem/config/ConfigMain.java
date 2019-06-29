package net.minemora.reportsystem.config;

import net.md_5.bungee.config.Configuration;

public final class ConfigMain extends Config {
	
	private static ConfigMain instance;

	private ConfigMain() {
		super("config.yml");
	}

	@Override
	public void load(boolean firstCreate) {
		return;
	}
	
	@Override
	public void update() {
		return;
	}
	
	public static Configuration get() {
		return getInstance().config;
	}

	public static ConfigMain getInstance() {
		if (instance == null) {
            instance = new ConfigMain();
        }
        return instance;
	}
}