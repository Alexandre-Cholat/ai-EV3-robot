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
	private boolean objDetected=false;

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


	public boolean obj_detected() {
		return objDetected;
	}


	// navigates to center from any position
	public void goToCenter() {
		goToXcenter();
		goToYcenter();
	}

	public void goToYcenter() {
		rotateTo(180);


		align(180,45);

		while (s.getDistance() != table_length / 2) {
			r.forward(s.getDistance() - table_length / 2);
		}
	}

	public void goToXcenter() {
		rotateTo(90);
		align(90,45);

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


	public void align(int startPos) {
		int dist1 = 0;
		int min = 1000;
		float minAngle = startPos;
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
	}

	public boolean smartAlign() {
		float startAng = p.getPosition();
		float sweepAngle = 45;

		while(sweepAngle>10) {
			startAng = p.getPosition();
			align(startAng, sweepAngle);
			sweepAngle = sweepAngle - 15;
		}


		return true;
	}

	public boolean align(float startAng, float sweepAngle) {

		//rotation 360
		//ArrayList<Float> tabDistances = spin(360);

		int turnSpeed = 30;
		//	sweep ( already facing wall )
		r.turn(-(sweepAngle/2), turnSpeed, false);
		r.display("Spinning", 500);
		ArrayList<Float> tabDistances = spin(sweepAngle);
		r.turn(-(sweepAngle/2), turnSpeed, false);	

		r.display("sample nb= " + tabDistances.size());


		//filter and reduce number of distance measurements
		ArrayList<Float> filteredDistances= downsampleToHalfDegree(tabDistances, sweepAngle);
		r.display("reduced nb = " + filteredDistances.size());


		//Calcul de indexe de la valeur la plus proche du mur
		try {

			//int minIdx = findCenterByDerivative(filteredDistances);
			int minIdx = findMinimum(filteredDistances);

			r.display("Best: "+minIdx + " of "+ filteredDistances.size(), 4000);

			// Calcul angle relative de minIdx
			//float minAngle = ((sweepAngle/filteredDistances.size()) * minIdx) - (sweepAngle/2);
			float minAngle = ((sweepAngle/filteredDistances.size()) * minIdx) - (sweepAngle/4);
			r.display("Best rel angle: " + minAngle, 2000);

			float wallAngle =  startAng + minAngle;

			//rotate to smallest distance to wall
			rotateTo(wallAngle);
			p.setAngle(wallAngle);
			r.display("New Position: " + wallAngle, 8000);


		}catch(Exception e) {
			r.display("no derivative found", 2500);
			r.display("tab length =  " + filteredDistances.size(), 2000);
			//try again
			align( startAng, sweepAngle);

		}


		return true;
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

	public void align2() {
		ArrayList<Float> vals = spin(360);

		float minIdx;

	}




	public ArrayList<Float> spin(float rotationDegrees) {

		ArrayList<Float> tabDistances= new ArrayList<Float>();

		int speed = 15;
		r.turn(rotationDegrees, speed, true);
		while(r.isMoving()){
			float distCm = s.getDistance();
			//100 delay time works decent
			//r.display("D: " + distCm, 100);
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

	private int findMinimum(ArrayList<Float> distances) {
		if (distances.isEmpty()) return 0;


		int minIdx = 0;
		float minVal = distances.get(0);

		for (int i = 1; i < distances.size(); i++) {
			if (distances.get(i) < minVal) {
				minVal = distances.get(i);
				minIdx = i;
			}
		}

		return minIdx;
	}

	// for continous sampling (very large distance.size() array), filters distance measurements
	private ArrayList<Float> downsampleToHalfDegree(ArrayList<Float> distances, float sweepAngle) {
		if (distances == null || distances.isEmpty()) {
			return new ArrayList<>();
		}

		// Calculate target number of measurements (one per 0.5 degrees)
		int targetSize = (int)(sweepAngle * 2); // *2 because 1/0.5 = 2

		// If we already have fewer measurements than target, return original
		if (distances.size() <= targetSize) {
			r.display("downsampleToHalfDegree: 2 small");
			return new ArrayList<>(distances);
		}

		// Calculate window size for averaging
		int windowSize = Math.max(1, distances.size() / targetSize);

		ArrayList<Float> downsampled = new ArrayList<>();

		// Apply moving average with calculated window size
		for (int i = 0; i < distances.size(); i += windowSize) {
			float sum = 0;
			int count = 0;

			// Average over the window
			for (int j = i; j < Math.min(i + windowSize, distances.size()); j++) {
				sum += distances.get(j);
				count++;
			}

			if (count > 0) {
				downsampled.add(sum / count);
			}
		}

		return downsampled;
	}





	public void rotate_until_disc_detected() {
		//tourne jusqu'a detecter une discontinuité, renvoie vrai s'il en trouve, false sinon
		float previousDist=s.getDistance();
		for(int angle=0;angle<=360;angle+=5) {
			r.turn(5);
			Delay.msDelay(100);
			float currentDist=s.getDistance();
			if(Math.abs(previousDist-currentDist)>10){//a voir s'il faut valeur plus grande ou plus petite
				objDetected=true;
				r.display("Grab detected",5000);
			}
			previousDist=currentDist;
		}
		objDetected=false;

	}
	/*
	public float[] tabDisc() {
		float[] tab = new float[];
		r.turn(360);
		while(r.isMoving()) {

		}
	}
	 */


	public boolean trajectory(float f) {
		//Dans la mesure où j'ai vraiment besoin de faire un balayage
		// pour retrouver le palet
		int[] angles = { -7, 2, 2, 2, 2, 2 };
		for(int an:angles ) {
			r.turn(an); 
			Delay.msDelay(100);

			if(s.getDistance()<f) {
				return true ;
			}
		}
		return false ;

		//Dans le cas contraire je tourne juste de juste 
		//auquel le robot a l'habitude de se décaler 

		/*r.turn(-5);
		if(s.getDistance()<f) {
			return true ;
		}
		r.turn(10);
		return true ;*/
	}

	public void moveToGrab() {
		float previousDistance = s.getDistance();
		float currentDistance = previousDistance;

		r.pincherOpen();
		r.forward();
		while (!s.isPressed()) {
			// On avance durant environ 200ms 
			Delay.msDelay(200);
			// La distance entre le robot et le palet après avoir avancé pendant 200ms
			currentDistance= s.getDistance();

			//Si currentDistance > Distance à laquelle le robot était il y'a 200 ms 
			if (currentDistance >= previousDistance+2) {
				r.stop();
				r.display("Mauvaise trajectoire",3000);
				trajectory(previousDistance);
				previousDistance= s.getDistance();
			}else{
				//Mise à jour de la distance avant les prochaines 200ms
				previousDistance = currentDistance;
			}
		}
		r.stop();
		r.display("Distance assez proche du pavé",5000);
	}



	public void pickUpGrab() {
		//if (s.getDistance() <= 10) {
		//r.forward(5);
		r.pincherClose();
		r.display("Pavé attrapé", 5000);
		//}

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

	public boolean signe (float f) {
		if(f<0) {
			return false;
		}
		return true;
	}

	public int [] angles_grab (ArrayList <Float> t) {

		int [] angles_grab = new int [9];
		for(int grab =0 ; grab< 9 ; grab++) {

			int i=0 ;
			float val1 = t.get(i);
			float val2= t.get(i+1);
			float dist = val2-val1 ;

			while(i<t.size()-1 && Math.abs(dist)<5) {
				i++;
				float newVal1= val2;
				float newVal2= t.get(i+1);
				dist = newVal2-newVal1 ;
			}
			if(!signe(dist)&& dist >= 15) {
				r.display("Première discontinuité");
			}
			//Recherche de la deuxième discontinuité
			int j=i ;
			float num1 = t.get(j);
			float num2= t.get(j+1);
			float mes = num2-num1;

			while(j<t.size()-1 && Math.abs(mes)<5) {
				j++;
				float newNum1= num2;
				float newNum2= t.get(j+1);
				mes = newNum2-newNum1 ;
			}
			if(!signe(dist)&& dist >= 15) {
				r.display("Deuxième discontinuité");
			}

			angles_grab[grab]= (i+j)/2;
		}
		return angles_grab ;
	}

	public int[] detectPaletAngles(ArrayList<Float> t) {

		//List<Integer> angles = new ArrayList<>();
		int [] angles = new int [9];

		int number=0 ;    
		int i = 0;

		while (i < t.size()-2) {

			float d1 = t.get(i);
			float d2 = t.get(i+1);
			float diff = d2 - d1;

			// Première discontinuité 
			if (diff <= -10) {
				r.display("Première discontinuité");
				int start = i;

				// Deuxième discontinuité
				int j = i+1;
				while (j < t.size() - 1 && Math.abs(t.get(j+1) - t.get(j)) < 5) {
					j++;
				}
				int end = j;
				int angle = (start + end) / 2;
				angles[number]= angle ;
				number++;

				// On cherche le prochain palet
				i = end + 1;
			}
			else {
				i++;
			}
		}

		return angles;
	}


	//}
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
