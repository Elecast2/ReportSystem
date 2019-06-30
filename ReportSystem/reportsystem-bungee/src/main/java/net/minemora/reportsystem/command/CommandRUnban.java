package net.minemora.reportsystem.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.minemora.reportsystem.ReportBanManager;
import net.minemora.reportsystem.ReportSystem;
import net.minemora.reportsystem.database.Database;
import net.minemora.reportsystem.util.Chat;

public class CommandRUnban extends Command implements TabExecutor {

	public CommandRUnban() {
		super("reportunban", "mod", "runban");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		if(args.length == 1) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			UUID uid = RedisBungee.getApi().getUuidFromName(args[0]);
			boolean online = true;
			if(uid == null) {
				online = false;
			}
			ServerInfo serverInfo = RedisBungee.getApi().getServerFor(uid);
			if(serverInfo == null) {
				online = false;
			}
			if(online) {
				ReportBanManager.unban(uid);
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("&aHas regresado los permisos de report a &e" + args[0])));
			}
			else {
				ReportSystem.getPlugin().getProxy().getScheduler().runAsync(ReportSystem.getPlugin(), new Runnable() {
					@Override
					public void run() {
						UUID uid2 = Database.getDatabase().getUuid(args[0]);
						if(uid2 == null) {
							player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cNo existe el jugador &4" + args[0])));
							return;
						}
						ReportBanManager.unban(uid2);
						player.sendMessage(TextComponent.fromLegacyText(Chat.format("&aHas regresado los permisos de report a &e" + args[0])));
		            }
				});
			}
			
		}
	}
	
	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return new ArrayList<String>();
		}		
		List<String> result = new ArrayList<>();  
		ProxiedPlayer player = (ProxiedPlayer) sender;
		if(args.length == 1) {
			for(UUID uid : RedisBungee.getApi().getPlayersOnServer(player.getServer().getInfo().getName())) {
				String playerName = RedisBungee.getApi().getNameFromUuid(uid);
				if(!playerName.equals(player.getName())) {
					result.add(playerName);
				}
			}
		}
		else if (args.length == 2) {
			for(UUID uid : RedisBungee.getApi().getPlayersOnServer(player.getServer().getInfo().getName())) {
				String playerName = RedisBungee.getApi().getNameFromUuid(uid);
				if(!playerName.equals(player.getName())) {
					if(playerName.toLowerCase().startsWith(args[1])) {
						result.add(playerName);
					}
				}
			}
		}
		return result;
	}

}
