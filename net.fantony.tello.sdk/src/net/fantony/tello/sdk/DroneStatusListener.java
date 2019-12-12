package net.fantony.tello.sdk;

import net.fantony.tello.sdk.internal.StatusUpdate;

public interface DroneStatusListener {

	public void onStatusUpdate(StatusUpdate update);
}
