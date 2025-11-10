package ev3;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.GraphicsLCD;

public class Robot{
	
	private EV3MediumRegulatedMotor leftMotor;
    private EV3MediumRegulatedMotor rightMotor;
    private EV3MediumRegulatedMotor pincher;
    static boolean pincherOpen;
    
    public Position position;
    public Sensor sensor;  // Composition - Robot HAS-A Sensor


    public Robot() {
        leftMotor = new EV3MediumRegulatedMotor(MotorPort.C);
        rightMotor = new EV3MediumRegulatedMotor(MotorPort.B);
        pincher = new EV3MediumRegulatedMotor(MotorPort.D);
        pincherOpen = false;
        
        //create psoition instance
        this.position = new Position();

        // set speed default
        leftMotor.setSpeed(300);
        rightMotor.setSpeed(300);
        pincher.setSpeed(200);
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
    // ───────────────────────────────────────────────
    //  BASIC MOVEMENTS
    // ───────────────────────────────────────────────

    public void forward() {
        leftMotor.forward();
        rightMotor.forward();
    }

    public void backward() {
        leftMotor.backward();
        rightMotor.backward();
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
    
    
   

	public static void main(String[] args) {
		/*
		Robot r = new Robot();	
		
		r.pincherOpen();
		int [] tab = r.position.getPosition();
		r.display("Angle: " + tab[0], 5000);
		Robot.pincherOpen= true;
		r.pincherClose();
		
		r.close();
		*/
		

	}

}
