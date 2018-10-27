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
	private PImage weapon1;
	private PImage weapon2;
	private PImage weapon3;
	private PImage[] weapons = new PImage[5];
	private String[] imageArray = {"data/weapon1.png", "data/weapon2.png","data/weapon1","data/weapon2"};
	private int currImage = 0;
	private int currWeapon = 0;
	final double THRESHOLD = .1f;//radius of weapon

	public WeaponPiece(PApplet app) {
		//save random location
		x = random.nextFloat();
		y = random.nextFloat();
		this.app = app;
		weapon1 = app.loadImage(imageArray[0]);
		weapon2 = app.loadImage(imageArray[1]);
		weapon3 = app.loadImage("data/weapon3.png");
		
		weapons[0] = weapon1;
		weapons[1] = weapon2;
		weapons[2] = weapon3;
		weapons[3] = weapon2;
		weapons[4] = weapon1;
		
		//drawWeapon();
		
	}
	
	public void drawWeapon() {
		//draw the weapon here
//		app.fill(0, 0,255);
//		app.ellipse(x, y, .2f, .2f);
		if (currWeapon < weapons.length) {
		//System.out.println("curr weapon is: " + currWeapon);
		app.image(weapons[currWeapon], x, y, 0.6f, 0.6f);
		}
		else {
			app.fill(0, 0,255);
			app.ellipse(x, y, .2f, .2f);
		}
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
		currWeapon++;
		//change its image too
//		if (imageArray[currImage+1] != null) {
//			currImage++;
//		}
	}
	
}
