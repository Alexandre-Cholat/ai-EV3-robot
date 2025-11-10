package ev3;

public class Position {
	// 0 pour camp adversaire, 180 pour zone d'equipe
	public int angle;
	
	//distance estimee du centre
	public int dist_x;
	public int dist_y;
	
	public Position() {
		angle = 0;
	}
	
	public int getPosition() {
		return angle;
	}
	
	public void setPosition(int i) {
		this.angle = i;
	}
	

}
