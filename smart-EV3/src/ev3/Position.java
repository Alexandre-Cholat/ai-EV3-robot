package ev3;

public class Position {
	// 180 pour camp adversaire, 0 pour zone d'equipe
	public int angle;
	
	public boolean perdu;
	
	//supposons le robot commence face au camp adversaire, n'importe ou.
	public Position() {
		angle = 180;
		perdu = true;
	}
	
	//check if x,y position is known
	public boolean getPerdu() {
		return perdu;
	}
	
	public void setPerdu(boolean b) {
		perdu = b;
	}
	
	public int getPosition() {
		return angle;
	}
	
	public void setAngle(int i) {
		this.angle = i;
	}
	
	

}
