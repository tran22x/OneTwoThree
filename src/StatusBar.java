import processing.core.PApplet;
import processing.core.PImage;

public class StatusBar {
	private PApplet app;
	private String name;
	private float xLocation;
	private final float BORDER = .01f;
	private PImage lifeLabel;
	private PImage powerLabel;
	
	public StatusBar(PApplet app, String name, float xLocation) {
		this.app = app;
		this.name = name;
		this.xLocation = xLocation;
		lifeLabel = app.loadImage("data/life.png");
		powerLabel = app.loadImage("data/power.png");
	}
	
	public void draw(int level) {
		app.fill(0,0,0);
		float y = 1.2f;
		float barWidth = 1f;
		float barHeight = .1f;
		if (name == "life") {
			app.image(lifeLabel, xLocation, y, 0.4f, 0.4f);
		} else if (name == "power") {
			app.image(powerLabel, xLocation, y, 0.4f, 0.4f);
		}
		app.rect(xLocation, y, barWidth, barHeight);
		app.fill(244, 137, 66);
		if (level != 0) {
			app.rect(xLocation+BORDER, y, barWidth*level/5-BORDER*2, barHeight-BORDER*2, 7);
		}
	}
}
