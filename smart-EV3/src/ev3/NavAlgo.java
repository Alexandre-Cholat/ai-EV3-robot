package ev3;


import lejos.utility.Delay;
import java.util.ArrayList;

import lejos.hardware.Battery;

/**
 * The NavAlgo class provides a comprehensive set of navigation algorithms 
 * for controlling a robot in a predefined environment. It integrates 
 * functionalities for movement, alignment, obstacle avoidance, and object 
 * interaction, leveraging sensor data and position tracking to achieve 
 * precise navigation and task execution.
 * 
 * Key Features:
 * - Navigate to specific positions (e.g., center of the table, opponent's base).
 * - Align the robot to walls or targets using smart alignment algorithms.
 * - Avoid obstacles dynamically during movement.
 * - Detect and interact with objects, such as grabbing and depositing items.
 * - Perform calibration and testing routines for movement and turning.
 * - Monitor battery status and display relevant information.
 * 
 * The class relies on the RobotPilot, Sensor, and Position classes to 
 * interface with the robot's hardware components, including motors, sensors, 
 * and display. It is designed to operate within a rectangular environment 
 * with predefined dimensions.
 * 
 * Usage:
 * Instantiate the NavAlgo class and call the appropriate methods to perform 
 * navigation tasks. The class supports both autonomous and semi-autonomous 
 * operations, with methods for manual calibration and testing.
 * 
 * Note:
 * Ensure that the environment dimensions (table_length and table_width) are 
 * correctly set to match the physical setup. The robot's sensors should be 
 * calibrated for accurate distance and color detection.
 */
public class NavAlgo {

	public RobotPilot r;
	private Sensor s;
	private Position p;
	private boolean objDetected = false;

	// environment dimensions (must be capitalized)!
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

	public boolean obj_detected() {
		//Returns true if an object is detected, false otherwise
		return objDetected;
	}

	// navigates to center from any position
	/**
	 * Moves the robot to the center of the coordinate system by first centering 
	 * it along the Y-axis and then along the X-axis. Displays messages at each 
	 * step to indicate progress.
	 */
	public void goToCenter() {
		goToYcenter();
		r.display("Y is centered", 5000);
		goToXcenter();
		r.display("X is centered", 5000);


	}

	/**
	 * Adjusts the robot's position to align with the center of the Y-axis 
	 * of the table by measuring the distance and moving accordingly.
	 */
	public void goToYcenter() {
		rotateTo(180);

		float sample = s.getDistance();

		r.forward(sample - (table_length / 2), true);

		// aprox abs values: it will never reach perfect mesurement
		while (Math.abs(sample - (table_length / 2)) > 2.5) {
			r.display("D : " + sample, 50);
			sample = s.getDistance();
		}
		/*
		r.forward(true);
		while (Math.abs(sample - table_length / 2) > 5) {
			r.display("D : " + sample, 50);
			sample = s.getDistance();

		}
		 */
		r.stop();
	}


	/**
	 * Moves the robot to the center of the X-axis of the table.
	 * The method aligns the robot, calculates the distance to the center,
	 * and adjusts its position accordingly.
	 */
	public void goToXcenter() {
		rotateTo(90);
		smartAlign();

		r.forward();
		while (Math.abs(s.getDistance() - table_width / 2) > 5) {
			r.forward(s.getDistance() - table_width / 2);
		}
		r.stop();
		rotateTo(180);
	}
	/*Another method that centers the robot on the Y axis and checks, 
	 * using the measured distances, that it is detecting a wall and not an object.
	 */
	public boolean goToYcenter2() {
		rotateTo(180);
		// smartAlign();
		float dist1 = s.getDistance();
		r.turn(-180);
		float dist2 = s.getDistance();
		if (dist1 + dist2 + 20 > table_length) {
			// There is no puck on this path; the robot can move forward.
			r.turn(180);
			while (s.getDistance() != table_length / 2) {
				r.forward(s.getDistance() - table_length / 2);
				return true;
			}
		}
		return false;
	}

