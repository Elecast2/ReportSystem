package net.minemora.reportsystem.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.minemora.reportsystem.util.Chat;

public class CommandReportInfo extends Command {

	public CommandReportInfo() {
		super("reportinfo");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) sender;
		player.sendMessage(TextComponent.fromLegacyText(Chat.format("")));
		player.sendMessage(TextComponent.fromLegacyText(Chat.format("&f&l&m============&E&L(&c&lREGLAS DEL /REPORT&E&L)&7&l&m============")));
		player.sendMessage(TextComponent.fromLegacyText(Chat.format(" &f>> &6Debes estar seguro de que la persona que reportas esta "
				+ "incumpliendo normas. &cFalsos reportes podrian provocar que se te quiten los permisos de reportar, o hasta podrias "
				+ "ser baneado.")));
		player.sendMessage(TextComponent.fromLegacyText(Chat.format(" &f>> &6Prohibido el uso \"en broma\" del comando.")));
		player.sendMessage(TextComponent.fromLegacyText(Chat.format(" &f>> &6No repetir reportes a la misma persona de forma seguida.")));
		player.sendMessage(TextComponent.fromLegacyText(Chat.format(" &7Subir pruebas: &bfacebook.com/groups/MinemoraReportes/")));
		player.sendMessage(TextComponent.fromLegacyText(Chat.format("&7&l&m======================&f&l&m======================")));
	}
}