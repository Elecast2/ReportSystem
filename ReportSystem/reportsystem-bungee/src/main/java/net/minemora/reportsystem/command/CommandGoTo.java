package net.minemora.reportsystem.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.minemora.reportsystem.CachedReport;
import net.minemora.reportsystem.network.PluginMessageHandler;
import net.minemora.reportsystem.util.Chat;

public class CommandGoTo extends Command {
	
	private static Map<String, String> lastGoTo = new HashMap<>();

	public CommandGoTo() {
		super("goto", "staff", "gt");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		if(args.length > 2 || args.length == 0) {
			return; //TODO correct usage
		}
		ProxiedPlayer player = (ProxiedPlayer) sender;
		String targetName;
		if(args[0].startsWith("-")) {
			targetName = args[1];
		}
		else {
			targetName = args[0];
		}
		UUID uid = RedisBungee.getApi().getUuidFromName(targetName);
		if(uid == null) {
			player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cEl jugador al que quieres ir no se encuentra conectado")));
			return;
		}
		ServerInfo serverInfo = RedisBungee.getApi().getServerFor(uid);
		if(serverInfo == null) {
			player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cEl jugador al que quieres ir no se encuentra conectado")));
			return;
		}
		if(args[0].startsWith("-")) {
			if(args.length == 1) {
				return; //TODO correct usage
			}
			if(args[0].equals("-v")) {
				if(CachedReport.getCache().containsKey(targetName)) {
					String assigned = CachedReport.getCache().get(targetName).getAssigned();
					if(assigned != null) {
						if(!player.getName().equals(assigned)) {
							player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cEl staff &b" + assigned 
									+ " &cya se encuentra revisando a &4" + targetName + " &e si aun asi deseas ir usa &a/goto -i " + targetName)));
							return;
						}
					}
				}
				PluginMessageHandler.sendGoTo(player.getName(), targetName, true);
				RedisBungee.getApi().sendChannelMessage("ReportSystem", "Assign:" + player.getName() + ":" + targetName);
			}
			else if(args[0].equals("-i")) {
				PluginMessageHandler.sendGoTo(player.getName(), targetName, true);
				if(CachedReport.getCache().containsKey(targetName)) {
					String assigned = CachedReport.getCache().get(targetName).getAssigned();
					if(assigned == null) {
						RedisBungee.getApi().sendChannelMessage("ReportSystem", "Assign:" + player.getName() + ":" + targetName);
					}
				}
			}
			else {
				return; //TODO correct usage
			}
			lastGoTo.put(player.getName(), targetName);
			player.sendMessage(TextComponent.fromLegacyText(Chat.format("&aEnviando...")));
		}
		else {
			PluginMessageHandler.sendGoTo(player.getName(), targetName, false);
		}
	}

	public static Map<String, String> getLastGoTo() {
		return lastGoTo;
	}

}