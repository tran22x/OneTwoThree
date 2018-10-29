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
	private static final int NUM_POWER = 5;
	private static float PROJECTOR_RATIO = (float)PROJECTOR_HEIGHT/(float)PROJECTOR_WIDTH;
	
	private boolean gameOver;
	private boolean gameStart;
	private boolean gameWon;
	
	private Monster monster;
	private Arm arm;
	private Power power;
	private StatusBar bloodBar;
	private StatusBar powerBar;
	
	private PImage instructionImg;
	private PImage startButtonImg;
	private PImage gameOverImage;
	private PImage gameWinImage;
	private PImage bg;
	private PImage winbg;
	private PImage clawImage;
	
	private int powerCollected = 0;
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
		power = new Power(this);
		// load images
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
		kinectReader = new TCPBodyReceiver("138.110.92.93", 8008);
		try {
			kinectReader.start();
		} catch (IOException e) {
			System.out.println("Unable to connect tao kinect server");
			exit();
		}
	}
	
	public void draw(){
		updateSurrounding();
		//draw power
		power.drawPower();
		//draw person
		KinectBodyData bodyData = kinectReader.getNextData();
		if(bodyData == null) return;
		Body person = bodyData.getPerson(0);
		// draw instruction
		if (!gameStart) {
			// load instruction and start button
			image(instructionImg, -2f, -.5f, 4f, 2f);
			image(startButtonImg, -.2f, -.6f, .4f, .2f);
		}
		if(person != null && !gameOver){
			PVector shoulderRight = person.getJoint(Body.SHOULDER_RIGHT);
			PVector handRight = person.getJoint(Body.HAND_RIGHT);
			PVector elbowRight = person.getJoint(Body.ELBOW_RIGHT);
			arm.draw(handRight, elbowRight, shoulderRight, powerCollected, this);
			// if game has started and hand grab power, update power bar
			if (handRight != null && gameStart && powerCollected < NUM_POWER) {
				checkPowerGrabbed(handRight);
			}
			// check if the monster has been beaten if all powers have been collected
			if (handRight!=null && powerCollected >= NUM_POWER && gameStart) {
				power.setCollected(true);
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
			checkMovement();
		// reset when the monster goes to sleep
		} else {
			moving = false;
			lastDead = 0;
		}
		
		// if game over and win, display winning scene. If game over and lose, display losing scene
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
	
	private void updateSurrounding() {
		if (gameWon) {
			image(winbg, 0,0, width, height);
		} else {
			image(bg, 0,0, width, height);
		}
		setScale(.5f);
		noStroke();
		//draw monster
		monster.draw(this);
		// draw blood bar and power bar
		bloodBar.draw(arm.getState());
		powerBar.draw(this.powerCollected);
	}
	
	private void checkPowerGrabbed(PVector hand) {
		if (power.isGrabbed(hand)) {
			power.nextPower();
			powerCollected++;
			powerBar.draw(powerCollected);
		}
	}
	
	private void checkMovement() {
		if (moving == false){
			moving =  arm.isMoving();
			// if arm is moving, update life
			if (moving) {
				updateLife();
				checkGameOver();
			}
		} else {
			// give the player sometime to stop if they just got caught moving
			if (lastDead != 0 && millis() - lastDead > deadWaitTime){
				moving = false;
				lastDead = 0;
			} else {
				if (arm.getLastPosition() != null) {
					image(clawImage, arm.getLastPosition().x-1f, -1.2f, 1.7f, 1.7f);
				}
			}
		}
	}
	
	private void updateLife() {
		arm.setState(arm.getState()-1);
		if (arm.getLastPosition() != null) {
			image(clawImage, arm.getLastPosition().x, -1.2f, 1f, 1f);
		}
		lastDead = millis();
		bloodBar.draw(arm.getState());
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
	 * Win the game if the monster is touched by either hand, elbow or shoulder
	 * @param v hand
	 * @return true if the monster is touched
	 */
	private void checkGameWon() {
		if (arm.getLastPosition() != null && monster.touchedMonster(arm.getLastPosition())) {
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