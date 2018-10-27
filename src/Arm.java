import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PVector;

public class Arm {
	private final double MOVEMENT_THRESHOLD = 0.05;
	public int state = 4;
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
	
	public Arm (PApplet app) {
		this.app = app;
		handQueue = new LinkedList<>();
		shoulderQueue = new LinkedList<>();
		elbowQueue = new LinkedList<>();
	}
	
	public void draw(PVector hand, PVector elbow, PVector shoulder, PApplet app) {
		// update position of the arm
		this.hand = hand;
		this.elbow = elbow;
		this.shoulder = shoulder;
		
		// push new position into the queue
		handQueue.add(hand);
		shoulderQueue.add(shoulder);
		elbowQueue.add(elbow);
		
		if (handQueue.size() > 5) {
			preHand = handQueue.poll();
		}
		if (shoulderQueue.size() > 5) {
			preShoulder = shoulderQueue.poll();
		}
		if (elbowQueue.size() > 5) {
			preElbow = elbowQueue.poll();
		}
		//draw entire arm
		drawConnection(hand, elbow);
		drawConnection(elbow, shoulder);
		
		//draw joint
		drawJoint (hand);
		drawJoint (elbow);
		drawJoint(shoulder);
		//draw joint
		drawJoint (preHand);
		drawJoint (preElbow);
		drawJoint (preShoulder);
	}
	
	public int getState() {
		//get current state of the arm
		return state;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	private void drawJoint (PVector v) {
		if (v != null) {
			app.fill(255, 0, 0);
			app.ellipse(v.x, v.y, .05f,.05f);
		}
	}
	
	private void drawConnection (PVector v1, PVector v2) {
		if (v1 != null && v2 != null) {
			app.stroke(255, 255,255);
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
				System.out.println("movement is detected" + difX);
				return isMoving;
			}
		}
		if (preShoulder!=null && shoulder!=null) {
			float difX = Math.abs(preShoulder.x - shoulder.x);
			float difY = Math.abs(preShoulder.y - shoulder.y);
			float difZ = Math.abs(preShoulder.z - shoulder.z);
			isMoving = difX > MOVEMENT_THRESHOLD || difY > MOVEMENT_THRESHOLD || difZ > MOVEMENT_THRESHOLD;
			if (isMoving) {
				System.out.println("movement is detected" + difX);
				return isMoving;
			}
		}
		if (preElbow!=null && elbow!=null) {
			float difX = Math.abs(preElbow.x - elbow.x);
			float difY = Math.abs(preElbow.y - elbow.y);
			float difZ = Math.abs(preElbow.z - elbow.z);
			isMoving = difX > MOVEMENT_THRESHOLD || difY > MOVEMENT_THRESHOLD || difZ > MOVEMENT_THRESHOLD;
			if (isMoving) {
				System.out.println("movement is detected" + difX + " " + difY + " " + difZ);
				return isMoving;
			}
		}
		return isMoving;
	}
}
