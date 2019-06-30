package net.minemora.reportsystem.command;

import net.md_5.bungee.api.plugin.Plugin;

public final class CommandManager {
	
	private CommandManager() {}
	
	public static void registerCommands(Plugin plugin) {
		plugin.getProxy().getPluginManager().registerCommand(plugin, new CommandReport());
		plugin.getProxy().getPluginManager().registerCommand(plugin, new CommandGoTo());
		plugin.getProxy().getPluginManager().registerCommand(plugin, new CommandChatStaff());
		plugin.getProxy().getPluginManager().registerCommand(plugin, new CommandStaffList());
		plugin.getProxy().getPluginManager().registerCommand(plugin, new CommandRBan());
		plugin.getProxy().getPluginManager().registerCommand(plugin, new CommandRUnban());
		plugin.getProxy().getPluginManager().registerCommand(plugin, new CommandRRHistory());
		plugin.getProxy().getPluginManager().registerCommand(plugin, new CommandREHistory());
	}
}
