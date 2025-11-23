package ev3;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.utility.Delay;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;

public class NavAlgo {

	private RobotPilot r;
	private Sensor s;
	private Position p;

	// environment dimensions
	static int table_length = 300;
	static int table_width = 200;

	public NavAlgo() {
		this.r = new RobotPilot();
		this.s = new Sensor();
		this.p = new Position();
	}

	// navigates to center from any position
	public void goToCenter() {
		goToXcenter();
		goToYcenter();
	}

	public void goToYcenter() {
		rotateTo(180);
		align(180);

		while (s.getDistance() != table_length / 2) {
			r.forward(s.getDistance() - table_length / 2);
		}
	}

	public void goToXcenter() {
		rotateTo(90);
		align(90);

		while (s.getDistance() != table_width / 2) {
			r.forward(s.getDistance() - table_width / 2);
		}
	}

	public void rotateTo(int orientation) {
		int current_a = p.getPosition();
		int calc_turn = orientation - current_a;
		r.turn(calc_turn);
		p.setAngle(orientation);
	}

	public void align(int startPos) {
		int dist1;
		int min = 1000;
		int minAngle = startPos;

		rotateTo(startPos);

		for (int i = 0; i < 10; i++) {
			int randAngle = (int) (Math.random() * 7);
			r.turn(randAngle);

			dist1 = (int) s.getDistance();

			if (dist1 < min) {
				min = dist1;
				minAngle = p.getPosition();
				r.display("New min angle: " + minAngle);
			}

			rotateTo(startPos);
		}

		rotateTo(minAngle);
		p.setAngle(startPos);
	}


	public void rotate_until_disc_detected() {
		//tourne jusqu'a detecter une discontinuité
		float previousDist=s.getDistance();
		for(int angle=0;angle<=360;angle+=5) {
			r.turn(5);
			Delay.msDelay(100);
			float currentDist=s.getDistance();
			if(Math.abs(previousDist-currentDist)>10){//a voir s'il faut valeur plus grande ou plus petite
				return;
			}
			previousDist=currentDist;
		}

	}
	public void new_research(int angle) {
		//si le premier tour n'a rien donner, 
	}

	public boolean obj_detected() {
		float d = s.getDistance();
		if (d < 50) {
			r.display("Grab detected");
			return true;
		}
		return false;
	}


	public void moveToGrab() {
		if (obj_detected()) {
			float previousDistance = s.getDistance();
			float currentDistance = previousDistance;

			while (currentDistance >= 10) {
				r.forward(3);

				previousDistance = currentDistance;
				currentDistance = s.getDistance();

				if (currentDistance >= previousDistance) {
					r.stop();
					r.display("Mauvaise trajectoire",3000);
					return;
				}
			}
			r.stop();
			r.display("Distance assez proche du pavé",5000);
		}
	}


	public void pickUpGrab() {
		if (s.getDistance() <= 10) {
			r.pincherOpen();
			r.forward(5);
			r.pincherClose();
			r.display("Pavé attrapé", 5000);
		}
	}

	public void testing() {
		float distCm = s.getDistance();
		r.display("D: " + distCm, 200);

		while (true) {
			distCm = s.getDistance();
			r.display("D: " + distCm, 200);

			if (s.isPressed()) {
				r.beep();
			}
		}
	}

	public void forwardsTest() {
		r.forward(-50);
	}

	// ────────────────
	// WANDER FUNCTIONS
	// ────────────────

	public void wander() {
		while (s.getDistance() > 10) {
			r.forward();
		}
		r.turn(1000000);	//infinite turn
		long rand = (long) (Math.random() * 10000);
		Delay.msDelay(rand);
		//stop
		r.stop();
	}

	public void wander2() {
		float distCm = s.getDistance();
		r.display("D: " + distCm, 200);

		while (distCm > 20) {
			distCm = s.getDistance();
			r.display("D: " + distCm, 200);
			r.forward();
		}

		r.beep();
		r.stop();


	}

}
