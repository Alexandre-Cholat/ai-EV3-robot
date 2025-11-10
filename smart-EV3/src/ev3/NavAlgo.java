package ev3;

import lejos.hardware.Button;
import lejos.utility.Delay;

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
    
    public void rotate_until_obj_detected() {
    	
    }
    
    public boolean is_obj(){
    	return false;
    	
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
    
    public void wander() {
    	
    	while (s.getDistance()> 10) {
    		r.forward();
    	}
    	r.turnLeft();
    	long rand = (long)( Math.random()* 10000);
    	Delay.msDelay(rand);
    	
    }
    
    public void wander2() {
    	float distCm = s.getDistance();
    	r.display("D: "+ distCm, 200);
    	while(distCm > 20) {
    		distCm = s.getDistance();
        	r.display("D: "+ distCm, 200);
        	r.forward();
    	}
 
    	r.beep();
    	r.stop();
    			
    		
    	
    	
    }

}
