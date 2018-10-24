import processing.core.PApplet;
import processing.core.PVector;

public class Arm {

	public int state = 4;
	private PApplet app;
//	private PVector hand;
//	private PVector shoulder;
//	private PVector elbow;
	
	public Arm (PApplet app) {
		this.app = app;
	}
	
	public void draw(PVector hand, PVector elbow, PVector shoulder, PApplet app) {
		//draw entire arm
		drawConnection(hand, elbow);
		drawConnection(elbow, shoulder);
		
		//draw joint
		drawJoint (hand);
		drawJoint (elbow);
		drawJoint(shoulder);	
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
	
}
