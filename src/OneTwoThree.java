
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

import edu.mtholyoke.cs.comsc243.kinect.Body;
import edu.mtholyoke.cs.comsc243.kinect.KinectBodyData;
import edu.mtholyoke.cs.comsc243.kinectTCP.TCPBodyReceiver;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class OneTwoThree extends PApplet {
	
	public static int PROJECTOR_WIDTH = 1024;
	public static int PROJECTOR_HEIGHT = 786;
	public boolean gameOver;
	public Monster monster;
	public Arm arm;
	public WeaponPiece weapon;
	private PImage weaponImage;
	private static final int NUM_WEAPON = 10;
	private int weaponCollected = 0;
	private PImage bg;
	
	private boolean moving = false;
	private int lastDead;
	private int deadWaitTime = 500;
	
//	private LinkedList<PVector> recentLocations;
	//TO DO: define the threshold
//	private final float MOVEMENT_THRESHOLD = 0;
	TCPBodyReceiver kinectReader;
	public static float PROJECTOR_RATIO = (float)PROJECTOR_HEIGHT/(float)PROJECTOR_WIDTH;

	public void createWindow(boolean useP2D, boolean isFullscreen, float windowsScale) {
		if (useP2D) {
			if(isFullscreen) {
				fullScreen(P2D);  			
			} else {
				size((int)(PROJECTOR_WIDTH * windowsScale), (int)(PROJECTOR_HEIGHT * windowsScale), P2D);
			}
		} else {
			if(isFullscreen) {
				fullScreen();  			
			} else {
				size((int)(PROJECTOR_WIDTH * windowsScale), (int)(PROJECTOR_HEIGHT * windowsScale));
			}
		}		
	}
	
	// use lower numbers to zoom out (show more of the world)
	// zoom of 1 means that the window is 2 meters wide and appox 1 meter tall in real world units
	// sets 0,0 to center of screen
	public void setScale(float zoom) {
		scale(zoom* width/2.0f, zoom * -width/2.0f);
		translate(1f/zoom , -PROJECTOR_RATIO/zoom );		
	}

	public void settings() {
		createWindow(true, true, .5f);
		monster = new Monster(this);
		arm = new Arm(this);
		weapon = new WeaponPiece(this);
		bg = loadImage("data/Gamebgnd.jpg");
		//weaponImage = loadImage(weapon.drawWeapon());
//		recentLocations = new LinkedList<>();
	}

	public void setup(){

		/*
		 * use this code to run your PApplet from data recorded by recorder 
		 */
		/*
		try {
			kinectReader = new KinectBodyDataProvider("test.kinect", 10);
		} catch (IOException e) {
			System.out.println("Unable to creat e kinect producer");
		}
		 */
		
		kinectReader = new TCPBodyReceiver("138.110.92.93", 8008);
		try {
			kinectReader.start();
		} catch (IOException e) {
			System.out.println("Unable to connect to kinect server");
			exit();
		}

	}
	public void draw(){
		setScale(.5f);
		noStroke();
		background(200,200,200);
		fill(0,255,0);
		
		
		//draw monster
		//draw weapon
		weapon.drawWeapon();
		//image(weaponImage,(float) -0.5, (float)-0.5, 2f, 2f);
		monster.draw(this);
		
		//draw person
		//HOW TO DETECT MOVEMENT?
		KinectBodyData bodyData1 = kinectReader.getMostRecentData();
		KinectBodyData bodyData = kinectReader.getNextData();
		if(bodyData == null) return;
		Body person = bodyData.getPerson(0);
		if(person != null){
			PVector shoulderRight = person.getJoint(Body.SHOULDER_RIGHT);
			PVector handRight = person.getJoint(Body.HAND_RIGHT);
			PVector elbowRight = person.getJoint(Body.ELBOW_RIGHT);
			arm.draw(handRight, elbowRight, shoulderRight, this);
			if (handRight != null && weapon.isGrabbed(handRight)) {
				if (weaponCollected < NUM_WEAPON) {
					weapon.nextWeapon();
					weaponCollected++;
				}
			}
		}
		if (monster.isAwake()) {
			if (moving == false){
				moving =  arm.isMoving();
				if (moving) {
					arm.setState(arm.getState()-1);
					lastDead = millis();
					System.out.println(moving + "+" + arm.getState());
					fill(1,1,1);
					this.ellipse(0, 0, 0.5f, 0.5f);
					checkGameOver();
				}
			} else if(lastDead != 0 && millis() - lastDead > deadWaitTime){
				moving = false;
				lastDead = 0;
			}
		} else {
			moving = false;
			lastDead = 0;
		}
	}
		
	/**
	 * Draws an ellipse in the x,y position of the vector (it ignores z).
	 * Will do nothing is vec is null.  This is handy because get joint 
	 * will return null if the joint isn't tracked. 
	 * @param vec
	 */
	public void drawIfValid(PVector vec) {
		if(vec != null) {
			ellipse(vec.x, vec.y, .1f,.1f);
		}
	}
	
	private boolean checkGameOver() {
		if (arm.getState()==0) {
			gameOver = true;
		}
		if (gameOver) {
			//TO DO: a scene for game over
			background(0,0,0);
		}
		return gameOver;
	}
	
	public static void main(String[] args) {
		PApplet.main(OneTwoThree.class.getName());
	}

}