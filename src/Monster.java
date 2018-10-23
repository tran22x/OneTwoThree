
import processing.core.PApplet;

public class Monster {
	
	public static enum State {AWAKE, SLEEPING};
	public State state = State.SLEEPING;
	private int time;
	private int wait = 8000;
	private PApplet app;
	private float x = (float) -1.6;
	private float y = (float) -0.7;
	public Monster (PApplet app) {
		this.app = app;
		time = app.millis();
	}
	
	public void draw (PApplet app) {
		if(app.millis() - time >= wait){
			if (state == State.AWAKE) {
				state = State.SLEEPING;
			}
			else state = State.AWAKE;
		    time = app.millis();//also update the stored time
		}
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
		app.fill(0,255,0);
		app.ellipse(x,y, 0.5f, 0.5f);
		
	}
	
	private void drawAwake (PApplet app) {
		//draw red circle
		app.fill(255, 0,0);
		app.ellipse(x, y, .5f, .5f);
	}
	
}
