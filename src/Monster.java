
import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Monster {
	private PApplet app;
	
	public static enum State {AWAKE, SLEEPING};
	private State state = State.SLEEPING;
	private int time = Integer.MAX_VALUE;
	
	private int wait = 2000;
	private Random random = new Random();
	
	public final float x = (float) -2.4;
	public final float y = (float) -0.5;
	public final int MONSTER_SIZE = 2;
	
	private PImage sleeping;
	private PImage awake;
	
	public Monster (PApplet app) {
		this.app = app;
		sleeping = app.loadImage("data/Monster-Sleeping.png");
		awake = app.loadImage("data/Monster-Awake.png");
	}
	
	public void startTimer () {
		time = app.millis();
	}
	
	public void draw (PApplet app) {
		updateTimer(app);
		switch(state) {
		case AWAKE:
			drawAwake(app);
			break;
		case SLEEPING:
			drawSleeping(app);
			break;
		}
	}

	private void updateTimer(PApplet app) {
		if(app.millis() - time >= wait){ //if time passed is greater or equal to wait time then switch state
			if (state == State.AWAKE) {
				state = State.SLEEPING;
			}
			else state = State.AWAKE;
			wait = random.nextInt(5000 + 1 - 2000) + 2000; //generate random wait time between 5 - 8 seconds
		    time = app.millis();//also update the stored time
		}
	}
	
	private void drawSleeping (PApplet app) {
		app.image(sleeping,x,y, MONSTER_SIZE, MONSTER_SIZE);
		app.fill(255,255,255);
	}
	
	private void drawAwake (PApplet app) {
		app.image(awake,x,y, MONSTER_SIZE, MONSTER_SIZE);
	}
	
	/** Check if monster has been touched**/
	public boolean touchedMonster (PVector v) {
		if (v.x <= x+MONSTER_SIZE-0.5f && v.y <= y+MONSTER_SIZE) {
			app.ellipse(0, 0, 0.5f, 0.5f);
			return true;
		}
		return false;
	}
	
	public boolean isAwake() {
		return state == State.AWAKE;
	}
	
	public void setStage(State newState) {
		this.state = newState;
	}
}
