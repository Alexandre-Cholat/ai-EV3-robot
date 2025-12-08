package ev3;

import java.util.ArrayList;

public class Automate extends NavAlgo {
	RobotPilot r = new RobotPilot(); 
	Sensor s= new Sensor ();
	Position p= new Position();
	NavAlgo play = new NavAlgo(r,s,p);

	public Automate() {

	}

	public void play() {

		int grab = 0;
		boolean center      = false;
		boolean objDetected = false;
		boolean picking     = false;
		boolean attrape     = false;

		//récupération premier palet
		play.firstGrab();
		grab++;

		while (grab < 9) {

			// STAP 1 : go to center
			if (!center) {
				r.display("Going to center");
				play.goToCenter();
				/*if(!goToXcenter2()|| (!goToYcenter2())) {

				}
				 */
				// grab detected before reaching the center
				if (play.obj_detected()) {
					objDetected = true;   //Go to STAP 3
				}
				center = true;
			}
			// STAP 2 :Search for object
			if (!objDetected && !picking) {

				r.display("Searching object");

				ArrayList<Float> t=play.downsampleToHalfDegree(play.spin(360), 360);
				double[] tab=play.angles_grab(t);
				if(tab==null) {
					r.display("aucun palet detecté...");
				}else {
					r.turn((int)tab[0]);
					objDetected=true;
				}
				
			}
			// STAP 3:Object detected
			if (objDetected && !picking) {

				r.display("Approaching object");

				//play.moveToGrab();
				picking = true;

				/*// Lost object : Back to search
				if (!obj_detected()) {
					objDetected = false;
				}
				if (reached) {
					picking = true;
				}*/
			}
			// Catch the object
			if (picking && !attrape) {
				r.display("Grabbing object");
				pickUpGrab();
				attrape = true;
			}
			// STAP 4 : Return to the opposing camp
			if (attrape) {
				r.display("Returning to base");
				play.goToBaseAdverse();
				play.setDowngrab();
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
