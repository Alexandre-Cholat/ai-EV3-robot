package ev3;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.utility.Delay;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.RegulatedMotor;

import lejos.hardware.motor.MotorRegulator;
import lejos.internal.ev3.EV3MotorPort;

// Movement with the MovePilot Class: lejos.robotics.navigation.MovePilot
public class RobotPilot {

	private Wheel wheel1;
	private Wheel wheel2;
	public Chassis chassis; 
	public MovePilot pilot;
	
    private EV3MediumRegulatedMotor pincher;

    private int defaultPincherSpeed = 300;
	private boolean pincherOpen;
	
	//for adelie robot chassis_offset, 62 almost perfect, 55 millimeters from center to wheel actual measurement: 61 not enough, 63 is too much
	// for alexandre robot chassis_offset use 65 
	double wheel_size = 56;
	double chassis_offset = 65; 
	
	double default_linear_speed = 200;
	double default_angular_Speed = 50;
	double defualt_linear_accel = 100;
	double defualt_angular_accel = 100;

	
	
	
	public RobotPilot(){
		// Pincher
		
		Port portD = BrickFinder.getDefault().getPort("D");
		Port portC = BrickFinder.getDefault().getPort("C");
		Port portA = BrickFinder.getDefault().getPort("A");
		
		RegulatedMotor motorC = new EV3LargeRegulatedMotor(portC);
		RegulatedMotor motorA = new EV3LargeRegulatedMotor(portA);

		
		try{
		    pincher = new EV3MediumRegulatedMotor(portD);
		    pincher.setSpeed(defaultPincherSpeed);
		    pincherOpen = false;
	    }catch (Exception e){
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
		g.clear();
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
	    pilot.forward();
	}
	
	public void forward(boolean b) {
	    pilot.forward();
	}
	
	public void forward(float distanceCm) {
	    pilot.travel(distanceCm * 10); // cm to mm
	}
	
	// @param b true for synchronous execution
	public void forward(float distanceCm, int speed, boolean b) {
	    setSpeed(speed);
	    pilot.travel(distanceCm * 10, b);
	}

	public void turn(float degrees) {
	    pilot.rotate(degrees);
	}
	
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
	
	//sert à savoir si le robot est en mouvement
	public boolean isMoving() {
	    return pilot.isMoving();
	}
	
	// ───────────────────────────────────────────────
	//  SPEED CONTROL METHODS
	// ───────────────────────────────────────────────
	
	public void setSpeed(int speed) {
	    pilot.setLinearSpeed(speed); // in mm/s
	}
	
	public void setTurnSpeed(int speed) {
	    pilot.setAngularSpeed(speed); // degrees/s
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
    
    public void stopPinch() {
        pincher.stop();
    }

	
}
