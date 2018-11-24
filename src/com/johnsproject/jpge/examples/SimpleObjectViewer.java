package com.johnsproject.jpge.examples;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;

import com.johnsproject.jpge.Engine;
import com.johnsproject.jpge.JPGE;
import com.johnsproject.jpge.Profiler;
import com.johnsproject.jpge.dto.Camera;
import com.johnsproject.jpge.dto.Light;
import com.johnsproject.jpge.dto.Mesh;
import com.johnsproject.jpge.dto.SceneObject;
import com.johnsproject.jpge.dto.Texture;
import com.johnsproject.jpge.dto.Transform;
import com.johnsproject.jpge.graphics.SceneWindow;
import com.johnsproject.jpge.graphics.SceneWindow.ScenePanel;
import com.johnsproject.jpge.graphics.Shader;
import com.johnsproject.jpge.io.ImportExeption;
import com.johnsproject.jpge.io.KeyInputManager;
import com.johnsproject.jpge.io.MouseInputManager;
import com.johnsproject.jpge.io.SOMImporter;

public class SimpleObjectViewer implements JPGE{
	
	private static final int B_WIDTH = 120;
	private static final int B_HEIGHT = 30;
	
	private Mesh[] mesh = new Mesh[8];
	private Mesh mesh2;
	private Transform transform1;
	private Transform transform2;
	private Transform transform3;
	private Transform transform4;
	private SceneObject sceneObject;
	private SceneObject sceneObject2;
	private Camera camera;
	private Light light;
	private Texture texture;
	
