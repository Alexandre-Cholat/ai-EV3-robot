package ev3;

import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.GraphicsLCD;

import java.util.Arrays;

import lejos.hardware.*;
import lejos.hardware.Button;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.Sound;
import lejos.utility.Delay;


public class Sensor {
	
	// UltrasonicSensor
	private static EV3UltrasonicSensor ultrasonic;

	// TouchSensor
	private EV3TouchSensor touch;
	
	// ColorSensor
	private EV3ColorSensor colorSensor;
	


	// constructor
	public Sensor() {
		touch = new EV3TouchSensor (SensorPort.S2);
		ultrasonic = new EV3UltrasonicSensor(SensorPort.S4);
		colorSensor = new EV3ColorSensor(SensorPort.S3);
	}
	
	
	// true if touch sensor is pressed
	public boolean isPressed()	{
		float[] sample = new float[1];
		touch.fetchSample(sample, 0);
		return sample[0] != 0;
	}
	
	public static void testTouch() {
		Sensor s = new Sensor();
		int i = 0;
		while(i<4) {
			if(s.isPressed() ){
				i++;
				GraphicsLCD g= BrickFinder.getDefault().getGraphicsLCD();
				g.drawString( "Touched "+ i+" times", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
				Delay.msDelay(3000);
				g.clear();		
			}
		}
		
		GraphicsLCD g= BrickFinder.getDefault().getGraphicsLCD();
		g.drawString( "Touched 4 times", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
		Delay.msDelay(2000);
		g.clear();
		g.drawString("Bye...", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
		Delay.msDelay(3000);
	}
	
	public static void ultrasonicTest() {
		// Setup sensor
        ultrasonic.enable();
        
        // Get distance mode
        SampleProvider distance = ultrasonic.getDistanceMode();
        float[] sample = new float[distance.sampleSize()];
        
        // 3. Loop forever (or until some condition) to read and display
        while (true) {
            distance.fetchSample(sample, 0);
            float distMeters = sample[0];   // distance in metres
            // Convert to more convenient unit if you like:
            float distCm = distMeters * 100.0f;
            
            GraphicsLCD g= BrickFinder.getDefault().getGraphicsLCD();
    		g.drawString( "\"Distance:"+distCm, 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
                     
            Delay.msDelay(200);
    		g.clear();
            
    		if (Button.ENTER.isDown()) {
    	        ultrasonic.disable();
        		g.drawString( "Sensor disabled", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
        		Delay.msDelay(3000);
    	        ultrasonic.close();
    	        break;
    	    }
    	
            
        }
	}
	

	
	public static void main(String[] args) {
		ultrasonic = new EV3UltrasonicSensor(SensorPort.S4);
		ultrasonicTest();
		
		

	}

}
