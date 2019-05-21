package net.minemora.reportsystem.packet;

public class PacketGoTo {
	
	private String player;
	private String target;
	private boolean vanish;
	
	public PacketGoTo(String player, String target, boolean vanish) {
		this.player = player;
		this.target = target;
		this.vanish = vanish;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public boolean isVanish() {
		return vanish;
	}

	public void setVanish(boolean vanish) {
		this.vanish = vanish;
	}

}
