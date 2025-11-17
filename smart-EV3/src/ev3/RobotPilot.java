package ev3;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.MotorPort;
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
	private Chassis chassis; 
	private MovePilot pilot;
	
    private EV3MediumRegulatedMotor pincher;

    private int defaultPincherSpeed = 300;
	boolean pincherOpen;
	
	
	
	double wheel_size = 56;
	float chassis_size = 5.5;
	
	
	
	public RobotPilot(){
		//MovePilot for main motors
		Wheel wheel1 = WheeledChassis.modelWheel(Motor.C, wheel_size).offset(-70);
		Wheel wheel2 = WheeledChassis.modelWheel(Motor.B, wheel_size).offset(70);
		Chassis chassis = new WheeledChassis(new Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL); 
		MovePilot pilot = new MovePilot(chassis);
		
		//Pincher
		pincher = new EV3MediumRegulatedMotor(MotorPort.D);
        pincher.setSpeed(defaultPincherSpeed);
        pincherOpen = false;
      
    }
	
	
}
