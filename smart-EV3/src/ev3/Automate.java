package ev3;

import java.util.ArrayList;

public class Automate {

	private NavAlgo nav;

	public Automate() {
		nav = new NavAlgo();
	}

	public void play() {

		int grab = 0;
		boolean center = false;
		boolean objDetected = false;
		boolean picking = false;

		while (grab < 9) {

			// STEP 1 : go to center
			if (!center) {

				nav.r.display("Going to center");
				nav.goToCenter();

				if (nav.obj_detected()) {
					objDetected = true;
				}

				center = true;
			}

			// STEP 2 : search object
			if (!objDetected && !picking) {

				nav.r.display("Searching object");
				nav.rotate_until_disc_detected();
				objDetected = true;
			}

			// STEP 3 : approach object
			if (objDetected && !picking) {

				nav.r.display("Approaching object");
				picking = nav.moveToGrabFacile();
			}

			// STEP 4 : return object
			if (picking) {

				nav.r.display("Returning to base");
				nav.goToBaseAdverse();
				nav.setDowngrab();
				grab++;

				// HERE WE GO AGAIN FOR ANOTHER ROUND
				center = false;
				objDetected = false;
				picking = false;
			}
		}

		nav.r.display("Mission done!");
	}
}
