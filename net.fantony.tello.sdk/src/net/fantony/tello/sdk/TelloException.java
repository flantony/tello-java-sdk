package net.fantony.tello.sdk;

public class TelloException extends Exception {

	public TelloException(Exception e) {
		super(e);
	}

	public TelloException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
