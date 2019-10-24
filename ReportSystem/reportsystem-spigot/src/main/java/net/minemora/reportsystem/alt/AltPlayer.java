package net.minemora.reportsystem.alt;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

public class AltPlayer {

	private String realName;
	private String disguisedName;
	private UUID uuid;

	private GameProfile oldProfile;
	private GameProfile newProfile;

	public AltPlayer(Player player, String disguisedName, GameProfile oldProfile, GameProfile newProfile) {
	    this.realName = player.getName();
	    this.disguisedName = disguisedName;
	    this.uuid = player.getUniqueId();
	    this.oldProfile = oldProfile;
	    this.newProfile = newProfile;
	}


	public String getRealName() {
	    return this.realName;
	}

	public String getDisguisedName() {
	    return this.disguisedName;
	}

	public UUID getUUID() {
	    return this.uuid;
	}

	public GameProfile getOldProfile() {
	    return oldProfile;
	}

	public GameProfile getNewProfile() {
	    return newProfile;
	}
}