	/*Another method that centers the robot on the X axis and checks,
	 * using the measured distances, that it is detecting a wall and not an object.
	 */

	public boolean goToXcenter2() {
		rotateTo(90);
		smartAlign();
		float dist1 = s.getDistance();
		r.turn(-180);
		float dist2 = s.getDistance();
		if (dist1 + dist2 + 20 > table_width) {
			// There is no puck on this path; the robot can move forward.
			r.turn(180);
			while (s.getDistance() != table_width / 2) {
				r.forward(s.getDistance() - table_width / 2);
				return true;
			}
		}
		return false;
	}
	//Method to drop the puck in the opponent’s base
	public void simpleDepot() {
		rotateTo(180);
		// smartAlign();
		float dist1 = s.getDistance();
		r.forward(dist1-12);
		r.pincherOpen();
		r.forward(-25);
		r.pincherClose();
		r.display("Puck dropped");

	}
	/*Method that makes the robot go to the opponent’s base,
	 * using either white line detection or perceived distance
	 * to verify that it has arrived.
	 */
	public void goToBaseAdverse() {
		rotateTo(180);
		boolean estArrivee=false;
		while(!estArrivee) {
			r.forward(s.getDistance()-10, 50, true);
			if (s.getColor().equals("White")) {
				//Case where it detects the white line.
				estArrivee=true;
				r.display("White line detected");
			}
			float f1=s.getDistance();
			/*Apply an offset to the robot to check that
			it is indeed heading toward the opponent’s base and not toward a puck
			 */
			decalageDroite();
			if(Math.abs(s.getDistance()-f1)<5) {
				estArrivee=true;
			}
			/*If the distance increased when it shifted,
			then it was probably detecting a puck or another object
			 */
		}
		//deposit grab
		r.stop();
		r.display("I'm in base adverse");
		r.forward(10);
		r.pincherOpen();
		r.forward(-15);
		r.pincherClose();
		r.display("Puck dropped.");
	}

	public void avoidObstacle() {
		/* The robot shifts either to the right
		or to the left based on the best distance
		to avoid an obstacle
		 */
		r.display("Obstacle avoidance", 500);
		r.turn(-90);
		if (s.getDistance() <= 15) {

			r.forward(15);

			r.turn(90);

			r.forward(20);
			r.display("Obstacle avoided", 500);
		} else {
			r.turn(-180);
			r.forward(15);
			r.turn(90);
			r.forward(20);
			r.display("Obstacle avoided", 500);
		}
	}
	//the robot shifts to the right
	public void decalageDroite() {
		
		r.turn(-90);
		r.forward(15);
		r.turn(90);
	}
	//the robot shifts to the left
	public void decalageGauche() {
		r.turn(90);
		r.forward(15);
		r.turn(-90);
	}

