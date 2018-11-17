package com.johnsproject.jpge.examples;

import java.awt.event.KeyEvent;

import javax.swing.JLabel;

import com.johnsproject.jpge.Engine;
import com.johnsproject.jpge.JPGE;
import com.johnsproject.jpge.Profiler;
import com.johnsproject.jpge.dto.Camera;
import com.johnsproject.jpge.dto.Light;
import com.johnsproject.jpge.dto.Mesh;
import com.johnsproject.jpge.dto.SceneObject;
import com.johnsproject.jpge.dto.Transform;
import com.johnsproject.jpge.graphics.SceneWindow;
import com.johnsproject.jpge.graphics.Shader;
import com.johnsproject.jpge.io.ImportExeption;
import com.johnsproject.jpge.io.KeyInputManager;
import com.johnsproject.jpge.io.SOMImporter;
import com.johnsproject.jpge.utils.MathUtils;
import com.johnsproject.jpge.utils.VectorUtils;

public class SpaceShipGame implements JPGE{
	
	private static final int vx = VectorUtils.X, vy = VectorUtils.Y, vz = VectorUtils.Z;
	
	private static final int W_WIDTH = 640;
	private static final int W_HEIGHT = 640;
	private static final int R_WIDTH = 1024;
	private static final int R_HEIGHT = 1024;
	
	private static final int RANGE = 4000;
	private static final int SHIP_SPEED = 100;
	private static final int METEOR_START = 20000;
	private static final int METEOR_START_RANGE = 10000;
	private static final int SHOOT_TIME = 300;
	private static final int BULLET_SPEED = 5000;
	
	private SceneObject spaceship = null;
	private SceneObject[] bulletPool = new SceneObject[5];
	private SceneObject[] meteorPool = new SceneObject[10];
	private SceneObject explosion = null;
	private int lastBullet = 0;
	
	private int points = 0;
	private int hp = 100;
	
	private Mesh explosionMesh = null;
	private Mesh meteorMesh = null;
	private Mesh bulletMesh = null;
	
	private JLabel hpText = null;
	private JLabel pointsText = null;
	
	public SpaceShipGame() {
		try {
			meteorMesh = SOMImporter.load(getClass().getResourceAsStream("/meteor.som"));
			explosionMesh = SOMImporter.load(getClass().getResourceAsStream("/explosion.som"));
			bulletMesh = SOMImporter.load(getClass().getResourceAsStream("/bullet.som"));
		} catch (ImportExeption e) {
			e.printStackTrace();
		}
		initializeShip();
		initializeExplosion();
		initializeBulletPool();
		initializeMeteorPool();
		Transform cameraTransform = new Transform(new int[] {0, -2000, -8000} , new int[3], new int[3]);
		Transform lightTransform = new Transform(new int[] {3, 3, 0} , new int[3], new int[3]);
		Camera camera = new Camera("testCam", cameraTransform, 0, 0, R_WIDTH, R_HEIGHT);
		Light light = new Light("testLight", lightTransform);
		Engine.getInstance().setSceneWindow(new SceneWindow(W_WIDTH, W_HEIGHT));
		Engine.getInstance().getRenderBuffer().setSize(R_WIDTH, R_HEIGHT);
		Engine.getInstance().getScene().addCamera(camera);
		Engine.getInstance().getScene().addLight(light);
		initializeUI();
		Engine.getInstance().addJPGEListener(this);
	}
	
	public void initializeUI() {
		hpText = new JLabel("HP : " + hp);
		hpText.setSize(100,50);
		Engine.getInstance().getSceneWindow().getPanel().add(hpText);
		pointsText = new JLabel("Points : " + points);
		pointsText.setSize(100,50);
		Engine.getInstance().getSceneWindow().getPanel().add(pointsText);
	}
	
	public void initializeShip() {
		Transform shipTransform = new Transform(new int[3], new int[3], new int[] {1,1,1});
		Mesh shipMesh = null;
		try {
			shipMesh = SOMImporter.load(getClass().getResourceAsStream("/spaceship.som"));
		} catch (ImportExeption e) {
			e.printStackTrace();
		}
		spaceship = new SceneObject("ship", shipTransform, shipMesh);
		spaceship.getRigidbody().setKinematic(true);
		spaceship.getShader().setDrawingType(Shader.DRAW_FLAT);
		Engine.getInstance().getScene().addSceneObject(spaceship);
	}
	
	public void initializeBulletPool() {
		for (int i = 0; i < bulletPool.length; i++) {
			Transform bulletTransform = new Transform(new int[] {0,0,-10000}, new int[3], new int[] {1,1,1});
			bulletPool[i] = new SceneObject("bullet " + i, bulletTransform, bulletMesh);
			bulletPool[i].getRigidbody().setKinematic(true);
			bulletPool[i].getShader().setDrawingType(Shader.DRAW_FLAT);
			Engine.getInstance().getScene().addSceneObject(bulletPool[i]);
		}
	}
	
