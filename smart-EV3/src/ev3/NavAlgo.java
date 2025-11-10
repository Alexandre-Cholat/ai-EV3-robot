package ev3;

public class NavAlgo {
	
    private Robot r;
    private Sensor s;
    private Position p;
    
    public NavAlgo() {
    	this.r = new Robot();
        this.s = new Sensor();
        this.p = new Position();
    }
    
    public void findCenter() {
    	
    }
    
    public void scan4pucks() {
    	
    }
    
    public void pickUpPuck() {
    	
    	
    }
    
    public void grab() {
    	r.pincherOpen();
		int [] tab = p.getPosition();
		r.display("Angle: " + tab[0], 5000);
		Robot.pincherOpen= true;
		r.pincherClose();
    	
    }

}
