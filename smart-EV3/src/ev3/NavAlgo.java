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
import java.util.HashMap;

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

	public void rotateTo(float orientation) {
		float current_a = p.getPosition();
		float calc_turn = orientation - current_a;
		r.turn(calc_turn); //set to synchronous?
		p.setAngle(orientation);
	}

	public boolean align(int startAng) {
		
		//rotation 360
		//ArrayList<Float> tabDistances = spin(360);
	
		
		//	sweep ( already facing wall )
		float sweepAngle = 45;
		r.turn(-(sweepAngle/2), 30, false);
		r.display("Starting Spin", 800);
		ArrayList<Float> tabDistances = spin(sweepAngle);
		r.turn(-(sweepAngle/2), 30, false);		
		
		
		//Calcul de indexe de la valeur la plus proche du mur
		try {
			
			int minIdx = findCenterByDerivative(tabDistances);
			
			r.display("Best: "+minIdx + " of "+ tabDistances.size(), 4000);
			
			// Calcul angle relative de minIdx
			float minAngle = ((sweepAngle/tabDistances.size()) * minIdx) - (sweepAngle/2);
			r.display("Best rel angle: " + minAngle, 2000);

			float wallAngle =  startAng + minAngle;
			
			//rotate to smallest distance to wall
			rotateTo(wallAngle);
			p.setAngle(wallAngle);
			r.display("New Position: " + wallAngle, 8000);


		}catch(Exception e) {
			r.display("no derivative found", 2500);
			r.display("tab length =  " + tabDistances.size(), 2000);
			//try again
			align( startAng);
		}
		
		
		return true;
	}
	
	public ArrayList<Float> spin(float rotationDegrees) {
	    
	    ArrayList<Float> tabDistances= new ArrayList<Float>();
	    
	    int speed = 15;
	    r.turn(rotationDegrees, speed, true);
	    while(r.isMoving()){
	    	float distCm = s.getDistance();
			//100 delay time works decent
	    	r.display("D: " + distCm, 100);
	    	tabDistances.add(distCm);	
	    }
	    
	    // length = 3000 if no delays
	    return tabDistances;
	}
	
	
	private int findCenterByDerivative(ArrayList<Float> distances) throws Exception {
        if (distances.size() < 3) return 0;
        
        ArrayList<Float> derivatives = new ArrayList<>();
        
        // Calculate simple derivatives
        for (int i = 1; i < distances.size(); i++) {
            derivatives.add(distances.get(i) - distances.get(i - 1));
        }
        
        //find local minima
        
        // Find where derivative changes from negative to positive (valley bottom)
        for (int i = 1; i < derivatives.size(); i++) {
            if (derivatives.get(i - 1) < 0 && derivatives.get(i) >= 0) {
            	if (derivatives.get(i - 2) < 0 && derivatives.get(i+1) >= 0) {
            		if (derivatives.get(i - 2) < 0 && derivatives.get(i+2) >= 0) {
            			return i; // Return index in original array
            		}
            		
            	}
                
            }
        }
        
        throw new Exception("no derivative found");

    }
	
	//unused function
	public HashMap<Float, Integer> findLocalMinima(ArrayList<Float> data) {
	    HashMap<Float, Integer> minima = new HashMap<>();
	    
	    if (data == null || data.size() < 3) {
	        return minima; // Return empty map if not enough data
	    }
	    
	    for (int i = 1; i < data.size() - 1; i++) {
	        float prev = data.get(i - 1);
	        float current = data.get(i);
	        float next = data.get(i + 1);
	        
	        // Check if current point is lower than both neighbors
	        if (current < prev && current < next) {
	            minima.put(current, i);
	        }
	    }
	    
	    return minima;
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
	/*
	public float[] tabDisc() {
		float[] tab = new float[];
		r.turn(360);
		while(r.isMoving()) {
			
		}
	}
	*/

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

	public void dist_greater_than_20() {
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
				return;
			}
		}
	}

	public void forwardsTest() {
		r.forward(-50);
	}

	public void calibrateTurn(int x) {
		r.turn(x);

		
		
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
			r.turn(180, 150, true);
		}
		
		

	}

}
