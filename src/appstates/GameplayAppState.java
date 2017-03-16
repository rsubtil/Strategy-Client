package appstates;

import board.Board;
import board.PlainsBoard;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.texture.FrameBuffer;
import controls.SkyControl;
import interfaces.ScreenResize;
import java.awt.GraphicsDevice;
import java.util.ArrayList;
import units.Cannon;
import units.Mortar;
import units.Sniper;
import units.Soldier;
import util.Fields;

public class GameplayAppState extends AbstractAppState {
    
    // Variables
    // Essential
    private SimpleApplication app;
    private AppStateManager stateManager;
    private AssetManager assetManager;
    private Node rootNode;
    private ViewPort viewPort;
    private Camera cam;
    private FlyByCamera flyCam;
    private InputManager inputManager;
    public int WIDTH;
    public int HEIGHT;
    private final AppSettings settings;
    
    // LWJGL
    private GraphicsDevice device;
    
    // Sky
    private SkyControl skyControl;
    
    // Network
    private NetworkAppState networkAppState;
    
    // Board
    private Board board;
    private Soldier sniper;
    private Mortar mortar;
    
    // Screenshots
    private ScreenshotAppState printScreen;
    
    // GUI
    private MenuAppState menuAppState;
    
    // Interfaces
    private final ArrayList<ScreenResize> screenInterfaces = new ArrayList<ScreenResize>();
    
    // Debug
    private Node mortarN;
    private boolean isAerialView = false;
    private Vector3f camOriginalPos;
    
