package net.minemora.reportsystem.command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
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
				LinkedHashMap<Report, Long> reports = Util.sort(Database.getDatabase().getLastestReports(40));
				
				Set<String> listed = new HashSet<String>();
				
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("        &e- - - &6&lReportes recientes: &e- - -")));
				player.sendMessage(TextComponent.fromLegacyText(Chat.format("&fRevisado &7| &fHora &7| &fOpciones | &fReportado &7| &fServidor &7| &fRazón")));
				player.sendMessage(TextComponent.fromLegacyText(Chat.format(" ")));
				
				for(Report report : reports.keySet()) {
					
					if(listed.contains(report.getReported())) {
						continue;
					}
					
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

					UUID uid = RedisBungee.getApi().getUuidFromName(report.getReported());
					if(uid == null) {
						continue;
					}
					ServerInfo serverInfo = RedisBungee.getApi().getServerFor(uid);
					if(serverInfo == null) {
						continue;
					}
						
					String assigned = CachedReport.getCache().get(report.getReported()).getAssigned();
					
					TextComponent sa;
					if(assigned != null) {
						sa = new TextComponent("✔");
						sa.setColor(ChatColor.GREEN);
						BaseComponent[] hoverMsg = new ComponentBuilder(Chat.format("&a&l✔ Asignado:") + "\n" 
								+ Chat.format("&7Staff: &6" + assigned)).create();
						sa.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMsg));
					}
					else {
						sa = new TextComponent("✘");
						sa.setColor(ChatColor.RED);
						BaseComponent[] hoverMsg = new ComponentBuilder(Chat.format("&c&l✘ Sin Asignar") + "\n" 
								+ Chat.format("&7Haz click en &c&lIR &7para revisar")).create();
						sa.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverMsg));
					}
					
					TextComponent msg = new TextComponent("");
					msg.addExtra(sa);
					
					TextComponent dateString = new TextComponent(TextComponent.fromLegacyText(Chat.format(" &3" + date + " &r")));
					
					msg.addExtra(dateString);
					
					TextComponent hidden = new TextComponent("IR");
					hidden.setColor(ChatColor.RED);
					hidden.setBold(true);
					hidden.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/goto -v " + report.getReported()));
					TextComponent ban = new TextComponent("BN");
					ban.setColor(ChatColor.AQUA);
					ban.setBold(true);
					ban.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ban " + report.getReported() 
						+ " 30d " + report.getReason() + " /report"));
					TextComponent legit = new TextComponent("LG");
					legit.setColor(ChatColor.GOLD);
					legit.setBold(true);
					legit.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/legit " + report.getReported()));
					
					msg.addExtra(hidden);
					msg.addExtra(new TextComponent(" "));
					msg.addExtra(ban);
					msg.addExtra(new TextComponent(" "));
					msg.addExtra(legit);
					
					String extra = "";
					int rtimes = CachedReport.getCache().get(report.getReported()).getReportedTimes();
					if(rtimes > 1) {
						extra = " &7(&e" + rtimes +"&7)";
					}
				    
				    msg.addExtra(new TextComponent(TextComponent.fromLegacyText(Chat.format(" &a" + report.getReported() + extra
				    	+ " &b" + serverInfo.getName() + " &c" + report.getReason()))));				

				    player.sendMessage(msg);
				    
				    listed.add(report.getReported());
				}
            }
		});
	}
}
