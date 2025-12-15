package ev3;

import java.util.ArrayList;

import lejos.hardware.Button;

public class Main {
	
	
	//automate a etats
	public static void main(String[] args) {
		NavAlgo monRobot = new NavAlgo();
		//Automate play = new Automate ();
		
		//play.play();
		monRobot.goToCenter();
		monRobot.rotate_until_disc_detected();
		
		if(monRobot.moveToGrabFacile()) {
			monRobot.simpleDepot();
			monRobot.goToCenter();
		}
				monRobot.goToBaseAdverse();
				monRobot.setDowngrab();
				
		//ArrayList<Float> map = monRobot.downsampleToHalfDegree(monRobot.spin(360), 360);
		
		//monRobot.angles_grab(map);

		
		

		
		Etat etat=Etat.CHERCHE_MILIEU;


		while (Button.ESCAPE.isUp()) {
			
			//monRobot.obj_detected();
			
			//monRobot.moveToGrab();
			// monRobot.goToXcenter2();
			//monRobot.angles_grab(monRobot.spin(90));	
						
			//monRobot.calibrateTurn(720);
			
			//monRobot.goToCenter();
			switch(etat) {
			case CHERCHE_MILIEU:
				//instructions...
				break;
			case CHERCHE_OBJET:
				//instructions...
				break;
			case OBJET_TROUVE:
				//instructions...
				break;
			case RAMENER_OBJET:
				//instructions...
				break;
			}
			
			monRobot.batteryStatus();
			//monRobot.errorCalc();

			//monRobot.calibrateTurn(720);

		
		}
		
		
	
	}

}
