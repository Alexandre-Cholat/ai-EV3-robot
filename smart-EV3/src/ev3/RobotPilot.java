package ev3;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.utility.Delay;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;

// Movement with the MovePilot Class: lejos.robotics.navigation.MovePilot
public class RobotPilot {

	private Wheel wheel1;
	private Wheel wheel2;
	public Chassis chassis; 
	public MovePilot pilot;
	
    private EV3MediumRegulatedMotor pincher;

    private int defaultPincherSpeed = 300;
	private boolean pincherOpen;
	
	
	
	double wheel_size = 56;
	double chassis_offset = 62; //55 millimeters from center to wheel actual measurement: 60 not enough, 65 is too much, 62 slightly too much
	
	
	
	
	public RobotPilot(){
	    // MovePilot for main motors
	    this.wheel1 = WheeledChassis.modelWheel(Motor.C, wheel_size).offset(-chassis_offset);
	    this.wheel2 = WheeledChassis.modelWheel(Motor.B, wheel_size).offset(chassis_offset);
	    this.chassis = new WheeledChassis(new Wheel[] { this.wheel1, this.wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL); 
	    this.pilot = new MovePilot(this.chassis);
	    
	    // Pincher
	    try {
		    pincher = new EV3MediumRegulatedMotor(MotorPort.D);
		    pincher.setSpeed(defaultPincherSpeed);
		    pincherOpen = false;
	    }catch (Exception e){
	    	System.err.println("Error initializing pincher! ");
	        e.printStackTrace();
	    }
	    
	    
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
	
	public void forward(float distanceCm) {
	    pilot.travel(distanceCm * 10); // cm to mm
	}

	public void forward(float distanceCm, int speed) {
	    setSpeed(speed);
	    pilot.travel(distanceCm * 10);
	}

	public void turn(int degrees) {
	    pilot.rotate(degrees);
	}

	public void turn(int degrees, int speed) {
	    setTurnSpeed(speed);
	    pilot.rotate(degrees);
	}
	
	public void stop() {
	    pilot.stop();
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
