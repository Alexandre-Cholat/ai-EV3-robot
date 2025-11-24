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

import java.util.ArrayList;

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
		
		//rotation 360
		ArrayList<Float> tabDistances = spin(360);
		
		
		/*	sweep
		ArrayList<Float> tabDistances = spin(30);
		ArrayList<Float> tabDistances2 = spin(-60);
		tabDistances.addAll(tabDistances2);
		*/
		
		//Calcul de indexe de la valeur la plus proche du mur
		int minIdx = findCenterByDerivative(tabDistances);
		
		// Calcul angle de minIdx
		int minAngle = (tabDistances.size()/360) * minIdx;
		
		
		//rotate to smallest distance to wall
		rotateTo(minAngle);
		p.setAngle(minAngle);

		r.display("New min angle: " + minAngle, 10000);

	}
	
	public ArrayList<Float> spin(int rotationDegrees) {
	    
	    ArrayList<Float> tabDistances= new ArrayList<Float>();

	    // Start rotation
	    r.turn(rotationDegrees);
	    while(r.isMoving()){
	    	float distCm = s.getDistance();
			r.display("D: " + distCm, 200);
	    	tabDistances.add(distCm);	
	    }	    	    
	    return tabDistances;
	}
	
	
	private int findCenterByDerivative(ArrayList<Float> distances) {
        if (distances.size() < 3) return 0;
        
        ArrayList<Float> derivatives = new ArrayList<>();
        
        // Calculate simple derivatives
        for (int i = 1; i < distances.size(); i++) {
            derivatives.add(distances.get(i) - distances.get(i - 1));
        }
        
        // Find where derivative changes from negative to positive (valley bottom)
        for (int i = 1; i < derivatives.size(); i++) {
            if (derivatives.get(i - 1) < 0 && derivatives.get(i) >= 0) {
                return i; // Return index in original array
            }
        }
        
        // Fallback: find minimum derivative (steepest descent)
        int minDerivIndex = 0;
        float minDeriv = derivatives.get(0);
        for (int i = 1; i < derivatives.size(); i++) {
            if (derivatives.get(i) < minDeriv) {
                minDeriv = derivatives.get(i);
                minDerivIndex = i;
            }
        }
        return minDerivIndex;
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
<<<<<<< HEAD
	
	public float[] tabDisc() {
		float[] tab = new float[];
		r.turn(360);
		while(r.isMoving()) {
			
		}
	}
=======

>>>>>>> d1e9f31ca8359760541f789a4180d1a2a9656cbc

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
		r.forward(200);
		Delay.msDelay(500);
		r.turn(20);
		Delay.msDelay(500);
		r.turn(-10);
		Delay.msDelay(500);
		r.turn(-20);
		Delay.msDelay(500);
		r.turn(10);
		r.forward(-200);


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
