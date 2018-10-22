
import processing.core.PApplet;

public class Monster {
	
	public static enum State {AWAKE, SLEEPING};
	public State state = State.SLEEPING;
	
	public Monster () {
		
	}
	
	public void draw (PApplet app) {
		switch(state) {
		case AWAKE:
			drawAwake(app);
			break;
		case SLEEPING:
			drawSleeping(app);
			break;
	}
	}
	
	private void drawSleeping (PApplet app) {
//		System.out.println("Width is: " + app.width);
//		System.out.println("Height is: " + app.height);
		app.fill(0,255,0);
		app.ellipse((float)-0.5,(float)-0.5, 0.5f, 0.5f);
	}
	
	private void drawAwake (PApplet app) {
		
		//draw red circle
		app.fill(0, 255,0);
		app.ellipse(50, app.height - app.height/10, .2f, .2f);
	}
	
}
