package net.minemora.reportsystem.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.io.ByteStreams;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.minemora.reportsystem.ReportSystem;


public abstract class Config {
	
	protected File pdfile;
	protected Configuration config;
	protected String fileName;
	
	protected Config(String fileName) {
		this.fileName = fileName;
	}
	
	public void setup() {
		if (!ReportSystem.getPlugin().getDataFolder().exists()) {
			ReportSystem.getPlugin().getDataFolder().mkdir();
		}
		pdfile = new File(ReportSystem.getPlugin().getDataFolder(), fileName);
		boolean firstCreate = false;
		if (!pdfile.exists()) {
			firstCreate = true;
			try {
				pdfile.createNewFile();
				try (InputStream is = ReportSystem.getPlugin().getResourceAsStream(fileName);
						OutputStream os = new FileOutputStream(pdfile)) {
					ByteStreams.copy(is, os);
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to create the file: " + fileName, e);
			}
		}
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(pdfile);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		load(firstCreate);
		update();
	}
	
	public abstract void load(boolean firstCreate);
	
	public abstract void update();

	public void save() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, 
					new File(ReportSystem.getPlugin().getDataFolder(), fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public void reload() {
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(pdfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Configuration getConfig() {
		return config;
	}

	public String getFileName() {
		return fileName;
	}
}