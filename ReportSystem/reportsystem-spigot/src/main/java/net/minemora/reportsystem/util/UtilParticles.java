package net.minemora.reportsystem.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.minemora.reportsystem.ReportSystem;

/**
 * Created by sacha on 07/08/15.
 */
public class UtilParticles {
	
	private final static int DEF_RADIUS = 128;
	
	public static void drawLineToFollowPlayer(Player from, Player to) {
		new BukkitRunnable() {
			int count = 0;
			public void run() {
				if(!from.isOnline()) {
					cancel();
					return;
				}
				if(!to.isOnline()) {
					cancel();
					return;
				}
				drawRedLineToPlayer(from, from.getLocation().add(0, 1.5, 0), to.getLocation().add(0, 1.5, 0));
				count++;
				if(count == 24) {
					cancel();
					return;
				}
			}
		}.runTaskTimer(ReportSystem.getPlugin(), 10L, 5L);
	}
	
	public static void drawRedLineToPlayer(Player player, Location from, Location to) {
		if(!from.getWorld().equals(to.getWorld())) {
			return;
		}
		Location location = from.clone();
		Location target = to.clone();
		
		ParticleEffect effect = ParticleEffect.REDSTONE;
		Vector link = target.toVector().subtract(location.toVector());
		float length = (float) link.length();
		link.normalize();
		int particles = 15;
		
		float ratio = length / particles;
		Vector v = link.multiply(ratio);
		Location loc = location.clone().subtract(v);
		int step = 0;
		for (int i = 0; i < particles; i++) {
			if (step >= (double) particles)
				step = 0;
			step++;
			loc.add(v);
			effect.display(new ParticleEffect.OrdinaryColor(255, 0, 0), loc, player);
		}
	}
	
	public static void drawParticleLine(Location from, Location to, ParticleEffect effect, int particles, int r, int g, int b) {
		Location location = from.clone();
		Location target = to.clone();
		Vector link = target.toVector().subtract(location.toVector());
		float length = (float) link.length();
		link.normalize();
		
		float ratio = length / particles;
		Vector v = link.multiply(ratio);
		Location loc = location.clone().subtract(v);
		int step = 0;
		for (int i = 0; i < particles; i++) {
			if (step >= (double) particles)
				step = 0;
			step++;
			loc.add(v);
			if (effect == ParticleEffect.REDSTONE)
				effect.display(new ParticleEffect.OrdinaryColor(r, g, b), loc, 128);
			else
				effect.display(0, 0, 0, 0, 1, loc, 128);
		}
	}
	
	public static void display(ParticleEffect effect, Location location, int amount, float speed) {
		effect.display(0, 0, 0, speed, amount, location, 128);
	}
	
	public static void display(ParticleEffect effect, Location location, int amount) {
		effect.display(0, 0, 0, 0, amount, location, 128);
	}
	
	public static void display(ParticleEffect effect, Location location) {
		display(effect, location, 1);
	}
	
	public static void display(ParticleEffect effect, double x, double y, double z, Location location, int amount) {
		effect.display((float) x, (float) y, (float) z, 0f, amount, location, 128);
	}
	
	public static void display(ParticleEffect effect, int red, int green, int blue, Location location, int amount) {
		for (int i = 0; i < amount; i++)
			effect.display(new ParticleEffect.OrdinaryColor(red, green, blue), location, DEF_RADIUS);
	}
	
	public static void display(int red, int green, int blue, Location location) {
		display(ParticleEffect.REDSTONE, red, green, blue, location, 1);
	}
	
	public static void display(ParticleEffect effect, int red, int green, int blue, Location location) {
		display(effect, red, green, blue, location, 1);
	}
	
	
}