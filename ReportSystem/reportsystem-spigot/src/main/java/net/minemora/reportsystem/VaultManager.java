package net.minemora.reportsystem;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;

public final class VaultManager {
	
	private static boolean enabled;
	
	private static Permission perms = null;
	
	private static String defaultGroup = "Usuario"; //TODO CONFIG y setear en setup()
	
	private VaultManager() {}
	
	public static void setup(Plugin plugin) {
		enabled = true;
		if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
			if(enabled) {
				plugin.getLogger().severe("Vault not found! disabling plugin...");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			}
            return;
        }
		setupPermissions(plugin);
	}
	
	public static boolean hasPermission(Player player, String permission) {
		if(enabled) {
			return getPermissions().has(player, permission);
		}
		else {
			return player.hasPermission(permission);
		}
	}
	
	public static String[] getGroups() {
		return getPermissions().getGroups();
	}
	
	public static String getDefaultGroup() {
		return defaultGroup;
	}
	
	private static boolean setupPermissions(Plugin plugin) {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
	
	
	public static Permission getPermissions() {
        return perms;
    }

	public static boolean isEnabled() {
		return enabled;
	}

}