	public SimpleObjectViewer() {
		transform1 = new Transform(new int[3], new int[3], new int[] {1,1,1});
		transform4 = new Transform(new int[] {0, 2000, 0}, new int[3], new int[] {1,1,1});
		transform2 = new Transform(new int[] {0, 0, -8000} , new int[3], new int[3]);
		transform3 = new Transform(new int[3] , new int[3], new int[3]);
		try {
			mesh2 = SOMImporter.load(getClass().getResourceAsStream("/plane.som"));
			mesh[0] = SOMImporter.load(getClass().getResourceAsStream("/plane.som"));
			mesh[1] = SOMImporter.load(getClass().getResourceAsStream("/cube.som"));
			mesh[2] = SOMImporter.load(getClass().getResourceAsStream("/cone.som"));
			mesh[3] = SOMImporter.load(getClass().getResourceAsStream("/cylinder.som"));
			mesh[4] = SOMImporter.load(getClass().getResourceAsStream("/sphere.som"));
			mesh[5] = SOMImporter.load(getClass().getResourceAsStream("/torus.som"));
			mesh[6] = SOMImporter.load(getClass().getResourceAsStream("/monkey.som"));
			mesh[7] = SOMImporter.load(getClass().getResourceAsStream("/cube.som"));
			texture = new Texture(getClass().getResourceAsStream("/JohnsProject.png"));
			for (int i = 0; i < mesh.length; i++) {
				mesh[i].getMaterial(0).setTexture(texture);
			}
			mesh2.getMaterial(0).setTexture(texture);
		} catch (ImportExeption e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		sceneObject = new SceneObject("testSO", transform1, mesh[0]);
		sceneObject2 = new SceneObject("testSO2", transform4, mesh2);
		sceneObject.getRigidbody().useGravity(false);
		sceneObject2.getRigidbody().useGravity(false);
		camera = new Camera("testCam", transform2, 0, 0, Main.R_WIDTH, Main.R_HEIGHT);
		light = new Light("testLight", transform3);
		Engine.getInstance().setSceneWindow(new SceneWindow(Main.W_WIDTH, Main.W_HEIGHT));
		Engine.getInstance().getRenderBuffer().setSize(Main.R_WIDTH, Main.R_HEIGHT);
		Engine.getInstance().getScene().addSceneObject(sceneObject);
		Engine.getInstance().getScene().addSceneObject(sceneObject2);
		Engine.getInstance().getScene().addCamera(camera);
		Engine.getInstance().getScene().addLight(light);
		Engine.getInstance().addJPGEListener(this);
//		Profiler.getInstance().start();
		createUI();
	}
	
	void createUI() {
		ScenePanel panel = Engine.getInstance().getSceneWindow().getPanel();
		panel.add(createText("Graphics", 0, 5, B_WIDTH, B_HEIGHT-5));
		panel.add(createText("Move Camera:\n w,a,s,d", 0, B_HEIGHT+1, B_WIDTH, B_HEIGHT));
		panel.add(createText("Rotate Camera:\n middle click, move", 0, B_HEIGHT*2+1, B_WIDTH, B_HEIGHT));
		panel.add(createText("Move Light:\n i,j,k,l,o,m", 0, B_HEIGHT*3+1, B_WIDTH, B_HEIGHT));
		panel.add(createText("Rotate Object:\n right click, move", 0, B_HEIGHT*4+1, B_WIDTH, B_HEIGHT));
		panel.add(createText("Model:\n 1,2,3,4,5,6,7,8", 0, B_HEIGHT*5+1, B_WIDTH, B_HEIGHT));
		panel.add(createButton("Flat", 0, B_HEIGHT*7-13, B_WIDTH/2, B_HEIGHT-10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sceneObject.getShader().setShadingType(Shader.SHADE_FLAT);
			}
		}));
		panel.add(createButton("Soft", B_WIDTH/2, B_HEIGHT*7-13, B_WIDTH/2+2, B_HEIGHT-10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sceneObject.getShader().setShadingType(Shader.SHADE_GOURAUD);
			}
		}));
		panel.add(createText("Shading:", 0, B_HEIGHT*6+1, B_WIDTH, B_HEIGHT));
		panel.add(createText("Drawing Type:", 0, B_HEIGHT*7+10, B_WIDTH, B_HEIGHT-10));
		panel.add(createButton("Vertex", 0, B_HEIGHT*8, B_WIDTH, B_HEIGHT-10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sceneObject.getShader().setDrawingType(Shader.DRAW_VERTEX);
			}
		}));
		panel.add(createButton("Wireframe", 0, B_HEIGHT*8+20, B_WIDTH, B_HEIGHT-10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sceneObject.getShader().setDrawingType(Shader.DRAW_WIREFRAME);
			}
		}));
		panel.add(createButton("Flat", 0, B_HEIGHT*8+40, B_WIDTH, B_HEIGHT-10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sceneObject.getShader().setDrawingType(Shader.DRAW_FLAT);
			}
		}));
		panel.add(createButton("Textured", 0, B_HEIGHT*8+60, B_WIDTH, B_HEIGHT-10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sceneObject.getShader().setDrawingType(Shader.DRAW_TEXTURED);
			}
		}));
		panel.add(createButton("Profiler", B_WIDTH+2, 5, B_WIDTH, B_HEIGHT, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Profiler.getInstance().start();
			}
		}));
		final JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "SOM files";
			}
			
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				if (f.getName().contains(".som")) return true;
				return false;
			}
		});
		panel.add(createButton("Load Mesh", B_WIDTH+2, 40, B_WIDTH, B_HEIGHT, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            try {
		            	mesh[7] = SOMImporter.load(file.getPath());
						sceneObject.setMesh(mesh[7]);
						mesh[7].getMaterial(0).setTexture(texture);
					} catch (ImportExeption e1) {
						e1.printStackTrace();
					}
		        }
			}
		}));
		panel.add(createText("Physics", Main.W_WIDTH-B_WIDTH, 5, B_WIDTH, B_HEIGHT-5));
		panel.add(createButton("Use Gravity", Main.W_WIDTH-B_WIDTH, B_HEIGHT+1, B_WIDTH, B_HEIGHT, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sceneObject.getRigidbody().useGravity(!sceneObject.getRigidbody().useGravity());
			}
		}));
		panel.add(createButton("Add Force", Main.W_WIDTH-B_WIDTH, B_HEIGHT*2+1, B_WIDTH, B_HEIGHT, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sceneObject.getRigidbody().addForce(0, -50, 0);
			}
		}));
	}

	JTextArea createText(String text, int x, int y, int w, int h) {
		JTextArea textArea = new JTextArea(text);
		textArea.setEditable(false);
		textArea.setLocation(x, y);
		textArea.setSize(w, h);
		return textArea;
	}
	
	JButton createButton(String text, int x, int y, int w, int h, ActionListener listener) {
		JButton button = new JButton(text);
		button.setLocation(x, y);
		button.setSize(w, h);
		button.addActionListener(listener);
		return button;
	}
	
	@Override
	public void update() {
		KeyInputManager keyInput = Engine.getInstance().getKeyInputManager();
		applyCameraMove(keyInput);
		applySceneObjectMove(keyInput);
		applyLightMove(keyInput);
		MouseInputManager mouseInput = Engine.getInstance().getMouseInputManager();
		if (mouseInput.getKey(MouseInputManager.RIGHT)) {
			int x = (mouseInput.getMouseX()) - (Main.W_WIDTH/2);
			int y = (mouseInput.getMouseY()) - (Main.W_HEIGHT/2);
			sceneObject.getTransform().rotate(y/(Main.W_HEIGHT>>4), x/(Main.W_WIDTH>>4), 0);
		}
		if (mouseInput.getKey(MouseInputManager.MIDDLE)) {
			int x = (mouseInput.getMouseX()) - (Main.W_WIDTH/2);
			int y = (mouseInput.getMouseY()) - (Main.W_HEIGHT/2);
			camera.getTransform().rotate(y/(Main.W_HEIGHT>>3), x/(Main.W_WIDTH>>3), 0);
		}
	}
	
	public void applyCameraMove(KeyInputManager input) {
		if (input.getKey(KeyEvent.VK_W)) {
			camera.getTransform().translateLocal(0, 0, 60);
		}
		if (input.getKey(KeyEvent.VK_S)) {
			camera.getTransform().translateLocal(0, 0, -60);
		}
		if (input.getKey(KeyEvent.VK_A)) {
			camera.getTransform().translateLocal(-60, 0, 0);
		}
		if (input.getKey(KeyEvent.VK_D)) {
			camera.getTransform().translateLocal(60, 0, 0);
		}
		if (input.getKey(KeyEvent.VK_E)) {
			camera.getTransform().translateLocal(0, -60, 0);
		}
		if (input.getKey(KeyEvent.VK_Y)) {
			camera.getTransform().translateLocal(0, 60, 0);
		}
	}
	
	public void applySceneObjectMove(KeyInputManager input) {
		if (input.getKey(KeyEvent.VK_1)) {
			sceneObject.setMesh(mesh[0]);
		}
		if (input.getKey(KeyEvent.VK_2)) {
			sceneObject.setMesh(mesh[1]);
		}
		if (input.getKey(KeyEvent.VK_3)) {
			sceneObject.setMesh(mesh[2]);
		}
		if (input.getKey(KeyEvent.VK_4)) {
			sceneObject.setMesh(mesh[3]);
		}
		if (input.getKey(KeyEvent.VK_5)) {
			sceneObject.setMesh(mesh[4]);
		}
		if (input.getKey(KeyEvent.VK_6)) {
			sceneObject.setMesh(mesh[5]);
		}
		if (input.getKey(KeyEvent.VK_7)) {
			sceneObject.setMesh(mesh[6]);
		}
		if (input.getKey(KeyEvent.VK_8)) {
			sceneObject.setMesh(mesh[7]);
		}
	}
	
	public void applyLightMove(KeyInputManager input) {
		if (input.getKey(KeyEvent.VK_I)) {
			light.getTransform().translateLocal(0, 0, -1);
		}
		if (input.getKey(KeyEvent.VK_K)) {
			light.getTransform().translateLocal(0, 0, 1);
		}
		if (input.getKey(KeyEvent.VK_J)) {
			light.getTransform().translateLocal(1, 0, 0);
		}
		if (input.getKey(KeyEvent.VK_L)) {
			light.getTransform().translateLocal(-1, 0, 0);
		}
		if (input.getKey(KeyEvent.VK_M)) {
			light.getTransform().translateLocal(0, 1, 0);
		}
		if (input.getKey(KeyEvent.VK_O)) {
			light.getTransform().translateLocal(0, -1, 0);
		}
	}
}
