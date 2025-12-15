package ev3;

import lejos.hardware.motor.EV3MediumRegulatedMotor;

import lejos.hardware.port.Port;
import lejos.utility.Delay;
import lejos.hardware.BrickFinder;
import lejos.hardware.Sound;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.RegulatedMotor;

public class RobotPilot {

	private Wheel wheel1;
	private Wheel wheel2;
	public Chassis chassis;
	public MovePilot pilot;

	private EV3MediumRegulatedMotor pincher;

	private int defaultPincherSpeed = 300;
	private boolean pincherOpen;

	// for adelie robot chassis_offset, 62 almost perfect, 55 millimeters from
	// center to wheel actual measurement: 61 not enough, 63 is too much
	// for alexandre robot chassis_offset use 65
	double wheel_size = 56;
	double chassis_offset = 62;

	double default_linear_speed = 200;
	double default_angular_Speed = 50;
	double defualt_linear_accel = 100;
	double defualt_angular_accel = 100;

	/**
	 * Initializes the RobotPilot with all necessary motor configurations and
	 * movement capabilities.
	 * 
	 * This constructor sets up the EV3 robot by performing the following
	 * operations:
	 * <ul>
	 * <li>Retrieves port references (A, C, D) from the default EV3 brick</li>
	 * <li>Initializes two large regulated motors (ports A and C) for wheel
	 * control</li>
	 * <li>Initializes a medium regulated motor (port D) as a pincher actuator with
	 * error handling</li>
	 * <li>Configures wheel models with appropriate sizing and chassis offset
	 * parameters</li>
	 * <li>Creates a differential chassis with two wheels for robot locomotion</li>
	 * <li>Establishes a MovePilot for coordinated robot movement control</li>
	 * <li>Sets default speed and acceleration parameters for both linear and
	 * angular motion</li>
	 * </ul>
	 * 
	 * <p>
	 * Motor Assignments:
	 * </p>
	 * <ul>
	 * <li><b>Port A (motorA):</b> Left wheel - offset negatively from chassis
	 * center</li>
	 * <li><b>Port C (motorC):</b> Right wheel - offset positively from chassis
	 * center</li>
	 * <li><b>Port D (pincher):</b> Pincher/gripper mechanism with speed
	 * control</li>
	 * </ul>
	 * 
	 * <p>
	 * Note: If pincher initialization fails, an error message is printed and the
	 * stack trace is logged,
	 * but execution continues without interruption.
	 * </p>
	 * 
	 * @throws Exception Caught internally during pincher initialization; errors are
	 *                   logged but do not prevent object construction
	 */
	public RobotPilot() {
		// Pincher

		Port portD = BrickFinder.getDefault().getPort("D");
		Port portC = BrickFinder.getDefault().getPort("C");
		Port portA = BrickFinder.getDefault().getPort("A");

		RegulatedMotor motorC = new EV3LargeRegulatedMotor(portC);
		RegulatedMotor motorA = new EV3LargeRegulatedMotor(portA);

		try {
			pincher = new EV3MediumRegulatedMotor(portD);
			pincher.setSpeed(defaultPincherSpeed);
			pincherOpen = false;
		} catch (Exception e) {
			System.err.println("Error initializing pincher! ");
			e.printStackTrace();
		}

		// MovePilot for main motors
		this.wheel1 = WheeledChassis.modelWheel(motorA, wheel_size).offset(-chassis_offset);
		this.wheel2 = WheeledChassis.modelWheel(motorC, wheel_size).offset(chassis_offset);
		this.chassis = new WheeledChassis(new Wheel[] { this.wheel1, this.wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);

		this.pilot = new MovePilot(this.chassis);

		this.pilot.setLinearSpeed(default_linear_speed);
		this.pilot.setAngularSpeed(default_angular_Speed);
		this.pilot.setAngularAcceleration(defualt_angular_accel);
		this.pilot.setLinearAcceleration(defualt_linear_accel);

	}

	// ───────────────────────────────────────────────
	// SOUND & DISPLAY
	// ───────────────────────────────────────────────

	/**
	 * Displays a message on the EV3 brick's LCD screen for 2 seconds.
	 * <p>
	 * <b>Note:</b> This method is blocking and will pause program execution for
	 * the duration of the display time (2 seconds).
	 * </p>
	 * 
	 * @param s the string message to display on the EV3 brick's LCD screen
	 * 
	 * @throws NullPointerException if the EV3 brick cannot be found or if {@code s}
	 *                              is null
	 * 
	 * @since 1.0
	 */
	public void display(String s) {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.drawString(s, 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
		Delay.msDelay(2000);
		g.clear();

	}

	/**
	 * Displays a message on the EV3 robot's LCD screen for a specified duration.
	 * 
	 * This method retrieves the graphics LCD display from the EV3 brick, clears any
	 * existing content, and renders the provided string message on the screen. The
	 * message
	 * is positioned at coordinates (0, 0) with vertical center and left horizontal
	 * alignment.
	 * After the specified time interval has elapsed, the display is cleared again.
	 * 
	 * @param s       the text message to display on the LCD screen. Must not be
	 *                null.
	 * @param ms_time the duration in milliseconds to keep the message displayed on
	 *                screen
	 *                before clearing it. Must be a non-negative value.
	 * 
	 * @throws NullPointerException     if the parameter s is null
	 * @throws IllegalArgumentException if ms_time is negative
	 * 
	 * @see lejos.hardware.BrickFinder
	 * @see lejos.hardware.lcd.GraphicsLCD
	 * @see lejos.utility.Delay
	 */
	public void display(String s, int ms_time) {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.clear();
		g.drawString(s, 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
		Delay.msDelay(ms_time);
		g.clear();

	}

	public void beep() {
		Sound.beep();
	}

	// ───────────────────────────────────────────────
	// BASIC MOVEMENTS
	// ───────────────────────────────────────────────

	/**
	 * Moves the robot forward asynchronously (non-blocking) at the current speed
	 * until stopped.
	 */

	public void forward() {
		pilot.forward();
	}

	/**
	 * Moves the robot forward asynchronously (non-blocking) at the current speed.
	 * 
	 * @param b parameter reserved for future synchronous execution control
	 */

	public void forward(boolean b) {
		pilot.forward();
	}

	/**
	 * Moves the robot forward a specified distance in centimeters asynchronously.
	 * 
	 * @param distanceCm the distance to travel in centimeters
	 */

	public void forward(float distanceCm) {
		pilot.travel(distanceCm * 10); // cm to mm
	}

	/**
	 * Moves the robot forward a specified distance at a given speed with optional
	 * synchronous execution.
	 * 
	 * @param distanceCm the distance to travel in centimeters
	 * @param speed      the linear speed in mm/s
	 * @param b          true for synchronous (blocking) execution, false for
	 *                   asynchronous
	 */

	// @param b true for synchronous execution
	public void forward(float distanceCm, int speed, boolean b) {
		setSpeed(speed);
		pilot.travel(distanceCm * 10, b);
	}

	/**
	 * Moves the robot forward a specified distance with optional synchronous
	 * execution at current speed.
	 * 
	 * @param distanceCm the distance to travel in centimeters
	 * @param b          true for synchronous (blocking) execution, false for
	 *                   asynchronous
	 */

	public void forward(float distanceCm, boolean b) {
		pilot.travel(distanceCm * 10, b);
	}

	/**
	 * Rotates the robot by a specified angle in degrees asynchronously.
	 * 
	 * @param degrees the rotation angle in degrees (positive for counterclockwise,
	 *                negative for clockwise)
	 */

	public void turn(float degrees) {
		pilot.rotate(degrees);
	}

	/**
	 * Rotates the robot by a specified angle with optional synchronous execution.
	 * 
	 * @param degrees the rotation angle in degrees
	 * @param b       true for synchronous (blocking) execution, false for
	 *                asynchronous
	 */

	// @param b true for synchronous execution
	public void turn(int degrees, boolean b) {
		pilot.rotate(degrees, b);
	}

	// @param b true for synchronous execution
	public void turn(float degrees, int speed, boolean b) {
		setTurnSpeed(speed);
		pilot.rotate(degrees, b);
	}

	public void stop() {
		pilot.stop();
	}

	// sert à savoir si le robot est en mouvement
	public boolean isMoving() {
		return pilot.isMoving();
	}

	// ───────────────────────────────────────────────
	// SPEED CONTROL METHODS
	// ───────────────────────────────────────────────

	public void setSpeed(int speed) {
		pilot.setLinearSpeed(speed); // in mm/s
	}

	public void setTurnSpeed(int speed) {
		pilot.setAngularSpeed(speed); // degrees/s
	}

	// ───────────────────────────────────────────────
	// PINCHER CONTROL
	// ───────────────────────────────────────────────

	/**
	 * Opens the pincher gripper by rotating it forward by a specified angle.
	 * 
	 * This method performs a synchronous rotation and waits for completion.
	 * 
	 * @param degrees the rotation angle in degrees to open the pincher
	 */

	public void unPinch(int degrees) {
		pincher.rotate(degrees, true);
		pincher.waitComplete(); // Wait for rotation to finish

	}

	/**
	 * Closes the pincher gripper by rotating it backward by a specified angle.
	 * 
	 * This method performs a synchronous rotation and waits for completion.
	 * 
	 * @param degrees the rotation angle in degrees to close the pincher
	 */

	public void pinch(int degrees) {
		pincher.rotate(-degrees, true);
		pincher.waitComplete(); // Wait for rotation to finish

	}

	/**
	 * Opens the pincher gripper with status display on the LCD screen.
	 * 
	 * Displays a message on the LCD indicating the pincher state. If already open,
	 * displays
	 * "Pincher already open" and returns false. Otherwise, displays "Pincher
	 * opening",
	 * opens the pincher by 1200 degrees, updates the internal state, and returns
	 * true.
	 * 
	 * @return true if the pincher was successfully opened, false if already open
	 */

	public boolean pincherOpen() {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();

		if (pincherOpen) {
			g.drawString("Pincher already open", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
			Delay.msDelay(2000);

			return false;
		} else {
			g.drawString("Pincher opening", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
			Delay.msDelay(2000);

			unPinch(1200);
			pincherOpen = true;

			g.clear();

		}
		return true;
	}

	/**
	 * Closes the pincher gripper with status display on the LCD screen.
	 * 
	 * Displays a message on the LCD indicating the pincher state. If already
	 * closed, displays
	 * "Pincher already closed" and returns false. Otherwise, displays "Pincher
	 * closing",
	 * closes the pincher by 1200 degrees, updates the internal state, and returns
	 * true.
	 * 
	 * @return true if the pincher was successfully closed, false if already closed
	 */

	public boolean pincherClose() {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();

		if (pincherOpen) {
			g.drawString("Pincher closing", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
			Delay.msDelay(2000);

			pinch(1200);
			pincherOpen = false;
			g.clear();
		} else {
			g.drawString("Pincher already closed", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
			Delay.msDelay(2000);

			return false;
		}

		return true;
	}

	/**
	 * Immediately stops the pincher motor without changing its state.
	 */

	public void stopPinch() {
		pincher.stop();
	}

}
