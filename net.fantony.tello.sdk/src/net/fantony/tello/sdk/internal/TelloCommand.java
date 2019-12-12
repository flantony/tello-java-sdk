package net.fantony.tello.sdk.internal;

import java.util.concurrent.Callable;

import net.fantony.tello.sdk.TelloException;

public class TelloCommand implements Callable<String> {

	private TelloCommandString command;
	private TelloConnection connection;
	private String secondaryCommand;

	public TelloCommand(TelloCommandString primaryCommand, String secondaryCommand, TelloConnection connection) {
		this.command = primaryCommand;
		this.secondaryCommand = secondaryCommand;
		this.connection = connection;
	}

	public TelloCommand(TelloCommandString command, TelloConnection connection) {
		this(command, "", connection);
	}

	@Override
	public String call() throws TelloException {
		String response = "error";
		if (connection.isOpen()) {
			int retrys = 0;
			response = execute();
			System.out.println("---> " + response);
			while (!"ok".equals(response) && retrys < 2) {
				response = execute();
				System.out.println("---> " + response);
				retrys++;
			}
		}
		return response;
	}

	private String execute() throws TelloException {
		synchronized (connection) {
			String message = this.command.command(secondaryCommand);
			connection.send(message);
			return connection.receive();
		}
	}
}
