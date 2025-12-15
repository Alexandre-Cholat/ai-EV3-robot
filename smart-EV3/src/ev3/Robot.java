/**
 * The {@code Robot} class is a controller for an EV3 robot, providing methods 
 * for movement, speed control, sound, display, and pincher operations. The class also includes utility methods for displaying messages 
 * on the EV3 LCD screen and producing sound beeps.
 * 
 * <p><b>Note:</b> This class is no longer used and has been replaced by the 
 * {@code RobotPilot} class, which leverages the {@code MovePilot} class for 
 * more advanced robot navigation/control.</p>
 */
package ev3;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;
import lejos.hardware.BrickFinder;
import lejos.hardware.Sound;
import lejos.hardware.lcd.GraphicsLCD;

// robot movement and output controller
public class Robot {

	// Initialize Position as a property of Robot?
	// private Position p;

	private EV3MediumRegulatedMotor leftMotor;
	private EV3MediumRegulatedMotor rightMotor;
	private EV3MediumRegulatedMotor pincher;
	static boolean pincherOpen;

	// constants
	private static final float WHEEL_DIAMETER_CM = 5.6f; // Standard EV3 wheel diameter
	private static final float WHEEL_CIRCUMFERENCE_CM = (float) (WHEEL_DIAMETER_CM * Math.PI);
	private static final float TRACK_WIDTH_CM = 12.0f; // Distance between wheels
	private static final int DEGREES_PER_CM = (int) (360 / WHEEL_CIRCUMFERENCE_CM);

	// Default speeds
	private int defaultSpeed = 300;
	private int defaultTurnSpeed = 200;
	private int defaultPincherSpeed = 300;

	public Robot() {
		leftMotor = new EV3MediumRegulatedMotor(MotorPort.C);
		rightMotor = new EV3MediumRegulatedMotor(MotorPort.B);
		pincher = new EV3MediumRegulatedMotor(MotorPort.D);
		pincherOpen = false;

		setSpeed(defaultSpeed);
		setTurnSpeed(defaultTurnSpeed);
		pincher.setSpeed(defaultPincherSpeed);

	}
	// ───────────────────────────────────────────────
	// SOUND & DISPLAY
	// ───────────────────────────────────────────────

