package ev3;

import lejos.hardware.Button;

public class Main {
	
	
	//automate a etats
	public static void main(String[] args) {
		NavAlgo monRobot = new NavAlgo();	
		
		while (Button.ENTER.isUp()) {
			//monRobot.grab();
			
			monRobot.goToCenter();
			
			//monRobot.goToCenter();
			
			

			
			
		}
		
		
	
	}

}
