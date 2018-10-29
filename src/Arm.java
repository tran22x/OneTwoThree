import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PImage;

public class Arm {
	private final double MOVEMENT_THRESHOLD = 0.08;
	public int state = 5;
	private PApplet app;
	private PVector hand;
	private PVector shoulder;
	private PVector elbow;
	
	private PVector preHand;
	private PVector preShoulder;
	private PVector preElbow;
	
	private LinkedList<PVector> handQueue;
	private LinkedList<PVector> shoulderQueue;
	private LinkedList<PVector> elbowQueue;
	
	private PImage powerPic;
	private int power;
	
	public Arm (PApplet app) {
		this.app = app;
		handQueue = new LinkedList<>();
		shoulderQueue = new LinkedList<>();
		elbowQueue = new LinkedList<>();
		powerPic = app.loadImage("data/light.png");
	}
	
	public void draw(PVector hand, PVector elbow, PVector shoulder, int power, PApplet app) {
		// update position of the arm
		this.hand = hand;
		this.elbow = elbow;
		this.shoulder = shoulder;
		this.power = power;
		
		// push new position into the queue
		handQueue.add(hand);
		shoulderQueue.add(shoulder);
		elbowQueue.add(elbow);
		
		if (handQueue.size() > 10) {
			preHand = handQueue.poll();
		}
		if (shoulderQueue.size() > 10) {
			preShoulder = shoulderQueue.poll();
		}
		if (elbowQueue.size() > 10) {
			preElbow = elbowQueue.poll();
		}
		
		//draw entire arm
		drawConnection(hand, elbow);
		drawConnection(elbow, shoulder);
		
		//draw joint
		if (power == 0) {
			drawJoint (hand);
		}
		else if (power > 0) {
			drawPower(hand);
		}
		drawJoint (elbow);
		drawJoint (shoulder);
	}
	
	public int getState() {
		//get current state of the arm
		return state;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	private void drawPower (PVector v) {
		if (v != null) {
			float f = 0.1f*power;
			app.image(powerPic, v.x - f/2, v.y - f/2, f, f);
		}
	}
	
	private void drawJoint (PVector v) {
		if (v != null) {
			app.fill(0, 0, 0);
			app.ellipse(v.x, v.y, .01f,.01f);
		}
	}
	
	private void drawConnection (PVector v1, PVector v2) {
		if (v1 != null && v2 != null) {
			app.stroke(0, 0,0);
			app.strokeWeight(.01f);
			app.line(v1.x,v1.y, v2.x, v2.y);
		}
	}
	
	public boolean isMoving() {
		boolean isMoving = false;
		if (preHand!=null && hand!=null) {
			float difX = Math.abs(preHand.x - hand.x);
			float difY = Math.abs(preHand.y - hand.y);
			float difZ = Math.abs(preHand.z - hand.z);
			isMoving = (difX > MOVEMENT_THRESHOLD || difY > MOVEMENT_THRESHOLD || difZ > MOVEMENT_THRESHOLD);
			if (isMoving) {
				return isMoving;
			}
		}
		if (preShoulder!=null && shoulder!=null) {
			float difX = Math.abs(preShoulder.x - shoulder.x);
			float difY = Math.abs(preShoulder.y - shoulder.y);
			float difZ = Math.abs(preShoulder.z - shoulder.z);
			isMoving = difX > MOVEMENT_THRESHOLD || difY > MOVEMENT_THRESHOLD || difZ > MOVEMENT_THRESHOLD;
			if (isMoving) {
				return isMoving;
			}
		}
		if (preElbow!=null && elbow!=null) {
			float difX = Math.abs(preElbow.x - elbow.x);
			float difY = Math.abs(preElbow.y - elbow.y);
			float difZ = Math.abs(preElbow.z - elbow.z);
			isMoving = difX > MOVEMENT_THRESHOLD || difY > MOVEMENT_THRESHOLD || difZ > MOVEMENT_THRESHOLD;
			if (isMoving) {
				return isMoving;
			}
		}
		return isMoving;
	}
	
	public PVector getLastPosition() {
		if (handQueue.getLast() != null) {
			return handQueue.getLast();
		}
		if (elbowQueue.getLast() != null) {
			return elbowQueue.getLast();
		}
		if (shoulderQueue.getLast() != null) {
			return shoulderQueue.getLast();
		}
		return null;
	}
}
