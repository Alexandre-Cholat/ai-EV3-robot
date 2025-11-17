package ev3;

import lejos.hardware.Button;
import lejos.utility.Delay;

public class NavAlgo {

	private Robot r;
	private Sensor s;
	private Position p;

	//enviornment dimensions
	static int table_length = 300;
	static int table_width = 200;

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

		// rotates to face adversary camp and updates position
		rotateTo(180);
		

		//align perfectly
		align(180);

		// if not centered
		while(s.getDistance() != table_length/2) {

			r.forward(s.getDistance() - table_length /2);
		}


	}

	public void goToXcenter() {

		// rotate to face wall
		rotateTo(90);

		//align perfectly
		align(90);

		// if not centered
		while(s.getDistance() != table_width/2) {

			r.forward(s.getDistance() - table_width /2);
		}


	}

	// rotates to absolute orientation heading from any position angle
	public void rotateTo(int orientation){
		int current_a = p.getPosition();
		int calc_turn = orientation - current_a;
		r.turn(calc_turn);
		
		//update heading
		p.setAngle(orientation);
	}

	public void align(int startPos){
		int dist1 = (int) s.getDistance();
		int min = 1000;
		int minAngle = startPos;
		// minimise distance between wall
		
		int i = 0;
		
		while(i<10) {
			
			// rotate to random angle
			int randAngle = (int) Math.random() * 7;
			r.turn(randAngle);
			dist1 = (int) s.getDistance();
			
			//new best candidate
			if(dist1 < min) {
				min = dist1;
				minAngle = p.getPosition();
				
				r.display("New min angle: " + minAngle);
			}
			
			//return to starting position center
			rotateTo(startPos);
		}
		
		
		//rotate to smallest distance to wall
		rotateTo(minAngle);
		
		//set minAngle as intended start angle
		p.setAngle(startPos);
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

	public void testing() {
		
		float distCm = s.getDistance();
		r.display("D: "+ distCm, 200);
		
		while(true) {
			distCm = s.getDistance();
			r.display("D: "+ distCm, 200);
			
			if(s.isPressed() ) {
				r.beep();
			}
		}

	}
	
	public void forwardsTest() {
		r.forward(-50);
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
