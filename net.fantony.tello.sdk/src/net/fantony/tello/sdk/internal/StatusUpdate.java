package net.fantony.tello.sdk.internal;

public class StatusUpdate {

	private String statusString;

	public StatusUpdate(String statusString) {
		this.statusString = statusString;
	}

	@Override
	public String toString() {
		return "StatusUpdate [statusString=" + statusString + "]";
	}

}