    public GameplayAppState(AppSettings settings) {
        this.settings = settings;
        this.WIDTH = this.settings.getWidth();
        this.HEIGHT = this.settings.getHeight();
    }
    
@Override
    public void initialize(AppStateManager stateManager, Application app) {
        // Initializes essential variables
        this.app = (SimpleApplication)app;
        this.stateManager = stateManager;
        this.assetManager = this.app.getAssetManager();
        this.rootNode = this.app.getRootNode();
        this.viewPort = this.app.getViewPort();
        this.cam = this.app.getCamera();
        this.flyCam = this.app.getFlyByCamera();
        this.inputManager = this.app.getInputManager();
        
        // Loads board and debug units
        board = new PlainsBoard("B_Plains", assetManager, rootNode, viewPort);
        sniper = new Soldier(0, 0, assetManager.loadModel("Models/U_Soldier.j3o"), assetManager);
        rootNode.attachChild(sniper.getUnitNode());
        mortar = new Mortar(0, 1, assetManager.loadModel("Models/U_Mortar.j3o"));
        Cannon cannon = new Cannon(0, -1, assetManager.loadModel("Models/U_Cannon.j3o"));
        rootNode.attachChild(mortar.getUnitNode());
        rootNode.attachChild(cannon.getUnitNode());
        
        // Loads sky
        skyControl = new SkyControl(assetManager, cam, rootNode, viewPort, stateManager);
        skyControl.turnOn();
        rootNode.addControl(skyControl);
        
        // Changes camera's velocity
        flyCam.setMoveSpeed(25);
        
        // Add inputs
        // DEBUG: Activate for debug purposes.
        //inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        
        inputManager.addMapping("Wireframe", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("Print Screen", new KeyTrigger(KeyInput.KEY_O));
        inputManager.addMapping("Anim1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("Anim2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("Anim3", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("CreateBoard1", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("CreateBoard8", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("Aerial Cam", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("BenchmarkBoard", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("PauseMenu", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("MouseMoved", new MouseAxisTrigger(MouseInput.AXIS_X, true),
                                            new MouseAxisTrigger(MouseInput.AXIS_X, false),
                                            new MouseAxisTrigger(MouseInput.AXIS_Y, true),
                                            new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        
        inputManager.addListener(actionListener, "Wireframe", "Print Screen",
                "Anim1", "Anim2", "Anim3", "CreateBoard1",
                "Aerial Cam", "BenchmarkBoard", "Pause Menu");
        inputManager.addListener(analogListener, "CreateBoard8", "MouseMoved");
        
        // Hides debug stats
        this.app.setDisplayStatView(false);
        
        // Sets camera's position and rotation
        cam.setLocation(new Vector3f(0, 28, 39));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        // Adds Print Screen functionality
        printScreen = new ScreenshotAppState("screenshots\\");
        stateManager.attach(printScreen);
        
        // Starts network
        // DEBUG: We don't need servers for now
        //networkAppState = new NetworkAppState(this);
        //stateManager.attach(networkAppState);
        
        // Adds GUI
        menuAppState = new MenuAppState(networkAppState, WIDTH, HEIGHT);
        stateManager.attach(menuAppState);
        
        // Adds SceneProcessor to iterate through screenResize objects
        viewPort.addProcessor(new SceneProcessor() {

            public void reshape(ViewPort vp, int i, int i1) {
                // Prevents divison by zero exceptions on GUI calculations due
                // to too small resolution
                if(i < Fields.MIN_WIDTH || i1 < Fields.MIN_HEIGHT) {
                    AppSettings settings = new AppSettings(true);
                    settings.setResolution(Fields.MIN_WIDTH, Fields.MIN_HEIGHT);
                    settings.setResizable(true);
                    i = Fields.MIN_WIDTH;
                    i1 = Fields.MIN_HEIGHT;
                    GameplayAppState.this.app.setSettings(settings);
                    GameplayAppState.this.app.restart();
                }
                for(ScreenResize object : screenInterfaces) {
                    object.onScreenResize(i, i1);
                }
            }
            
            public void initialize(RenderManager rm, ViewPort vp) {
            }

            public boolean isInitialized() {
                return true;
            }

            public void preFrame(float f) {
            }

            public void postQueue(RenderQueue rq) {
            }

            public void postFrame(FrameBuffer fb) {
            }

            public void cleanup() {
            }
        });
        
        // Adds a ScreenResize to the list
        this.addScreenResize(screenResize);
        
        // Continues to initialize
        super.initialize(stateManager, app);
        
        // DEBUG:
        mortarN = mortar.getRotaryModel();
    }

    @Override
    public void update(float tpf) {
        mortarN.rotate(0, tpf, 0);
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }
    
    private final ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("Wireframe")) {
                wireframe(rootNode, isPressed);
            }
            if(name.equals("Print Screen") && isPressed) {
                System.out.println("Screenshot!");
                printScreen.takeScreenshot();
            }
            if(name.equals("Anim1") && !isPressed) {
                sniper.setAnim(Sniper.ANIM_WALK);
            }
            if(name.equals("Anim2") && !isPressed) {
                sniper.setAnim(Sniper.ANIM_SHOOT);
            }
            if(name.equals("Anim3") && !isPressed) {
                sniper.setAnim(Sniper.ANIM_DIE);
            }
            if(name.equals("CreateBoard1") && !isPressed) {
                board.newBoard();
            }
            if(name.equals("PauseMenu") && isPressed) {
                if(menuAppState.getCurrentScreen().equals("hud")) {
                    menuAppState.cancelLogin();
                    flyCam.setEnabled(false);
                    inputManager.setCursorVisible(true);
                }
            }
            if(name.equals("Aerial Cam") && isPressed) {
                if(isAerialView) {
                    isAerialView = false;
                    cam.setLocation(camOriginalPos);
                } else {
                    isAerialView = true;
                    camOriginalPos = cam.getLocation().clone();
                    cam.setLocation(new Vector3f(0, 50, 0.1f));
                }
            }
            if(name.equals("BenchmarkBoard") && !isPressed) {
                board.benchmarkBoard();
            }
            if(name.equals("MousePos") && !isPressed) {
                System.out.println(inputManager.getCursorPosition());
            }
            if(name.equals("GUIInfo") && !isPressed) {
                for(Spatial s : app.getGuiNode().getChildren()) {
                    System.out.println("\n\n\n\n\n");
                    System.out.println("Name: " + s.getName() + "\n");
                    System.out.println("Pos: " + s.getWorldTranslation() + "\n");
                    System.out.println("Rot: " + s.getWorldRotation() + "\n");
                    
                }
            }
        }
    };
    
    private final AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float intensidade, float tpf) {
            if(name.equals("CreateBoard8")) {
                board.newBoard();
            }
            if(name.equals("MouseMoved")) {
                menuAppState.mouseMoved();
            }
        }
    };
    
    private final ScreenResize screenResize = new ScreenResize() {
        public void onScreenResize(int width, int height) {
            cam.resize(width, height, true);
        }
    };
    
    public void addScreenResize(ScreenResize object) {
        screenInterfaces.add(object);
    }
    
    public void removeScreenResize(ScreenResize object) {
        screenInterfaces.remove(object);
    }
    
    public void wireframe(Spatial spatial, boolean wire) {
        if(spatial instanceof Node) {
            for(Spatial n : ((Node)spatial).getChildren()) {
                wireframe(n, wire);
            }
        } else if(spatial instanceof Geometry) {
            Geometry g = (Geometry)spatial;
            g.getMaterial().getAdditionalRenderState().setWireframe(wire);
        }
    }

    @Override
    public void cleanup() {
        // Stops the application
        app.stop();
        
        // Continues to cleanup
        super.cleanup();
    }
}
