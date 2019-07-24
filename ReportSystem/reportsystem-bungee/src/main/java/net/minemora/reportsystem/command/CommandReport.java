package net.minemora.reportsystem.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.minemora.reportsystem.CachedReport;
import net.minemora.reportsystem.Report;
import net.minemora.reportsystem.ReportBanManager;
import net.minemora.reportsystem.util.Chat;
import net.minemora.reportsystem.util.Util;

public class CommandReport extends Command implements TabExecutor {
	
	public static Map<String,Long> cooldown = new HashMap<>();
	
	private String[] reasons = new String[] {"killaura","aimbot","antikb","nofall","fly","speed","safewalk","x-ray","varios hacks"};
	private String[] reasonsBadWr = new String[] {"killaura","kill-aura","kill aura","aimbot","aim-bot","antikb","anti kb","anti knockback"
			,"antiempuje","anti empuje","anti-empuje","no kb","nofall","no fall","anti fall","fly","speed","safewalk","safe walk","x-ray","xray","x ray","hacks en general"
			,"varios hacks","multiples hacks","hacks exagerados"};
	
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
				if(time < 25000) {
					player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cDebes esperar " + (int)((25000 - time)/1000)
							+ " segundos antes de volver a usar este comando")));
					return;
				}
			}
			
			if(ReportBanManager.isBan(player.getUniqueId())) {
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cNo tienes permitido usar este comando.")));
				return;
			}
			
			if(Util.containsIgnoreCase(CachedReport.getCache().keySet() ,args[0])) {
				long time = CachedReport.getCache().get(args[0]).getTime();
				if((System.currentTimeMillis() - time) < 600000) {
					player.sendMessage(TextComponent.fromLegacyText(Chat.format("&c¡Este jugador ya ha sido reportado recientemente!")));
					return;
				}
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
			boolean badReason = true;
			for(String re : reasonsBadWr) {
				if(reason.trim().equalsIgnoreCase(re)) {
					badReason = false;
					break;
				}
			}
			if(badReason) {
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("&cLa razón debe ser una de las "
						+ "siguientes: &4 " + String.join(", ", reasons))));
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("&6&lRecuerda que los reportes falsos pueden "
						+ "provocar que se te niege el comando /report")));
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
		if(args.length == 2) {
			return new ArrayList<>(Arrays.asList(reasons));
		}
		return new ArrayList<String>();
	}
}