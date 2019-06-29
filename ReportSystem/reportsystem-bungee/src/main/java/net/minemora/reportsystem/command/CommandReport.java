package net.minemora.reportsystem.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.minemora.reportsystem.Report;
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
				if((System.currentTimeMillis() - cooldown.get(player.getName())) < 10000) {
					player.sendMessage(new TextComponent(Chat.format("&cDebes esperar " 
							+ (int)(10000 - ((System.currentTimeMillis() - cooldown.get(player.getName())))/1000) 
							+ " segundos antes de volver a usar este comando")));
					return;
				}
			}
			String[] reasonArray = new String[args.length - 1];
			for(int i = 1; i < args.length; i++) {
				reasonArray[i-1] = args[i];
			}
			String reason = String.join(" ", reasonArray);
			if(reason.length()>100) {
				player.sendMessage(new TextComponent(Chat.format("&cLa razón no puede contener mas de 100 caracteres")));
				return;
			}
			cooldown.put(player.getName(), System.currentTimeMillis());
			new Report(player.getName(), args[0], reason).send();
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return new ArrayList<String>();
		}
		if(args.length == 1) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			Set<UUID> uuids = RedisBungee.getApi().getPlayersOnServer(player.getServer().getInfo().getName());
			return uuids.stream().map(u -> RedisBungee.getApi().getNameFromUuid(u)).collect(Collectors.toList());
		}
		return new ArrayList<String>();
	}
}