	//Display the string s on the robot's screen in two seconds.
	public void display(String s) {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.drawString(s, 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
		Delay.msDelay(2000);
		g.clear();

	}
	//Display the string s on the robot's screen and for a duration of ms milliseconds. .
	public void display(String s, int ms_time) {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.drawString(s, 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
		Delay.msDelay(ms_time);
		g.clear();

	}
	//Make the robot beep.
	public void beep() {
		Sound.beep();
	}

	// ───────────────────────────────────────────────
	// BASIC MOVEMENTS
	// ───────────────────────────────────────────────
	//Makes the robot move forward continuously.
	public void forward() {
		leftMotor.forward();
		rightMotor.forward();
	}
	
	//stop the robot
	public void stop() {
		leftMotor.stop(true);
		rightMotor.stop(true);
	}
	
	//turn the robot to the left
	public void turnLeft() {
		leftMotor.backward();
		rightMotor.forward();
	}
	
	//turn the robot to the right
	public void turnRight() {
		leftMotor.forward();
		rightMotor.backward();
	}

	// ───────────────────────────────────────────────
	// SPEED CONTROL METHODS
	// ───────────────────────────────────────────────
	
	//to changed the robot speed
	public void setSpeed(int speed) {
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		// defaultSpeed = speed;
	}
	
	//to changed the robot speed to turn
	public void setTurnSpeed(int speed) {
		defaultTurnSpeed = speed;
	}

	//to changed the pincher speed
	public void setPincherSpeed(int speed) {
		pincher.setSpeed(speed);
		// defaultPincherSpeed = speed;
	}

	//to changed the left motor speed
	public int getLeftMotorSpeed() {
		return leftMotor.getSpeed();
	}
	
	//to changed the right motor speed
	public int getRightMotorSpeed() {
		return rightMotor.getSpeed();
	}

	// ───────────────────────────────────────────────
	// PRECISE MOVEMENT METHODS
	// ───────────────────────────────────────────────
	
	//To move the robot forward by cm centimeters.
	public void forward(float distanceCm) {
		forward(distanceCm, defaultSpeed);
	}
	
	//To move the robot forward by cm centimeters and choose the speed.
	public void forward(float distanceCm, int speed) {
		int degrees = (int) (distanceCm * DEGREES_PER_CM);
		setSpeed(speed);

		leftMotor.rotate(degrees, true);
		rightMotor.rotate(degrees, true);

		leftMotor.waitComplete();
		rightMotor.waitComplete();
	}
	//turn to int degrees
	public void turn(int degrees) {
		turn(degrees, defaultTurnSpeed);
	}
	//turn to int degrees and choose speed
	public void turn(int degrees, int speed) {
		// Calculate wheel rotation needed for the turn
		float wheelDistance = (float) (Math.PI * TRACK_WIDTH_CM * degrees / 360.0);
		int wheelDegrees = (int) (wheelDistance * DEGREES_PER_CM);

		setTurnSpeed(speed);
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);

		leftMotor.rotate(-wheelDegrees, true);
		rightMotor.rotate(wheelDegrees, true);

		leftMotor.waitComplete();
		rightMotor.waitComplete();
	}

	// ───────────────────────────────────────────────
	// TIMED MOVEMENTS
	// ───────────────────────────────────────────────
	
	//To move forward for a certain amount of time.
	public void forwardTimed(int durationMs) {
		forwardTimed(durationMs, defaultSpeed);
	}
	//To move forward for a certain amount of time and choose the speed
	public void forwardTimed(int durationMs, int speed) {
		setSpeed(speed);
		forward();
		Delay.msDelay(durationMs);
		stop();
	}
	//To turn to left for a certain amount of time.
	public void turnLeftTimed(int durationMs) {
		turnLeftTimed(durationMs, defaultTurnSpeed);
	}
	
	//To turn to left for a certain amount of time and choose the speed of rotation.
	public void turnLeftTimed(int durationMs, int speed) {
		setTurnSpeed(speed);
		turnLeft();
		Delay.msDelay(durationMs);
		stop();
	}
	
	//To turn to right for a certain amount of time.
	public void turnRightTimed(int durationMs) {
		turnRightTimed(durationMs, defaultTurnSpeed);
	}

	//To turn to right for a certain amount of time and choose the speed of rotation.
	public void turnRightTimed(int durationMs, int speed) {
		setTurnSpeed(speed);
		turnRight();
		Delay.msDelay(durationMs);
		stop();
	}

	// ───────────────────────────────────────────────
	// PINCHER CONTROL
	// ───────────────────────────────────────────────
	
	//
	public void unPinch(int degrees) {
		pincher.rotate(degrees, true);
		pincher.waitComplete(); // Wait for rotation to finish

	}

	public void pinch(int degrees) {
		pincher.rotate(-degrees, true);
		pincher.waitComplete(); // Wait for rotation to finish

	}
	
	//stop the pincher
	public void stopAux() {
		pincher.stop();
	}
	
	//close the pincher and motors
	public void close() {
		stop();
		stopAux();
		leftMotor.close();
		rightMotor.close();
		pincher.close();

		display("Motors Closed");
		Sound.beep();
	}
	//open the pincher and return false if it's already open
	public boolean pincherOpen() {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();

		if (pincherOpen) {
			g.drawString("Pincher already open", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
			Delay.msDelay(2000);

			return false;
		} else {
			g.drawString("Pincher opening", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
			Delay.msDelay(2000);

			unPinch(1000);
			pincherOpen = true;

			g.clear();

		}
		return true;
	}

	//close the pincher and return false if it's already closed
	public boolean pincherClose() {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();

		if (pincherOpen) {
			g.drawString("Pincher closing", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
			Delay.msDelay(2000);

			pinch(1000);
			pincherOpen = false;
			g.clear();
		} else {
			g.drawString("Pincher already closed", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
			Delay.msDelay(2000);

			return false;
		}

		return true;
	}

}
