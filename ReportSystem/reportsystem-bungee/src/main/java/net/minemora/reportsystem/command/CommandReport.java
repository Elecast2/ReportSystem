package net.minemora.reportsystem.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.minemora.reportsystem.Report;
import net.minemora.reportsystem.ReportBanManager;
import net.minemora.reportsystem.util.Chat;

public class CommandReport extends Command implements TabExecutor {
	
	public static Map<String,Long> cooldown = new HashMap<>();
	
	public CommandReport() {
		super("report", "report.temp");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		if(args.length >= 2) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			
			if(cooldown.containsKey(player.getName())) {
				long time = System.currentTimeMillis() - cooldown.get(player.getName());
				if(time < 10000) {
					player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cDebes esperar " + (int)((10000 - time)/1000)
							+ " segundos antes de volver a usar este comando")));
					return;
				}
			}
			
			if(ReportBanManager.isBan(player.getUniqueId())) {
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cNo tienes permitido usar este comando.")));
				return;
			}
			
			String[] reasonArray = new String[args.length - 1];
			for(int i = 1; i < args.length; i++) {
				reasonArray[i-1] = args[i];
			}
			String reason = String.join(" ", reasonArray);
			if(reason.length()>100) {
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cLa razón no puede contener mas de 100 caracteres")));
				return;
			}
			UUID uid = RedisBungee.getApi().getUuidFromName(args[0]);
			if(uid == null) {
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cEl jugador al que quieres reportar no se encuentra conectado")));
				return;
			}
			ServerInfo serverInfo = RedisBungee.getApi().getServerFor(uid);
			if(serverInfo == null) {
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cEl jugador al que quieres reportar no se encuentra conectado")));
				return;
			}
			cooldown.put(player.getName(), System.currentTimeMillis());
			player.sendMessage(TextComponent.fromLegacyText(Chat.format("&a&l¡Se ha enviado tu reporte con éxito!")));
			player.sendMessage(TextComponent.fromLegacyText(Chat.format("&eUn miembro del staff revisará tu reporte "
					+ "tan pronto como sea posible. &cRecuerda que el mal uso de este comando puede provocar que se niegen tus "
					+ "permisos para usarlo o hasta podrias ser baneado. &6Para mas información usa &e/reportinfo")));
			new Report(player.getName(), args[0], reason).send();
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