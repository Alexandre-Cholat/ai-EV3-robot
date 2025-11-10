package ev3;

public class Main {

	public static void main(String[] args) {
		Robot r = new Robot();
		
		r.pincherOpen();
		int [] tab = r.position.getPosition();
		r.display("Angle: " + tab[0], 5000);
		Robot.pincherOpen= true;
		r.pincherClose();
		
        r.close();

	}

}
