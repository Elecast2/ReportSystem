package net.minemora.reportsystem.network;

public class RequestPacket {
	
	private String origin;
	private String destiny;
	
	public RequestPacket(String origin, String destiny) {
		this.origin = origin;
		this.destiny = destiny;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public String getDestiny() {
		return destiny;
	}

	public void setDestiny(String destiny) {
		this.destiny = destiny;
	}

}
