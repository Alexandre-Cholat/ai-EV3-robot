package ev3;

import java.util.ArrayList;

public class Automate extends NavAlgo {

	public Automate() {
	}

	public void play() {

		int grab = 0;
		boolean center      = false;
		boolean objDetected = false;
		boolean picking     = false;
		boolean attrape     = false;

		//récupération premier palet
		//play.firstGrab();
		//grab++;

		while (grab < 9) {

			// STAP 1 : go to center
			if (!center) {
				r.display("Going to center");
				goToCenter();
				/*if(!goToXcenter2()|| (!goToYcenter2())) {

				}
				 */
				// grab detected before reaching the center
				if (obj_detected()) {
					objDetected = true;   //Go to STAP 3
				}
				center = true;
			}
			// STAP 2 :Search for object
			if (!objDetected && !picking) {

				r.display("Searching object");
				rotate_until_disc_detected();
				
			}
			// STAP 3:Object detected
			if (objDetected && !picking) {

				r.display("Approaching object");

				picking =moveToGrabFacile();
	

				/*// Lost object : Back to search
				if (!obj_detected()) {
					objDetected = false;
				}
				if (reached) {
					picking = true;
				}*/
			}
			
			// STAP 4 : Return to the opposing camp
			if (picking) {
				r.display("Returning to base");
				goToBaseAdverse();
				setDowngrab();
				grab++;

				// HERE WE GO AGAIN FOR ANOTHER ROUND
				center = false;
				objDetected = false;
				picking = false;
				attrape = false;
			}
		}
		r.display("Mission done!");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
