
package ev3;

/**
 * Represents the position and orientation state of the EV3 robot on the field.
 * 
 * This class tracks the robot's angle orientation and whether its position on the field
 * is known or lost. The angle is measured relative to the field's coordinate system where
 * 180 degrees represents the opponent's camp and 0 degrees represents the team's zone.
 * 
 * The robot is assumed to start facing the opponent's camp with an unknown position.
 */
public class Position {
	
	/**
	 * The angle orientation of the robot in degrees.
	 * 180 degrees = opponent's camp direction
	 * 0 degrees = team's zone direction
	 */
	public float angle;

	/**
	 * Flag indicating whether the robot's position on the field is lost (true) or known (false).
	 */
	public boolean perdu;

	/**
	 * Constructs a new Position instance with default values.
	 * 
	 * Initializes the robot's angle to 180 degrees (facing opponent's camp)
	 * and marks the position as lost (perdu = true), since the exact location
	 * on the field is initially unknown.
	 */
	public Position() {
		angle = 180;
		perdu = true;
	}

	/**
	 * Retrieves the current position loss status of the robot.
	 * 
	 * @return true if the robot's position is lost or unknown, false if position is known
	 */
	public boolean getPerdu() {
		return perdu;
	}

	/**
	 * Sets whether the robot's position on the field is known or lost.
	 * 
	 * @param b true to mark the position as lost, false to mark it as known
	 */
	public void setPerdu(boolean b) {
		perdu = b;
	}

	/**
	 * Retrieves the current angle orientation of the robot.
	 * 
	 * @return the angle in degrees (180 = opponent's camp, 0 = team's zone)
	 */
	public float getPosition() {
		return angle;
	}

	/**
	 * Sets the angle orientation of the robot.
	 * 
	 * @param i the angle in degrees to set (180 = opponent's camp, 0 = team's zone)
	 */
	public void setAngle(float i) {
		this.angle = i;
	}
}
