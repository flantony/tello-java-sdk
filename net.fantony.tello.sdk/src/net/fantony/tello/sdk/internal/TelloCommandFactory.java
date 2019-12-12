package net.fantony.tello.sdk.internal;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.fantony.tello.sdk.DroneStatusListener;

public class TelloCommandFactory implements Closeable {

	private final TelloConnection connection;

	public TelloCommandFactory(TelloConnection connection) {
		this.connection = connection;
	}

	public TelloCommand create(TelloCommandString command) {
		return new TelloCommand(command, connection);
	}

	public TelloCommand create(TelloCommandString command, int distance) {
		return new TelloCommand(command, String.valueOf(distance), connection);
	}

	public TelloCommand create(TelloCommandString command, String direction) {
		return new TelloCommand(command, direction, connection);
	}

	public Runnable createStatusUpdater(final List<DroneStatusListener> listener) {
		return new Runnable() {

			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					try {
						final StatusUpdate update = new StatusUpdate(
								TelloCommandFactory.this.connection.getStatusString());
						listener.forEach(l -> {
							l.onStatusUpdate(update);
						});
						Thread.sleep(TimeUnit.SECONDS.toMillis(5));
					} catch (Exception e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		};
	}

	@Override
	public void close() throws IOException {
		this.connection.close();
	}

	public void connect() {
		
	}

}
