package net.fantony.tello.sdk.internal;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import net.fantony.tello.sdk.TelloException;

public class TelloConnection implements Closeable {

	private static final int COMMAND_PORT = 8889;
	private static final int STATE_PORT = 8890;
	private static final int VIDEO_PORT = 11111;
	private final DatagramSocket commandSocket;
	private final DatagramSocket stateSocket;

	public TelloConnection(InetAddress address) throws TelloException {
		try {
			this.stateSocket = new DatagramSocket(STATE_PORT);
			this.commandSocket = new DatagramSocket();
			this.commandSocket.connect(address, COMMAND_PORT);
			this.stateSocket.connect(address, STATE_PORT);
			if (!commandSocket.isConnected() || !this.stateSocket.isConnected()) {
				throw new TelloException("Unable to connect to drone.");
			}
		} catch (Exception e) {
			throw new TelloException(e);
		}
	}

	@Override
	public void close() {
		if (this.commandSocket != null) {
			this.commandSocket.disconnect();
			this.commandSocket.close();
		}
		if (this.stateSocket != null) {
			this.stateSocket.disconnect();
			this.stateSocket.close();
		}
	}

	void send(String command) throws TelloException {
		System.out.println(command);
		final byte[] buffer = command.getBytes();
		try {
			this.commandSocket
					.send(new DatagramPacket(buffer, buffer.length, commandSocket.getInetAddress(), COMMAND_PORT));
		} catch (IOException e) {
			throw new TelloException(e);
		}
	}

	String receive() throws TelloException {
		try {
			byte[] buffer = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			this.commandSocket.receive(packet);
			return new String(Arrays.copyOf(buffer, packet.getLength()), StandardCharsets.UTF_8);
		} catch (IOException e) {
			new TelloException(e);
		}
		return "error";
	}

	public String getStatusString() throws IOException {
		final byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		this.stateSocket.receive(packet);
		return new String(Arrays.copyOf(buffer, packet.getLength()), StandardCharsets.UTF_8);
	}

	public boolean isOpen() {
		return this.commandSocket.isConnected();
	}

}
