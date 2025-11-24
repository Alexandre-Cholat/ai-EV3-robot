package ev3;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.robotics.navigation.MovePilot;


// robot movement and output controller
public class Robot{
	
	//initialiser Position comme propriete de Robot?
	//    private Position p;

	
	private EV3MediumRegulatedMotor leftMotor;
    private EV3MediumRegulatedMotor rightMotor;
    private EV3MediumRegulatedMotor pincher;
    static boolean pincherOpen;
    private MovePilot movePilot;
    
    // constants
    private static final float WHEEL_DIAMETER_CM = 5.6f; // Standard EV3 wheel diameter
    private static final float WHEEL_CIRCUMFERENCE_CM = (float)(WHEEL_DIAMETER_CM * Math.PI);
    private static final float TRACK_WIDTH_CM = 12.0f; // Distance between wheels
    private static final int DEGREES_PER_CM = (int)(360 / WHEEL_CIRCUMFERENCE_CM);
    
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
    //  SOUND & DISPLAY
    // ───────────────────────────────────────────────

    public void display(String s) {
    	GraphicsLCD g= BrickFinder.getDefault().getGraphicsLCD();
    	g.drawString( s, 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
	    Delay.msDelay(2000);
		g.clear();

    }
    
    
    
    public void display(String s, int ms_time) {
    	GraphicsLCD g= BrickFinder.getDefault().getGraphicsLCD();
    	g.drawString( s, 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
	    Delay.msDelay(ms_time);
		g.clear();

    }
    
    public void beep() {
		Sound.beep();
    }
    
    // ───────────────────────────────────────────────
    //  BASIC MOVEMENTS
    // ───────────────────────────────────────────────

    public void forward() {
        leftMotor.forward();
        rightMotor.forward();
    }

    public void stop() {
        leftMotor.stop(true);
        rightMotor.stop(true);
    }

    public void turnLeft() {
        leftMotor.backward();
        rightMotor.forward();
    }

    public void turnRight() {
        leftMotor.forward();
        rightMotor.backward();
    }
    
   

    // ───────────────────────────────────────────────
    //  SPEED CONTROL METHODS
    // ───────────────────────────────────────────────

    public void setSpeed(int speed) {
        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);
        //defaultSpeed = speed;
    }

    public void setTurnSpeed(int speed) {
        defaultTurnSpeed = speed;
    }

    public void setPincherSpeed(int speed) {
        pincher.setSpeed(speed);
        //defaultPincherSpeed = speed;
    }

    public int getLeftMotorSpeed() {
        return leftMotor.getSpeed();
    }

    public int getRightMotorSpeed() {
        return rightMotor.getSpeed();
    }
    
 // ───────────────────────────────────────────────
    //  PRECISE MOVEMENT METHODS
    // ───────────────────────────────────────────────


    public void forward(float distanceCm) {
        forward(distanceCm, defaultSpeed);
    }

    public void forward(float distanceCm, int speed) {
        int degrees = (int)(distanceCm * DEGREES_PER_CM);
        setSpeed(speed);
       
        leftMotor.rotate(degrees, true);
        rightMotor.rotate(degrees, true);
       
        leftMotor.waitComplete();
        rightMotor.waitComplete();
    }

    
    public void turn(int degrees) {
        turn(degrees, defaultTurnSpeed);
    }

    public void turn(int degrees, int speed) {
        // Calculate wheel rotation needed for the turn
        float wheelDistance = (float)(Math.PI * TRACK_WIDTH_CM * degrees / 360.0);
        int wheelDegrees = (int)(wheelDistance * DEGREES_PER_CM);
        
        setTurnSpeed(speed);
        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);
        
        leftMotor.rotate(-wheelDegrees, true);
        rightMotor.rotate(wheelDegrees, true);
        
        leftMotor.waitComplete();
        rightMotor.waitComplete();
    }
    
    // ───────────────────────────────────────────────
    //  TIMED MOVEMENTS
    // ───────────────────────────────────────────────

    public void forwardTimed(int durationMs) {
        forwardTimed(durationMs, defaultSpeed);
    }

    public void forwardTimed(int durationMs, int speed) {
        setSpeed(speed);
        forward();
        Delay.msDelay(durationMs);
        stop();
    }


    public void turnLeftTimed(int durationMs) {
        turnLeftTimed(durationMs, defaultTurnSpeed);
    }

    public void turnLeftTimed(int durationMs, int speed) {
        setTurnSpeed(speed);
        turnLeft();
        Delay.msDelay(durationMs);
        stop();
    }

    public void turnRightTimed(int durationMs) {
        turnRightTimed(durationMs, defaultTurnSpeed);
    }

    public void turnRightTimed(int durationMs, int speed) {
        setTurnSpeed(speed);
        turnRight();
        Delay.msDelay(durationMs);
        stop();
    }
    

    // ───────────────────────────────────────────────
    //  PINCHER CONTROL
    // ───────────────────────────────────────────────

    public void unPinch(int degrees) {
        pincher.rotate(degrees, true);
        pincher.waitComplete(); // Wait for rotation to finish

    }

    public void pinch(int degrees) {
        pincher.rotate(-degrees, true);
        pincher.waitComplete(); // Wait for rotation to finish

    }

    public void stopAux() {
        pincher.stop();
    }

    public void close() {
        stop();
        stopAux();
        leftMotor.close();
        rightMotor.close();
        pincher.close();

		display("Motors Closed");
        Sound.beep();
    }
    
    public boolean pincherOpen() {
		GraphicsLCD g= BrickFinder.getDefault().getGraphicsLCD();
    	
		if (pincherOpen) {
			g.drawString( "Pincher already open", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
	        Delay.msDelay(2000);
	        
			return false;
		}else {
			g.drawString( "Pincher opening", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);  
	        Delay.msDelay(2000);

			unPinch(1000);
			pincherOpen = true;
			
			g.clear();
			
		}
		return true;
    }
    
    public boolean pincherClose() {
		GraphicsLCD g= BrickFinder.getDefault().getGraphicsLCD();

		if (pincherOpen) {
			g.drawString( "Pincher closing", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);  
	        Delay.msDelay(2000);

			pinch(1000);
			pincherOpen = false;
			g.clear();
		}else {
			g.drawString( "Pincher already closed", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
	        Delay.msDelay(2000);

			return false;
		}
		
		return true;
    }

}
