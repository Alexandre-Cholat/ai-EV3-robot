package ev3;

import lejos.hardware.Button;

public class Main {
	
	
	//automate a etats
	public static void main(String[] args) {
		NavAlgo monRobot = new NavAlgo();
		


		while (Button.ESCAPE.isUp()) {
			
			//monRobot.obj_detected();
			//monRobot.grab();
			//monRobot.calibrateTurn(720);
			
			//monRobot.goToCenter();
			//monRobot.batteryStatus();
			monRobot.calibrateTurn(720);


			
			
		}
		
		
	
	}

}
