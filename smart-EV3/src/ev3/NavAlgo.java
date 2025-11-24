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
import lejos.hardware.Battery;

public class NavAlgo {

	private RobotPilot r;
	private Sensor s;
	private Position p;
	private boolean objDetecter=false;

	// environment dimensions
	static int table_length = 300;
	static int table_width = 200;

	public NavAlgo() {
		this.r = new RobotPilot();
		this.s = new Sensor();
		this.p = new Position();
	}

	public NavAlgo(RobotPilot r, Sensor s, Position p) {
		this.r = r;
		this.s = s;
		this.p = p;
	}
	/*public NavAlgo(RobotPilot r , Sensor s , Position p) {
		this.r = r;
		this.s = s;
		this.p = p;
	}*/

	
	public boolean getObjDetecter() {
		return objDetecter;
	}


	// navigates to center from any position
	public void goToCenter() {
		goToXcenter();
		goToYcenter();
	}

	public void goToYcenter() {
		rotateTo(180);


		//align perfectly>>>>>>> 82b597528f6f865f9137de4dc273bc8f6f09f11e
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
	public void goToBaseAdverse() {
		rotateTo(180);
		while(s.getDistance() != 20) {
			r.forward(s.getDistance()-20);
		}
	}

	public void rotateTo(int orientation) {
		int current_a = p.getPosition();
		int calc_turn = orientation - current_a;
		r.turn(calc_turn);
		p.setAngle(orientation);
	}

	public void align(int startPos) {
		int dist1 = 0;
		int min = 1000;
		int minAngle = startPos;
		// minimise distance between wall

		int i = 0;

		while(i<10) {

			// rotate to random angle
			int randAngle = (int) Math.random() * 7;
		rotateTo(startPos);
		
		for (int j = 0; j < 10; i++) {
			randAngle = (int) (Math.random() * 7);
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
		//tourne jusqu'a detecter une discontinuité, renvoie vrai s'il en trouve, false sinon
		float previousDist=s.getDistance();
		for(int angle=0;angle<=360;angle+=5) {
			r.turn(5);
			Delay.msDelay(100);
			float currentDist=s.getDistance();
			if(Math.abs(previousDist-currentDist)>10){//a voir s'il faut valeur plus grande ou plus petite
				objDetecter=true;
				r.display("Grab detected",5000);
			}
			previousDist=currentDist;
		}
		objDetecter=false;

	}

	public boolean obj_detected() {
		float d = s.getDistance();
		if (d < 50) {
			r.display("Grab detected");
			return true;
		}
		return false;
	}


	public boolean moveToGrab() {
		float previousDistance = s.getDistance();
		float currentDistance = previousDistance;

		while (currentDistance >= 10 && !s.isPressed()) {
			r.forward(3);

			previousDistance = currentDistance;
			currentDistance = s.getDistance();

			if (currentDistance >= previousDistance) {
				r.stop();
				r.display("Mauvaise trajectoire",3000);
				return false ;
			}
		}
		r.stop();
		r.display("Distance assez proche du pavé",5000);
		return true ;

	}



	public void pickUpGrab() {
		if (s.getDistance() <= 10) {
			r.pincherOpen();
			r.forward(5);
			r.pincherClose();
			r.display("Pavé attrapé", 5000);
		}
		/*r.pincherOpen();
		int [] tab = p.getPosition();
		r.display("Angle: " + tab[0], 5000);
		Robot.pincherOpen= true;
		r.pincherClose();*/

	}

	
	public void setDowngrab() {
		//méthode qui va deposer le palet et reculer et fermer les pinces
		r.pincherOpen();
		r.forward(-10);
		r.pincherClose();
		r.display("Pavé déposer",5000);
	}

	

	public void batteryStatus() {
		r.display("Battery: " + Battery.getVoltage() + " v", 5000);
	}



	// ────────────────
	// TESTING FUNCTIONS
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

	public void calibrateTurn(int x) {
		r.turn(x, 150);

		
		
	}
	public void calibrateMove() {
		r.forward(10);
		Delay.msDelay(500);
		r.turn(20, 150);
		Delay.msDelay(500);
		r.turn(-10, 150);
		Delay.msDelay(500);
		r.turn(-20, 150);
		Delay.msDelay(500);
		r.turn(10, 150);
		r.forward(-10);


	}
	
	public void errorCalc() {
		
		// 5 loops
		for(int i=0;i<2;i++) {
			r.forward(100);
			Delay.msDelay(500);
			r.turn(180, 150);
		}
		
		

	}

}
