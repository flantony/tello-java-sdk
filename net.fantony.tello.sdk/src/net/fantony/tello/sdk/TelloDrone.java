package net.fantony.tello.sdk;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.fantony.tello.sdk.internal.TelloCommand;
import net.fantony.tello.sdk.internal.TelloCommandFactory;
import net.fantony.tello.sdk.internal.TelloCommandString;
import net.fantony.tello.sdk.internal.TelloConnection;

public class TelloDrone implements AutoCloseable {

	private static final String OK = "ok";
	private final ExecutorService executor;
	private final List<DroneStatusListener> listener;
	private final TelloCommandFactory factory;

	public TelloDrone(String ip, DroneStatusListener listener) throws TelloException {
		this(createConnection(ip), listener);
	}

	public TelloDrone(TelloConnection connection, DroneStatusListener listener) throws TelloException {
		this(connection, Collections.singletonList(listener));
	}

	private TelloDrone(TelloConnection connection, List<DroneStatusListener> listener) throws TelloException {
		this.factory = new TelloCommandFactory(connection);
		this.listener = listener;
		executor = Executors.newFixedThreadPool(2);
	}


	private static TelloConnection createConnection(String ip) {
		try {
			return new TelloConnection(Inet4Address.getByName(ip));
		} catch (UnknownHostException | TelloException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void connect() throws TelloException {
		if (this.factory != null && !startSDKMode()) {
			throw new TelloException("Unable to connect to drone");
		}
	}

	@Override
	public void close() throws TelloException {
		try {
			this.executor.shutdown();
			this.executor.awaitTermination(5, TimeUnit.SECONDS);
			this.factory.close();
		} catch (Exception e) {
			throw new TelloException(e);
		}
	}

	public boolean startSDKMode() {
		if (internalExecuteCommand(factory.create(TelloCommandString.SDK_MODE))) {
			executor.execute(factory.createStatusUpdater(this.listener));
			return true;
		}
		return false;
	}

	public boolean takeOff() {
		return internalExecuteCommand(factory.create(TelloCommandString.TAKE_OFF));
	}

	public boolean moveForward(int distance) {
		if (checkDistance(distance)) {
			return internalExecuteCommand(factory.create(TelloCommandString.FORWARD, distance));
		}
		return false;
	}
	

	public boolean moveBackwards(int distance) {
		if (checkDistance(distance)) {
			return internalExecuteCommand(factory.create(TelloCommandString.BACKWARDS, distance));
		}
		return false;
	}

	public boolean moveLeft(int distance) {
		if (checkDistance(distance)) {
			return internalExecuteCommand(factory.create(TelloCommandString.LEFT, distance));
		}
		return false;
	}

	public boolean moveRight(int distance) {
		if (checkDistance(distance)) {
			return internalExecuteCommand(factory.create(TelloCommandString.RIGHT, distance));
		}
		return false;
	}

	public boolean flip(String direction) {
		return internalExecuteCommand(factory.create(TelloCommandString.FLIP, direction));
	}

	public boolean land() {
		return internalExecuteCommand(factory.create(TelloCommandString.LAND));
	}

	private boolean checkDistance(int distance) {
		return distance >= 20 && distance <= 500;
	}

	private boolean internalExecuteCommand(TelloCommand command) {
		if (command == null) {
			return false;
		}
		try {
			return this.executor.submit(command).get().equals(OK);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}

}
