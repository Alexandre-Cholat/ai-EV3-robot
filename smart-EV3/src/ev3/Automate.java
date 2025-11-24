package ev3;

public class Automate extends NavAlgo {
	RobotPilot r = new RobotPilot(); 
	Sensor s= new Sensor ();
	Position p= new Position();
	NavAlgo play = new NavAlgo(r,s,p);

	public void play() {

		int grab = 0;

		boolean center      = false;
		boolean objDetected = false;
		boolean picking     = false;
		boolean attrape     = false;

		while (grab < 9) {

			// ETAPE 1 : ALLER AU CENTRE
			if (!center) {
				r.display("Going to center");
				goToCenter();

				// OBJET TROUVÉ AVANT LE CENTRE
				if (obj_detected()) {
					objDetected = true;   //ON PASSE A L ETAPE 3
				}
				center = true;
			}
			// ETAPE 2 :CHERCHER OBJET
			else if (!objDetected && !picking) {

				r.display("Searching object");

				rotate_until_disc_detected();

				if (obj_detected()) {
					objDetected = true;
				}
			}
			// ETAPE 3:OBJET TROUVE
			else if (objDetected && !picking) {

				r.display("Approaching object");

				boolean reached = play.moveToGrab();

				// OBJET PERDU : RETOUR A LA RECHERCHE
				if (!obj_detected()) {
					objDetected = false;
				}
				else if (reached) {
					picking = true;
				}
			}
			// ATTRAPER LE PALET
			else if (picking && !attrape) {

				r.display("Grabbing object");
				pickUpGrab();
				attrape = true;
			}
			// ETAPE 4 : RETOUR AU CAMP ADVERSE
			else if (attrape) {
				r.display("Returning to base");
				play.setDowngrab();
				play.goToBaseAdverse();
				grab++;

				// C'EST REPARTI POUR UN TOUR 
				center = false;
				objDetected = false;
				picking = false;
				attrape = false;
			}
		}
		r.display("Mission accomplie !");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
