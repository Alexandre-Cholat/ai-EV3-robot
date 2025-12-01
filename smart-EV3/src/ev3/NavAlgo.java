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
	private boolean objDetected = false;

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
	/*
	 * public NavAlgo(RobotPilot r , Sensor s , Position p) {
	 * this.r = r;
	 * this.s = s;
	 * this.p = p;
	 * }
	 */

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

		align(180);

		while (s.getDistance() != table_length / 2) {
			r.forward(s.getDistance() - table_length / 2);
		}
	}

	public boolean goToYcenter2() {
		// cette methode verifie qu'il detecte bien un mur et pas un objet
		rotateTo(180);
		align(180);
		float dist1 = s.getDistance();
		r.turn(-180);
		float dist2 = s.getDistance();
		if (dist1 + dist2 + 20 > table_length) {
			// il n'y a pas de palet sur cette trajectoire, le robot peut avancer
			r.turn(180);
			while (s.getDistance() != table_length / 2) {
				r.forward(s.getDistance() - table_length / 2);
				return true;
			}
		}
		return false;
	}

	public void goToXcenter() {
		rotateTo(90);
		align(90);

		while (s.getDistance() != table_width / 2) {
			r.forward(s.getDistance() - table_width / 2);
		}
	}

	public boolean goToXcenter2() {
		// cette methode verifie qu'il detecte bien un mur et pas un objet
		rotateTo(90);
		align(90);
		float dist1 = s.getDistance();
		r.turn(-180);
		float dist2 = s.getDistance();
		if (dist1 + dist2 + 20 > table_width) {
			// il n'y a pas de palet sur cette trajectoire, le robot peut avancer
			r.turn(180);
			while (s.getDistance() != table_width / 2) {
				r.forward(s.getDistance() - table_width / 2);
				return true;
			}
		}
		return false;
	}

	public void goToBaseAdverse() {
		rotateTo(180);
		r.forward(table_length - 20, 50, true);
		while (s.getColor().equals("White")) {
			r.forward(table_length - 20, 50, true);
			if (s.getDistance() < 10) {
				// cas ou le robot voit un obstacle, il l'évite
				r.stop();
				r.display("Obstacle detecte", 800);
				avoidObstacle();
				r.forward();
			}
			if (s.getColor().equals("White")) {
				r.stop();
				r.display("Ligne blanche detectee", 1000);
				r.forward(10);
				r.pincherOpen();
				r.forward(-(table_length / 2 - 10));
				r.pincherClose();
			}
		}
	}

	public void avoidObstacle() {
		// le robot de décale pour éviter un obstacle
		r.display("Evitement d'un obstacle", 500);

		r.turn(-90);
		if (s.getDistance() <= 15) {

			r.forward(15);

			r.turn(90);

			r.forward(20);
			r.display("Obstacle evite", 500);
		} else {
			r.turn(-180);
			r.forward(15);
			r.turn(90);
			r.forward(20);
			r.display("Obstacle evite", 500);
		}
	}

	public void rotateTo(float orientation) {
		float current_a = p.getPosition();
		float calc_turn = orientation - current_a;
		r.turn(calc_turn); // set to synchronous?
		p.setAngle(orientation);
	}

	public void align(int startPos) {
		int dist1 = 0;
		int min = 1000;
		float minAngle = startPos;
		// minimise distance between wall

		int i = 0;

		while (i < 10) {

			// rotate to random angle
			int randAngle = (int) (Math.random() * 7);
			rotateTo(startPos);

			for (int j = 0; j < 10; i++) {
				randAngle = (int) (Math.random() * 7);
				r.turn(randAngle);

				dist1 = (int) s.getDistance();

				// new best candidate
				if (dist1 < min) {
					min = dist1;
					minAngle = p.getPosition();

					r.display("New min angle: " + minAngle);
				}

				// return to starting position center
				rotateTo(startPos);
			}

			// rotate to smallest distance to wall
			rotateTo(minAngle);

			// set minAngle as intended start angle
			if (dist1 < min) {
				min = dist1;
				minAngle = p.getPosition();
				r.display("New min angle: " + minAngle);
			}

			rotateTo(startPos);
		}
	}

	/**
	 * Aligns the robot to a target position using a smart alignment algorithm.
	 * The method starts with a specified sweep angle and iteratively reduces the
	 * sweep angle until it reaches a minimum threshold. At each step, it aligns
	 * the robot based on the current position and the sweep angle.
	 *
	 * @return true if the alignment process completes successfully.
	 */
	public boolean smartAlign() {
		float startAng = p.getPosition();
		float sweepAngle = 45;

		while (sweepAngle > 10) {
			startAng = p.getPosition();
			if (align(startAng, sweepAngle)) {
				sweepAngle = sweepAngle - 10;
			}

		}

		return true;
	}

	// ArrayList<Float> tabDistances = spin(360);

	/**
	 * Aligns the robot to face the closest wall within a specified sweep angle.
	 *
	 * This method performs the following steps:
	 * 1. Rotates the robot to perform a sweep within the given sweep angle.
	 * 2. Collects distance measurements during the sweep.
	 * 3. Filters and reduces the number of distance measurements for processing.
	 * 4. Identifies the angle corresponding to the closest wall.
	 * 5. Rotates the robot to align with the closest wall, if necessary.
	 *
	 * If an error occurs during the alignment process, the method retries the
	 * alignment.
	 *
	 * @param startAng   The starting angle of the robot before the sweep.
	 * @param sweepAngle The angle (in degrees) over which the robot sweeps to
	 *                   detect walls.
	 * @return returns true if alignment completes successfully.
	 */
	public boolean align(float startAng, float sweepAngle) {

		// rotation 360
		// ArrayList<Float> tabDistances = spin(360);

		int turnSpeed = 30;
		// sweep ( already facing wall )
		r.turn(-(sweepAngle / 2), turnSpeed, false);
		r.display("Spinning", 500);
		ArrayList<Float> tabDistances = spin(sweepAngle);
		r.turn(-(sweepAngle / 2), turnSpeed, false);

		// filter and reduce number of distance measurements
		ArrayList<Float> filteredDistances = downsampleToHalfDegree(tabDistances, sweepAngle);
		r.display("reduced nb = " + filteredDistances.size());

		// Calcul de indexe de la valeur la plus proche du mur
		try {

			// int minIdx = findCenterByDerivative(filteredDistances);
			int minIdx = findMinimum(filteredDistances);

			r.display("Best: " + minIdx + " of " + filteredDistances.size(), 4000);

			// Calcul angle relative de minIdx
			// float minAngle = ((sweepAngle/filteredDistances.size()) * minIdx) -
			// (sweepAngle/2);
			float minAngle = ((sweepAngle / filteredDistances.size()) * minIdx) - (sweepAngle / 4);
			r.display("Best rel angle: " + minAngle, 2000);

			float wallAngle = startAng + minAngle;

			// rotate to smallest distance to wall
			if (wallAngle != startAng) {
				rotateTo(wallAngle);
				p.setAngle(wallAngle);
				r.display("New Position: " + wallAngle, 8000);
			} else {
				r.display("Already centered!" + wallAngle, 8000);
			}

		} catch (Exception e) {
			r.display("no derivative found", 2500);
			r.display("tab length =  " + filteredDistances.size(), 2000);
			// try again
			align(startAng, sweepAngle);

		}

		return true;
	}

	/**
	 * Spins the robot by a specified number of degrees while collecting distance
	 * measurements during the rotation.
	 *
	 * @param rotationDegrees The number of degrees to rotate the robot. Positive
	 *                        values indicate clockwise rotation, and negative
	 *                        values indicate counterclockwise rotation.
	 * @return An ArrayList of Float values representing the distances measured
	 *         during the rotation. Each distance is collected at intervals while
	 *         the robot is moving.
	 */
	public ArrayList<Float> spin(float rotationDegrees) {

		ArrayList<Float> tabDistances = new ArrayList<Float>();

		int speed = 15;
		r.turn(rotationDegrees, speed, true);
		while (r.isMoving()) {
			float distCm = s.getDistance();
			// 100 delay time works decent
			// r.display("D: " + distCm, 100);
			tabDistances.add(distCm);
		}

		// length = 3000 if no delays
		return tabDistances;
	}

	/**
	 * Finds the index of the center point in a list of distances by identifying a local minimum where the
	 * derivative changes from negative to positive, indicating a valley bottom.
	 * 
	 * 
	 *
	 * @param distances An ArrayList of Float values representing distances.
	 *                  The list must contain at least 3 elements.
	 * @return The index of the center point in the original distances list where
	 *         a local minimum is detected.
	 * @throws Exception If no local minimum is found in the distances list.
	 */
	private int findCenterByDerivative(ArrayList<Float> distances) throws Exception {
		if (distances.size() < 3)
			return 0;

		ArrayList<Float> derivatives = new ArrayList<>();

		// Calculate simple derivatives
		for (int i = 1; i < distances.size(); i++) {
			derivatives.add(distances.get(i) - distances.get(i - 1));
		}

		// find local minima

		// Find where derivative changes from negative to positive (valley bottom)
		for (int i = 1; i < derivatives.size(); i++) {
			if (derivatives.get(i - 1) < 0 && derivatives.get(i) >= 0) {
				if (derivatives.get(i - 2) < 0 && derivatives.get(i + 1) >= 0) {
					if (derivatives.get(i - 2) < 0 && derivatives.get(i + 2) >= 0) {
						return i; // Return index in original array
					}

				}

			}
		}

		throw new Exception("no derivative found");

	}

	/**
	 * Finds the index of the minimum value in a list of distances. Simpler alternative for findCenterByDerivative 
	 *
	 * @param distances An ArrayList of Float values representing distances.
	 *                  The list must not be null, but it can be empty.
	 * @return The index of the smallest value in the list. If the list is empty,
	 *         the method returns 0.
	 */
	private int findMinimum(ArrayList<Float> distances) {
		if (distances.isEmpty())
			return 0;

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

	// for continous sampling (very large distance.size() array), filters distance
	// measurements
	private ArrayList<Float> downsampleToHalfDegree(ArrayList<Float> distances, float sweepAngle) {
		if (distances == null || distances.isEmpty()) {
			return new ArrayList<>();
		}

		// Calculate target number of measurements (one per 0.5 degrees)
		int targetSize = (int) (sweepAngle * 2); // *2 because 1/0.5 = 2

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
		// tourne jusqu'a detecter une discontinuité, renvoie vrai s'il en trouve, false
		// sinon
		float previousDist = s.getDistance();
		for (int angle = 0; angle <= 360; angle += 5) {
			r.turn(5);
			Delay.msDelay(100);
			float currentDist = s.getDistance();
			if (Math.abs(previousDist - currentDist) > 10) {// a voir s'il faut valeur plus grande ou plus petite
				objDetected = true;
				r.display("Grab detected", 5000);
			}
			previousDist = currentDist;
		}
		objDetected = false;

	}

	/*
	 * public float[] tabDisc() {
	 * float[] tab = new float[];
	 * r.turn(360);
	 * while(r.isMoving()) {
	 * 
	 * }
	 * }
	 */
	public boolean moveToGrab() {
		float previousDistance = s.getDistance();
		float currentDistance = previousDistance;
		int error = 0;
		r.pincherOpen();
		r.forward();
		while (!s.isPressed()) {
			// Moving forward for approximately 200ms
			Delay.msDelay(200);
			// Distance between robot and grab after moving during 200ms
			currentDistance = s.getDistance();

			// If currentDistance > distance to which the robot was 200ms
			if (currentDistance >= previousDistance + 2) {
				error++;
			}
			if (error >= 3) {
				r.stop();
				r.display("Mauvaise trajectoire", 3000);
				return false;
			}
			// Update of the distance before the following 200ms
			previousDistance = currentDistance;
		}

		r.stop();
		r.display("Distance assez proche du pavé", 5000);
		return true;
	}

	public void pickUpGrab() {
		// if (s.getDistance() <= 10) {
		// r.forward(5);
		r.pincherClose();
		r.display("Pavé attrapé", 5000);
		// }

		/*
		 * r.pincherOpen();
		 * int [] tab = p.getPosition();
		 * r.display("Angle: " + tab[0], 5000);
		 * Robot.pincherOpen= true;
		 * r.pincherClose();
		 */

	}

	public void setDowngrab() {
		// méthode qui va deposer le palet et reculer et fermer les pinces
		r.pincherOpen();
		r.forward(-10);
		r.pincherClose();
		r.display("Pavé déposer", 5000);
	}

	public void batteryStatus() {
		r.display("Battery: " + Battery.getVoltage() + " v", 5000);
	}

	public double[] angles_grab(ArrayList<Float> t) {
		double[] angles = new double[9];
		int number = 0;
		int i = 0;

		while (i < t.size() - 2) {
			float d1 = t.get(i);
			float d2 = t.get(i + 1);
			float diff = d2 - d1;

			// First discontinuity
			if (diff <= -10) {
				r.display("Première discontinuité");
				int start = i;

				// Second discontinuity
				int j = i + 1;
				while (j < t.size() - 1 && Math.abs(t.get(j + 1) - t.get(j)) < 5) {
					j++;
				}
				r.display("Deuxième discontinuité");
				int end = j;
				double angle = ((start + end) / 2) * 0.5;
				angles[number] = angle;
				number++;

				// Looking for a new grab
				i = end + 1;
			} else {
				i++;
			}
		}

		return angles;
	}

	// }
	// ────────────────
	// TESTING FUNCTIONS
	// ────────────────

	public void wander() {
		while (s.getDistance() > 10) {
			r.forward();
		}
		r.turn(1000000); // infinite turn
		long rand = (long) (Math.random() * 10000);
		Delay.msDelay(rand);
		// stop
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
		for (int i = 0; i < 2; i++) {
			r.forward(100);
			Delay.msDelay(500);
			r.turn(180, 150, true);
		}

	}

}
