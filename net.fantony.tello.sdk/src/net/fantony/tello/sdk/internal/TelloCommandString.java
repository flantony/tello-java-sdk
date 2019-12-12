package net.fantony.tello.sdk.internal;

public enum TelloCommandString {
	SDK_MODE("command"), TAKE_OFF("takeoff"), FORWARD("forward"), LAND("land"), BACKWARDS("back"), LEFT("left"),
	RIGHT("right"), ROTATE_CW("cw"), ROTATE_CCW("ccw"), FLIP("flip"), STREAM_ON("streamon"), STREAM_OFF("streamoff");

	final String command;

	TelloCommandString(String command) {
		this.command = command;
	}

	public String command(String parameters) {
		return (this.command + ((parameters != null ? " "+parameters : ""))).trim();
	}
}