	public void initializeMeteorPool() {
		for (int i = 0; i < meteorPool.length; i++) {
			int x = MathUtils.random(-RANGE, RANGE);
			int z = MathUtils.random(METEOR_START, METEOR_START + METEOR_START_RANGE);
			Transform bulletTransform = new Transform(new int[] {x,0,z}, new int[3], new int[] {1,1,1});
			meteorPool[i] = new SceneObject("meteor " + i, bulletTransform, meteorMesh);
			meteorPool[i].getRigidbody().setKinematic(true);
			meteorPool[i].getRigidbody().addForce(0, 0, -1000);
			meteorPool[i].getShader().setDrawingType(Shader.DRAW_FLAT);
			Engine.getInstance().getScene().addSceneObject(meteorPool[i]);
		}
	}
	
	public void initializeExplosion() {
		Transform bulletTransform = new Transform(new int[] {0,0,-10000}, new int[3], new int[] {1,1,1});
		explosion = new SceneObject("explosion", bulletTransform, explosionMesh);
		explosion.getRigidbody().setKinematic(true);
		explosion.getShader().setDrawingType(Shader.DRAW_FLAT);
		Engine.getInstance().getScene().addSceneObject(explosion);
	}

	private long lastTime = System.currentTimeMillis();	
	@Override
	public void update() {
		updateInput();
		updateCollisions();
		updatePositions();
		hpText.setText("HP : " + hp);
		pointsText.setText("Points : " + points);
	}
	
	public void updateCollisions() {
		for (int i = 0; i < meteorPool.length; i++) {
			if (meteorPool[i].getRigidbody().isCollidingSimple("bullet")) {
				int[] meteorPos = meteorPool[i].getTransform().getPosition();
				int[] explosionPos = explosion.getTransform().getPosition();
				int x = MathUtils.random(0, 90);
				int y = MathUtils.random(0, 90);
				explosion.getTransform().rotate(x, y, 0);
				VectorUtils.copy3(explosionPos, meteorPos);
				meteorPos[vz] = MathUtils.random(METEOR_START, METEOR_START + METEOR_START_RANGE);
				meteorPool[i].getRigidbody().addForce(0, 0, -50);
				points++;
			}
			if (meteorPool[i].getRigidbody().isColliding("ship")) {
				int[] meteorPos = meteorPool[i].getTransform().getPosition();
				int[] explosionPos = explosion.getTransform().getPosition();
				int x = MathUtils.random(0, 90);
				int y = MathUtils.random(0, 90);
				explosion.getTransform().rotate(x, y, 0);
				VectorUtils.copy3(explosionPos, meteorPos);
				meteorPool[i].getTransform().getPosition()[vz] = MathUtils.random(METEOR_START, METEOR_START + METEOR_START_RANGE);
				hp -= 10;
			}
		}
		for (int i = 0; i < bulletPool.length; i++) {
			if (bulletPool[i].getRigidbody().isCollidingSimple("meteor")) {
				bulletPool[i].getTransform().getPosition()[vz] = -10000;
				bulletPool[i].getRigidbody().getVelocity()[vz] = 0;
			}
		}
	}
	
	public void updatePositions() {
		for (int i = 0; i < meteorPool.length; i++) {
			int[] pos = meteorPool[i].getTransform().getPosition();
			if (pos[vz] <= -10000) {
				pos[vz] = MathUtils.random(METEOR_START, METEOR_START + METEOR_START_RANGE);
			}
		}
		for (int i = 0; i < bulletPool.length; i++) {
			int[] pos = bulletPool[i].getTransform().getPosition();
			if (pos[vz] >= 40000) {
				pos[vz] = -10000;
				bulletPool[i].getRigidbody().getVelocity()[vz] = 0;
			}
		}
	}
	
	public void updateInput() {
		KeyInputManager key = Engine.getInstance().getKeyInputManager();
		if (key.getKey(KeyEvent.VK_P)) {
			Profiler.getInstance().start();
		}
		if (key.getKey(KeyEvent.VK_A)) {
			int x = spaceship.getTransform().getPosition()[vx];
			if (x > -RANGE) {
				spaceship.getTransform().translate(-SHIP_SPEED, 0, 0);
			}
		}
		if (key.getKey(KeyEvent.VK_D)) {
			int x = spaceship.getTransform().getPosition()[vx];
			if (x < RANGE) {
				spaceship.getTransform().translate(SHIP_SPEED, 0, 0);
			}
		}
		if (key.getKey(KeyEvent.VK_SPACE)) {
			if(System.currentTimeMillis() - lastTime > SHOOT_TIME) {
			 	lastTime = System.currentTimeMillis();	
				SceneObject bullet = bulletPool[lastBullet];
				int[] bulletPos = bullet.getTransform().getPosition();
				int[] sceneObjectPos = spaceship.getTransform().getPosition();
				VectorUtils.copy3(bulletPos, sceneObjectPos);
				bullet.getRigidbody().getVelocity()[vz] = 0;
				bullet.getRigidbody().addForce(0, 0, BULLET_SPEED);
				lastBullet++;
				if (lastBullet >= bulletPool.length) lastBullet = 0;
			}
		}
	}
}
