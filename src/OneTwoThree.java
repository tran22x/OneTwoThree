
import java.io.IOException;

import edu.mtholyoke.cs.comsc243.kinect.Body;
import edu.mtholyoke.cs.comsc243.kinect.KinectBodyData;
import edu.mtholyoke.cs.comsc243.kinectTCP.TCPBodyReceiver;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class OneTwoThree extends PApplet {
	
	private static int PROJECTOR_WIDTH = 1024;
	private static int PROJECTOR_HEIGHT = 786;
	private static final int NUM_WEAPON = 5;
	private static float PROJECTOR_RATIO = (float)PROJECTOR_HEIGHT/(float)PROJECTOR_WIDTH;
	
	private boolean gameOver;
	private boolean gameStart;
	private boolean gameWon;
	
	private Monster monster;
	private Arm arm;
	private WeaponPiece weapon;
	private StatusBar bloodBar;
	private StatusBar powerBar;
	
	private PImage instructionImg;
	private PImage startButtonImg;
	private PImage gameOverImage;
	private PImage gameWinImage;
	private PImage bg;
	private PImage winbg;
	private PImage clawImage;
	
	private int weaponCollected = 0;
	private boolean moving = false;
	private int lastDead;
	private int deadWaitTime = 500;
	private TCPBodyReceiver kinectReader;

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
		bg = loadImage("data/Gamebgnd.png");
		winbg = loadImage("data/Gamebgnd_win.png");
		bloodBar = new StatusBar(this, "life", .5f);
		powerBar = new StatusBar(this, "power", -1f);
		instructionImg = loadImage("data/instruction.png");
		startButtonImg = loadImage("data/startbutton.png");
		gameOverImage = loadImage("data/gameover.png");
		gameWinImage = loadImage("data/winStatement.png");
		clawImage = loadImage("data/claw.png");
	}

	public void setup(){

		/*
		 * use this code to run your PApplet from data recorded by recorder 
		 */
		/*
		try {
			kinectReader = new KinectBodyDataProvider("test.kinect", 10);
		} catch (IOException e) {
			System.out.println("Unable to create kinect producer");
		}
		 */
		
		kinectReader = new TCPBodyReceiver("138.110.92.93", 8008);
		try {
			kinectReader.start();
		} catch (IOException e) {
			System.out.println("Unable to connect tao kinect server");
			exit();
		}

	}
	
	public void draw(){
		if (gameWon) {
			image(winbg, 0,0, width, height);
		} else {
			image(bg, 0,0, width, height);
		}
		setScale(.5f);
		noStroke();
		//draw weapon
		weapon.drawWeapon();
		//draw monster
		monster.draw(this);
		// draw blood bar and power bar
		bloodBar.draw(arm.getState());
		powerBar.draw(this.weaponCollected);
		
		//draw person
		KinectBodyData bodyData = kinectReader.getNextData();
		if(bodyData == null) return;
		Body person = bodyData.getPerson(0);
		if (!gameStart) {
			// load instruction and start button
			image(instructionImg, -2f, -.5f, 4f, 2f);
			image(startButtonImg, -.2f, -.6f, .4f, .2f);
		}
		if(person != null && !gameOver){
			PVector shoulderRight = person.getJoint(Body.SHOULDER_RIGHT);
			PVector handRight = person.getJoint(Body.HAND_RIGHT);
			PVector elbowRight = person.getJoint(Body.ELBOW_RIGHT);
			arm.draw(handRight, elbowRight, shoulderRight, weaponCollected, this);
			// if game has started and hand grab weapon, update power bar
			if (handRight != null && weapon.isGrabbed(handRight) && gameStart && weaponCollected < NUM_WEAPON) {
				weapon.nextWeapon();
				weaponCollected++;
				powerBar.draw(weaponCollected);
			}
			// check if the monster has been beaten if all weapons have been collected
			if (handRight!=null && weaponCollected >= NUM_WEAPON && gameStart) {
				weapon.setCollected(true);
				checkGameWon();
			}
			// if game has not been started, check if the start button is touched
			if (handRight!=null && !gameStart) {
				gameStart = checkTouchStartButton(handRight);
				if (gameStart) monster.startTimer();
			}
		}
		// check movement if the monster is awake
		if (monster.isAwake() && gameStart && !gameOver) {
			if (moving == false){
				moving =  arm.isMoving();
				// if arm is moving, update life
				if (moving) {
					arm.setState(arm.getState()-1);
					if (arm.getHand()!=null) {
						image(clawImage, arm.getHand().x, -1.2f, 1f, 1f);
					}
					lastDead = millis();
					bloodBar.draw(arm.getState());
					checkGameOver();
				}
			// give the player sometime to stop if they just got caught moving
			} else {
				if (lastDead != 0 && millis() - lastDead > deadWaitTime){
					moving = false;
					lastDead = 0;
				} else {
					if (arm.getHand()!=null) {
						image(clawImage, arm.getHand().x-1f, -1.2f, 1.7f, 1.7f);
					}
				}
			}
		// reset when the monster goes to sleep
		} else {
			moving = false;
			lastDead = 0;
		}
		
		if (gameOver){
			if (gameWon) {
				monster.setStage(Monster.State.SLEEPING);
				image(gameWinImage, -2f, -.5f, 4f, 2f);
			} else {
				monster.setStage(Monster.State.AWAKE);
				image(gameOverImage, -1.5f, -1.2f, 2f, 2f);
			}
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
	
	/**
	 * Check if the start button is touched to start the game
	 * @param hand the hand
	 * @return true if the button is touched
	 */
	private boolean checkTouchStartButton(PVector hand) {
		return (hand.x > -.2f && hand.x < .2f && hand.y > -.6f && hand.y < -.4f);
	}
	
	/**
	 * Win the game if the monster is touched
	 * @param v hand
	 * @return true if the monster is touched
	 */
	private void checkGameWon() {
		if (arm.getHand() != null && monster.touchedMonster(arm.getHand())) {
			gameOver = true;
			gameWon = true;
			return;
		}
		if (arm.getShoulder() != null && monster.touchedMonster(arm.getShoulder())) {
			gameOver = true;
			gameWon = true;
			return;
		}
		if (arm.getElbow() != null && monster.touchedMonster(arm.getElbow())) {
			gameOver = true;
			gameWon = true;
			return;
		}
	}
	
	/**
	 * If life is gone, set game over
	 */
	private void checkGameOver() {
		if (arm.getState()==0) {
			gameOver = true;
		}
	}
	
	public static void main(String[] args) {
		PApplet.main(OneTwoThree.class.getName());
	}

}