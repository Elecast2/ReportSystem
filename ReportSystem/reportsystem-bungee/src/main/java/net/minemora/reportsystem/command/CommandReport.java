package net.minemora.reportsystem.command;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.minemora.reportsystem.Report;

public class CommandReport extends Command implements TabExecutor {
	
	public CommandReport() {
		super("report");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		if(args.length >= 2) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			String[] reasonArray = new String[args.length - 1];
			for(int i = 1; i < args.length; i++) {
				reasonArray[i-1] = args[i];
			}
			String reason = String.join(" ", reasonArray);
			new Report(player.getName(), args[0], reason).send();
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return null;
		}
		if(args.length == 1) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			Set<UUID> uuids = RedisBungee.getApi().getPlayersOnServer(player.getServer().getInfo().getName());
			return uuids.stream().map(u -> RedisBungee.getApi().getNameFromUuid(u)).collect(Collectors.toList());
		}
		return null;
	}
}