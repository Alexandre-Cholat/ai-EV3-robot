package ev3;

public class Position {
	// 180 pour camp adversaire, 0 pour zone d'equipe
	public int angle;
	
	public boolean perdu;
	
	//supposons le robot commence face au camp adversaire, n'importe ou.
	public Position() {
		angle = 0;
		perdu = true;
	}
	
	//check if x,y position is known
	public boolean getPerdu() {
		return perdu;
	}
	
	public void setPerdu(boolean b) {
		perdu = b;
	}
	
	public int[] getPosition() {
		return new int[] {angle, dist_x, dist_y};
	}
	
	public void setAngle(int i) {
		this.angle = i;
	}
	
	public void setDist(int x , int y) {
		setPerdu(false);
		dist_x = x;
		dist_y = y;
	}
	

}
