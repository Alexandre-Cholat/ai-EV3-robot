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
			monRobot.goToCenter();
			monRobot.rotate_until_disc_detected();
			if (monRobot.getObjDetecter()) {
				monRobot.moveToGrab();
				monRobot.pickUpGrab();
				//ensuite retourner a la base et lacher le palet
				monRobot.goToBase();
			}
			else {
				//s'il trouve rien c'est que tout les palets on été récupéré normalement
			}
			
			
			monRobot.batteryStatus();
			monRobot.calibrateTurn(720);

		
		}
		
		
	
	}

}
