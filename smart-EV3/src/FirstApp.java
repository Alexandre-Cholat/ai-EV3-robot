import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.utility.Delay;


public class FirstApp {
	
	public static void main(String[] args) {
		GraphicsLCD g= BrickFinder.getDefault().getGraphicsLCD();
		
		g.drawString("Hello Fuckers", 0, 0, 0);
		
		Delay.msDelay(5000);
		
	}
	

}