	/**
	 * Rotates the robot to the specified orientation.
	 * 
	 * This method calculates the required turn angle based on the current position
	 * and the desired orientation, then commands the robot to perform the turn.
	 * After the turn, it updates the robot's position data.
	 * 
	 * Functions that call this method:
	 * - navigateTo()
	 * - alignToTarget()
	 * 
	 * Updates the following variables in the position object (p):
	 * - Angle (setAngle)
	 * 
	 * @param orientation The target orientation in degrees to rotate to.
	 */
	public void rotateTo(float orientation) {
		float current_a = p.getPosition();
		float calc_turn = orientation - current_a;
		r.turn(calc_turn); // set to synchronous?
		p.setAngle(orientation);
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

		// Calculation of the index of the value closest to the wall.
		try {

			// int minIdx = findCenterByDerivative(filteredDistances);
			int minIdx = findMinimum(filteredDistances);

			r.display("Best: " + minIdx + " of " + filteredDistances.size(), 4000);

			// Calculation of the relative angle of minIdx.
			// float minAngle = ((sweepAngle/filteredDistances.size()) * minIdx) -
			// (sweepAngle/2);
			float minAngle = ((sweepAngle / filteredDistances.size()) * minIdx) - (sweepAngle / 4);
			r.display("Best rel angle: " + minAngle, 1000);

			float wallAngle = startAng + minAngle;

			// rotate to smallest distance to wall
			if (wallAngle != startAng) {
				rotateTo(wallAngle);
				p.setAngle(wallAngle);
				r.display("New Position: " + wallAngle, 2000);
			} else {
				r.display("Already centered!" + wallAngle, 2000);
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

		// length = 3000+ if no delays
		return tabDistances;
	}

	/**
	 * Finds the index of the center point in a list of distances by identifying a
	 * local minimum where the
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
	 * Finds the index of the minimum value in a list of distances. Simpler
	 * alternative for findCenterByDerivative
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


	/**
	 * Downsamples a list of distance measurements to one measurement per 0.5 degrees
	 * using a moving average approach. This method is useful for reducing the number
	 * of data points in a sweep while maintaining a representative average of the
	 * original data.
	 *
	 * @param distances  The list of distance measurements to be downsampled. Each
	 *                   measurement represents a distance reading at a specific angle.
	 * @param sweepAngle The total sweep angle (in degrees) covered by the distance
	 *                   measurements.
	 * @return A new list of downsampled distance measurements, where the number of
	 *         measurements corresponds to one per 0.5 degrees of the sweep angle.
	 *         Returns an empty list if the input list is null or empty.
	 */
	ArrayList<Float> downsampleToHalfDegree(ArrayList<Float> distances, float sweepAngle) {
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
	/*This method makes the robot rotate degree by degree while calculating distances.
	When a discontinuity is detected (a change in the measured distance of more than 5 cm),
	the robot stops its rotation and turns toward the detected discontinuity.
	 */
	public void rotate_until_disc_detected() {
		float previousDist = s.getDistance();
		for (int angle = 0; angle <= 360; angle += 1) {
			r.turn(1);
			Delay.msDelay(100);
			float currentDist = s.getDistance();
			if (Math.abs(previousDist - currentDist) > 5) {
				objDetected = true;
				r.display("Grab detected in " + angle);
				r.turn(15);
				return;
			}
			previousDist = currentDist;
		}
		objDetected = false;

		r.display("END", 10000);

	}

	/*Method that makes the robot perform a scan
	to check that it is moving in the correct direction, toward a puck
	 */
	public float trajectory(float f) {
		int[] angles = { -7, 3, 3, 3, 3, 2 };
		for(int an:angles ) {
			r.turn(an); 
			Delay.msDelay(100);

			if(s.getDistance() <= f-2) {
				r.display("Good direction", 3000);
				return s.getDistance() ;
			}
		}
		return -1 ;
	}
	/*Method that positions the robot at the ideal distance from the puck,
	 * based either on the touch sensor or on the measured distances.
	 * When the robot is at the correct distance from the puck, it grabs it. 
	 */
	public boolean moveToGrabFacile() {
		float d1 = s.getDistance();
		float d2 = s.getDistance();
		float d = Math.min(d1, d2);
		r.forward(d-20);
		r.stop();

		float[] essais = {22, 10}; 
		for (int i= 0; i < 2; i++) {

			r.pincherOpen();
			r.display("I am facing the puck");
			r.forward(essais[i]);
			r.stop();
			if (s.isPressed()) {
				//case where it is certain that the grab has been caught because the sensor is pressed
				r.pincherClose();
				r.display("I felt the pressure of the puck!");
				return true;
			}
			// case where the distance is close enough to have the puck
			if (s.getDistance() < 25) {
				r.pincherClose();
				r.display("I have the puck between the grippers.");
				return true;
			}
			r.pincherClose();
			r.display("Grab not found yet.");
		}	
		r.forward(-d);
		r.display("Grab not found.");
		return false;
	}


	/*Method assuming the robot is perfectly facing a puck
	 * and will grab it.
	 */
	public void pickUpGrab(){
		r.forward(s.getDistance());
		r.pincherClose();
		r.display("Puck grabbed.", 5000);
	}

	/*if (error >= 3) {
>>>>>>> Stashed changes
				Float newDistance =trajectory(previousDistance);
				if(newDistance !=-1) {
					previousDistance=newDistance;
					error=0;
					//r.forward(); // reprendre la marche
				} else {
					r.stop();
				}
			}

			}*/



	/*
	 * r.pincherOpen();
	 * int [] tab = p.getPosition();
	 * r.display("Angle: " + tab[0], 5000);
	 * Robot.pincherOpen= true;
	 * r.pincherClose();
	 */


	/* Method that will drop the puck at the location
	 * where the robot is when this method is called.
	 */
	public void setDowngrab() {
		r.pincherOpen();
		r.forward(-10);
		r.pincherClose();
		r.display("Puck placed", 5000);
	}
	/*Method to retrieve the first puck during the game,
	 * assuming the robot starts facing a puck and the opponent's base.
	 */
	public void firstGrab() {
		r.pincherOpen();
		r.forward(60);
		pickUpGrab();
		decalageDroite();
		goToBaseAdverse();
	}
	//return the status of battery
	public void batteryStatus() {
		r.display("Battery: " + Battery.getVoltage() + " v", 5000);
	}
	
	/*Method using the ArrayList returned by the spin method
	 * to detect discontinuities, and therefore potential pucks in the measured distances,
	 * assuming that the spin method has completed a full 360-degree rotation.
	 * In the end, the robot turn to the first angle of discontinuity
	 * and return an array of the discontinuity angles
	 */
	public double[] angles_grab(ArrayList<Float> t) {
		double[] angles=new double[9];
		int number = 0;
		int i = 0;

		while (i < t.size() - 2) {
			float d1 = t.get(i);
			float d2 = t.get(i + 1);
			float diff = d1-d2;

			// First discontinuity
			if (diff > 20) {
				r.display("d1 = " + i);
				int j=i+1;
				float diff2=0;
				// Second discontinuity
				while(j<t.size()-2 && (diff2>-10)) {
					float d3 = t.get(j);
					float d4 = t.get(j + 1);
					diff2 = d3-d4;
					j++;
				}
				r.display("d2 = " + j);

				if((j-i>3&&j-i<40)) {
					/*We check the length of the discontinuity to ensure that it
					is likely a puck distance and not a sensor glitch
					 */
					if(number>=9) {
						/*case where the sensor detects more than 9 discontinuities
						 * while there should only be 9 pucks
						 */

						r.display("All the pucks are detected");
						return angles;
					}
					/*we find the middle index of the discontinuity
					 * and convert it into an angle
					 */
					angles[number]=((i+j)/2)*360/t.size();
					r.display(" Add: " + angles[number]);
					number++;
				}

				// Looking for a new grab
				i = j+1;
			} else {
				i++;

			}
		}
		r.display("Turning to " + angles[0]);
		r.turn((float)angles[0]);
		r.display("done");
		return angles;
	}



	/*Method similar to the first version of angle_grab but simpler.
	 * Here, the method only looks for the first discontinuity without 
	 * going through the entire list and returns its angle
	 *  and positions the robot facing this angle.
	 */
	public double angles_grab2(ArrayList<Float> t) {
		double angle = 0;
		int i = 0;

		while (i < t.size() - 2) {
			float d1 = t.get(i);
			float d2 = t.get(i + 1);
			float diff = d1-d2;

			// First discontinuity
			if (diff > 15) {
				r.display("d1 = " + i);
				angle = i*360/t.size();
				r.display("angle :" +angle);
				r.turn((float)angle+5);
				return angle;
			}
		}
		r.display("0 discontinuity detected");
		return 0;
	}

	/*Method that positions the robot facing the smallest
	 * distance detected by the robot during its rotation.
	 */
	public void goToMin(ArrayList<Float> t) {
		float min = t.get(0);

		for (int i = 1; i < t.size(); i++) {
			if (t.get(i) < min) {
				min = i;
			}

		}
		float angle =min*360/t.size();
		r.display("indx min = "+ min);
		r.display("angle :"+angle);
		r.turn(angle);
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



}
