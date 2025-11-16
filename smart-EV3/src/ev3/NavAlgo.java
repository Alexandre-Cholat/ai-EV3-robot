package ev3;

import lejos.hardware.Button;
import lejos.utility.Delay;

public class NavAlgo {

	private Robot r;
	private Sensor s;
	private Position p;

	//enviornment dimensions
	static int table_length = 250;
	static int table_width = 230;

	public NavAlgo() {
		this.r = new Robot();
		this.s = new Sensor();
		this.p = new Position();
	}

	// navigates to center from any position in the environment
	public void goToCenter() {

		// use ultrasonic to position midway between two X axis walls
		goToXcenter();

		// use ultrasonic to position midway between two Y axis walls
		goToYcenter();

	}

	public void goToYcenter() {

		// rotate to face adversary camp
		rotateTo(180);

		//align perfectly
		align();

		// if not centered
		while(s.getDistance() != table_length/2) {

			r.forward(s.getDistance() - table_length /2);
		}


	}

	public void goToXcenter() {

		// rotate to face wall
		rotateTo(90);

		//align perfectly
		align();

		// if not centered
		while(s.getDistance() != table_width/2) {

			r.forward(s.getDistance() - table_width /2);
		}


	}

	// rotates to absolute orientation heading from any position angle
	public void rotateTo(int orientation){
		int current_a = p.getPosition()[0];
		int calc_turn = orientation - current_a;
		r.turn(calc_turn);
	}

	public void align(){
		int dist1 = (int) s.getDistance();
		// minimise distance between wall
		while(true) {
			turn(10);

		}
	}

	public void rotate_until_obj_detected() {

	}


	public boolean obj_detected() {
		float d =  s.getDistance();
		if(d<50) { //choix du nombre aléatoirement
			r.display("Grab detected");
			return true ; 
		}
		return false ; 
	}


	public void moveToGrab() {
		if (obj_detected()) {
			float distanceGrabRobot = s.getDistance();
			while (distanceGrabRobot >10) {
				r.forward(5);
				distanceGrabRobot= s.getDistance();
			}
		}
		r.stop();
		r.display("Distance assez proche du pavé",5000);
	}

	public void pickUpGrab() {
		if (s.getDistance()<10) {
			r.pincherOpen();
			r.forward(5);
			r.pincherClose();
			r.display("Pavé attrapé",5000);
		}
		/*r.pincherOpen();
		int [] tab = p.getPosition();
		r.display("Angle: " + tab[0], 5000);
		Robot.pincherOpen= true;
		r.pincherClose();*/

	}

	public void wander() {

		while (s.getDistance()> 10) {
			r.forward();
		}
		r.turnLeft();
		long rand = (long)( Math.random()* 10000);
		Delay.msDelay(rand);

	}

	public void wander2() {
		float distCm = s.getDistance();
		r.display("D: "+ distCm, 200);
		while(distCm > 20) {
			distCm = s.getDistance();
			r.display("D: "+ distCm, 200);
			r.forward();
		}

		r.beep();
		r.stop();




	}

}
