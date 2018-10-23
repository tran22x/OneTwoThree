import java.util.Random;

import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PImage;

public class WeaponPiece {
	
	
	//generate random location for the piece
	private Random random = new Random();
	private float x;
	private float y;
	private PApplet app;
	private String[] imageArray = {"data/trashempty.png", "data/trashempty.png","data/trashempty.png"};
	private int currImage = 0;
	final double THRESHOLD = .1f;//radius of weapon

	public WeaponPiece(PApplet app) {
		//save random location
		x = random.nextFloat();
		y = random.nextFloat();
		this.app = app;
		//drawWeapon();
		
	}
	
	public void drawWeapon() {
		//draw the weapon here
		app.fill(0, 0,255);
		app.ellipse(x, y, .2f, .2f);
		//return imageArray[currImage];
	}
	
	public boolean isGrabbed(PVector v) {
		//if the hand is within a certain threshold compared to the weapon return true
		if (v != null && Math.abs(v.x - x) < THRESHOLD && Math.abs(v.y - y) < THRESHOLD) {
			return true;
		}
		return false;
	}
	
	public void nextWeapon() {
		//change its location
		x = random.nextFloat();
		y = random.nextFloat();
		//change its image too
//		if (imageArray[currImage+1] != null) {
//			currImage++;
//		}
	}
	
}
