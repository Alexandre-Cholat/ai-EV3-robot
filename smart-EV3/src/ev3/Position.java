/**
 * The Position class is responsible for representing and managing the 
 * robot's position information. It provides the necessary data to other 
 * components, such as the NavAlgo class, to facilitate navigation and 
 * decision-making processes. This class encapsulates the robot's 
 *  orientation, for tracking 
 * of its location during operation.
 */
package ev3;

public class Position {
	// 180 pour camp adversaire, 0 pour zone d'equipe
	public float angle;

	public boolean perdu;

	// supposons le robot commence face au camp adversaire, n'importe ou.
	public Position() {
		angle = 180;
		perdu = true;
	}

	// check if x,y position is known
	public boolean getPerdu() {
		return perdu;
	}

	public void setPerdu(boolean b) {
		perdu = b;
	}

	public float getPosition() {
		return angle;
	}

	public void setAngle(float i) {
		this.angle = i;
	}

}
