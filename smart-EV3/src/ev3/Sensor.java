package ev3;

import java.util.Arrays;

import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.Button;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.hardware.Sound;
import lejos.utility.Delay;


// sensor controller
public class Sensor{
	
	// UltrasonicSensor
	private EV3UltrasonicSensor ultrasonic;

	// TouchSensor
	private EV3TouchSensor touch;
	
	// ColorSensor
	private EV3ColorSensor colorSensor;
	private SampleProvider colorProvider;
	


	// constructor
	public Sensor() {
		Port port2 = BrickFinder.getDefault().getPort("S2");
		Port port4 = BrickFinder.getDefault().getPort("S4");
		Port port3 = BrickFinder.getDefault().getPort("S3");
				
		ultrasonic = new EV3UltrasonicSensor(port4);
		touch = new EV3TouchSensor(port2);
		colorSensor = new EV3ColorSensor(port3);
		colorProvider = colorSensor.getRGBMode();
	}
	
	
	// true if touch sensor is pressed
	public boolean isPressed()	{
		float[] sample = new float[1];
		touch.fetchSample(sample, 0);
		return sample[0] != 0;
	}
	
	public void displayColor() {
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
		colorProvider.fetchSample(colorSample, 0);
        return convertColor(colorSample);
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
	
	
	public void testTouch() {
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
	
	public float getDistance() {
        ultrasonic.enable();
        SampleProvider distance = ultrasonic.getDistanceMode();
        float[] sample = new float[distance.sampleSize()];
        distance.fetchSample(sample, 0);
        return sample[0] * 100.0f;  // Convert to cm
    }
	
	/**
	 * Permet de regarder une valeur devant le robot et de la ranger dans le tableau
	 * fourni en paramètres en l'aggrandissant d'une case.
	 * @param tab le tableau ayant déjà (ou pas) des données.
	 * @return un tableau de float ayant tous les éléments du tableau d'origine 
	 * avec la distance lue devant le robot en dernier élément.
	 */
	public float[] look(float[] tab) {
		float[] newTab = Arrays.copyOf(tab, tab.length+1);
		ultrasonic.fetchSample(newTab, newTab.length-1);
		Delay.msDelay(20);
		return newTab;
	}
	
	// cas ou pas de tableau initee
	public float[] look() {
		float[] tab = new float[0];
		float[] newTab = Arrays.copyOf(tab, tab.length+1);
		ultrasonic.fetchSample(newTab, newTab.length-1);
		Delay.msDelay(20);
		return newTab;
	}
	
		
	// Cleanup method
    public void close() {
        ultrasonic.close();
        touch.close();
        colorSensor.close();
    }

}
