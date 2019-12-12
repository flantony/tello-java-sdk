package net.fantony.tello.sdk;

import net.fantony.tello.sdk.internal.StatusUpdate;

public class DroneControllerExample implements DroneStatusListener {

	public static void main(String[] args) throws TelloException {
		DroneControllerExample controller = new DroneControllerExample();
		
		try (TelloDrone drone = new TelloDrone("192.168.10.1", controller)) {
			controller.startMission(drone);
		} catch (TelloException e) {
			e.printStackTrace();
		}
	}

	private void startMission(TelloDrone drone) throws TelloException {
		drone.takeOff();
		drone.moveForward(80);
		drone.flip("b");
		drone.moveLeft(80);
		drone.flip("r");
		drone.moveBackwards(80);
		drone.flip("f");
		drone.moveRight(80);
		drone.flip("l");
		drone.land();
	}

	@Override
	public void onStatusUpdate(StatusUpdate update) {
		System.out.println(update);
	}
}
