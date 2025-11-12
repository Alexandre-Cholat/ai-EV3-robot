package ev3;

import lejos.hardware.Button;

public class Main {

	public static void main(String[] args) {
		NavAlgo monRobot = new NavAlgo();	
		
		while (Button.ENTER.isUp()) {
			//monRobot.grab();
			
			monRobot.wander2();
			
			

			
			
		}
		
		
	
	}

}
