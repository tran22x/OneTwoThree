import java.util.Random;
import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PImage;

public class Power {
	private PApplet app;
	private Random random = new Random();
	//location of power
	private float x;
	private float y;
	
	private PImage power;
	public boolean allCollected = false; //keep track if all powers have been collected
	final double THRESHOLD = .2f;//threshold to check if power has been grabbed

	public void setCollected (boolean b) {
		allCollected = b;
	}
	public Power(PApplet app) {
		//save random location
		generateRandomLoc();
		this.app = app;
		power = app.loadImage("data/light.png");
	}
	private void generateRandomLoc() {
		if (random.nextBoolean()) {
			x = random.nextFloat();
			y = (float) ((float) random.nextFloat()*(0.7));
		}
		else {
			x = random.nextFloat()*(-1);
			y = (float) ((float) random.nextFloat()*(0.7))*(-1);
		}
	}
	
	public void drawPower() {
		if (!allCollected) {
			app.image(power, x, y, 0.3f, 0.3f);
		}
	}
	
	public boolean isGrabbed(PVector v) {
		//if the hand is within a certain threshold compared to the power return true
		if (v != null && Math.abs(v.x - x) < THRESHOLD && Math.abs(v.y - y) < THRESHOLD) {
			return true;
		}
		return false;
	}

	/**Method to generate next power by changing location of power*/
	public void nextPower() {
		generateRandomLoc();
	}
	
}
