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
	private PImage weapon;
	private int currWeapon = 0;
	public boolean allCollected = false;
	final double THRESHOLD = .2f;//radius of weapon

	public void setCollected (boolean b) {
		allCollected = b;
	}
	public WeaponPiece(PApplet app) {
		//save random location
		if (random.nextBoolean()) {
			x = random.nextFloat();
			y = (float) ((float) random.nextFloat()*(0.7));
		}
		else {
			x = random.nextFloat()*(-1);
			y = (float) ((float) random.nextFloat()*(0.7))*(-1);
		}
		this.app = app;
		weapon = app.loadImage("data/light.png");
	}
	
	public void drawWeapon() {
		if (!allCollected) {
			app.image(weapon, x, y, 0.3f, 0.3f);
		}
	}
	
	public boolean isGrabbed(PVector v) {
		//if the hand is within a certain threshold compared to the weapon return true
		if (v != null && Math.abs(v.x - x) < THRESHOLD && Math.abs(v.y - y) < THRESHOLD) {
			return true;
		}
		return false;
	}
	
	public void nextWeapon() {
		if (random.nextBoolean()) {
			x = random.nextFloat();
			y = (float) ((float) random.nextFloat()*(0.8));
		}
		else {
			x = random.nextFloat()*(-1);
			y = (float) ((float) random.nextFloat()*(0.7))*(-1);
		}
		currWeapon++;
	}
	
}
