package net.minemora.reportsystem.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.minemora.reportsystem.CachedReport;
import net.minemora.reportsystem.Report;
import net.minemora.reportsystem.ReportSystem;
import net.minemora.reportsystem.database.Database;
import net.minemora.reportsystem.util.Chat;
import net.minemora.reportsystem.util.Util;

public class CommandReportList extends Command {

	public CommandReportList() {
		super("reportlist", "staff", "rlist");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) sender;
		
		ReportSystem.getPlugin().getProxy().getScheduler().runAsync(ReportSystem.getPlugin(), new Runnable() {
			@Override
			public void run() {
				LinkedHashMap<Report, Long> reports = Util.sort(Database.getDatabase().getLastestReports(15));
				
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("     &e- - - &6&lReportes recientes: &e- - -")));
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("&f&l[ &7reportado &8(&aon &7off&8), "
						+ "&crazón&8, &6staff asignado &f&l]")));
				player.sendMessage(TextComponent.fromLegacyText(Chat.format(" ")));
				
				for(Report report : reports.keySet()) {
					
					if(CachedReport.getCache().containsKey(report.getReported())) {
						if(CachedReport.getCache().get(report.getReported()).isLegit()) {
							continue;
						}
					}
					else {
						continue;
					}
					
					Date datec = new Date(reports.get(report));
				    DateFormat formatter = new SimpleDateFormat("HH:mm");
				    String date = formatter.format(datec);
				    
				    String reason = report.getReason();
				    if(reason.length() > 16) {
				    	reason = reason.substring(0, 16);
				    }
				    
				    boolean targetOn = true;
					UUID uid = RedisBungee.getApi().getUuidFromName(report.getReported());
					if(uid == null) {
						targetOn = false;
					}
					ServerInfo serverInfo = RedisBungee.getApi().getServerFor(uid);
					if(serverInfo == null) {
						targetOn = false;
					}
				    
					String reported;
					if(targetOn) {
						reported = "&a" + report.getReported() + "&b" + serverInfo.getName();
					}
					else {
						reported = "&7" + report.getReported();
					}
					String sa;
						
					String assigned = CachedReport.getCache().get(report.getReported()).getAssigned();
					if(assigned != null) {
						sa = "&6" + assigned;
					}
					else {
						sa = "&4sin asignar";
					}
				    
				    TextComponent msg = new TextComponent(TextComponent.fromLegacyText(Chat.format("&3" + date + " " + reported 
				    		+ "&8, &c" + reason + "&9, " + sa + " &7- ")));
				    msg.setColor(ChatColor.GRAY);
					TextComponent visible = new TextComponent("VIS");
					visible.setColor(ChatColor.GREEN);
					visible.setBold(true);
					visible.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/goto " + report.getReported()));
					TextComponent hidden = new TextComponent("OCT");
					hidden.setColor(ChatColor.RED);
					hidden.setBold(true);
					hidden.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/goto -v " + report.getReported()));
					TextComponent ban = new TextComponent("BAN");
					ban.setColor(ChatColor.AQUA);
					ban.setBold(true);
					ban.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ban " + report.getReported() 
						+ " 30d " + report.getReason() + " /report"));
					TextComponent legit = new TextComponent("LEG");
					legit.setColor(ChatColor.GOLD);
					legit.setBold(true);
					legit.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/legit " + report.getReported()));
					msg.addExtra(visible);
					msg.addExtra(new TextComponent(" "));
					msg.addExtra(hidden);
					msg.addExtra(new TextComponent(" "));
					msg.addExtra(ban);
					msg.addExtra(new TextComponent(" "));
					msg.addExtra(legit);

				    player.sendMessage(msg);
				}
            }
		});
	}
}
