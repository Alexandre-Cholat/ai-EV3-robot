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
	private static EV3TouchSensor touch;
	
	// ColorSensor
	private static EV3ColorSensor colorSensor;
	private static SampleProvider colorProvider;
	


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
	
	public static void displayColor() {
        GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
        float[] colorSample = new float[colorProvider.sampleSize()];

        while (Button.ESCAPE.isUp()) {
            colorProvider.fetchSample(colorSample, 0);

            String colorName = convertColor(colorSample);

            // Clear and display
            g.clear();
            g.drawString("Color: " + colorName, 10, 50, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
            Delay.msDelay(200);
        }

        // Cleanup when ESCAPE is pressed
        Sound.beep();
        colorSensor.close();
        System.out.println("Program terminated by user.");
    }
	
	
	public String getColor() {
		colorProvider =  colorSensor.getRGBMode();
		float[] colorSample = new float[colorProvider.sampleSize()];
		while (Button.ESCAPE.isUp()){
			colorProvider.fetchSample(colorSample,0);
			String couleur = convertColor(colorSample);
			return couleur;
		}
		return "rien";
	}
	
	public static String convertColor(float [] tabColor) {
		double r = tabColor[0];
		double v = tabColor[1];
		double b = tabColor[2];
		if ((r>0.03 && r<0.04) && (v>0.06 && v<0.07) && (b>0.03 && b<0.04)) {
			return "Green";
		}
		if ((r>0.08 && r<0.09) && (v>0.015 && v<0.025) && (b>0.015 && b<0.025)) {
			return "Red";
		}
		if ((r>0.01 && r<0.02) && (v>0.02 && v<0.03) && (b>0.045 && b<0.055)) {
			return "Blue"; 
		}
		if ((r>0.055 && r<0.065) && (v>0.045 && v<0.055) && (b>0.06 && b<0.07)) {
			return "Grey";
		}
		if ((r>0.14 && r<0.17) && (v>0.1 && v<0.15) && (b>0.035 && b<0.045)) {
			return "Yellow"; 
		}
		if ((r>0.0075 && r<0.0085) && (v>0.0065 && v<0.0075) && (b>0.0075 && b<0.0085)) {
			return "Back"; 
		}
		if ((r>0.15 && r<0.25) && (v>0.10 && v<0.20) && (b>0.10 && b<0.20)) {
			return "White"; 
		}
		else return "Unknown Color";
	}
	
	
	public static void testTouch() {
		Sensor s = new Sensor();
		int i = 0;
		while(i<4) {
			if(s.isPressed() ){
				Sound.beep();
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
    			Sound.beep();
    	        ultrasonic.disable();
        		g.drawString( "Sensor disabled", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
        		Delay.msDelay(3000);
    	        ultrasonic.close();
    	        break;
    	    }
    	
            
        }
	}
	

	
	public static void main(String[] args) {
		//ultrasonic = new EV3UltrasonicSensor(SensorPort.S4);
		//ultrasonicTest();
		colorSensor = new EV3ColorSensor(SensorPort.S3);
	    colorProvider = colorSensor.getRGBMode();

		displayColor();
		
		

	}

}
