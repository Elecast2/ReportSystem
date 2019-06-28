package net.minemora.reportsystem.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public final class Util {
	
	private Util() {}
	
	public static int getPlayerPing(Player player) {
        try {
            int ping = 0;
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + getServerVersion() + ".entity.CraftPlayer");
            Object converted = craftPlayer.cast(player);
            Method handle = converted.getClass().getMethod("getHandle", new Class[0]);
            Object entityPlayer = handle.invoke(converted, new Object[0]);
            Field pingField = entityPlayer.getClass().getField("ping");
            ping = pingField.getInt(entityPlayer);
            return ping;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
  
    public static String getServerVersion() {
        Pattern brand = Pattern.compile("(v|)[0-9][_.][0-9][_.][R0-9]*");
        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        String version = pkg.substring(pkg.lastIndexOf('.') + 1);
        if (!brand.matcher(version).matches()) {
            version = "";
        }
        return version;
    }

}
