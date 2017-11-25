package appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.CameraInput;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import controls.SkyControl;
import java.util.ArrayList;
import squares.Grass;
import squares.Hole;
import squares.Lake;
import squares.Rock;
import squares.Tree;

public class EditorAppState extends AbstractAppState {
    
    // Variables
    // Essential
    private SimpleApplication app;
    private AppStateManager stateManager;
    private AssetManager assetManager;
    private InputManager inputManager;
    private Node rootNode;
    private Camera cam;
    
    // Sky
    private SkyControl skyControl;
    
    // Editor
    private boolean placingUnit = true;
    // DEBUG
    private Line line;
    private Geometry lineGeo;
    private Material lineMat;
    // Objects
    private Spatial currentObject;
    private int currentIndex = 0;
    private ArrayList<Spatial> objects;
    private final String[] assetNames = {Grass.NAME,
                                    Hole.NAME,
                                    Lake.NAME,
                                    Rock.NAME,
                                    Tree.NAME};
    // Grid
    private final int NUM_SQUARES = 8;
    private final Grid grid = new Grid(NUM_SQUARES + 1, NUM_SQUARES + 1, 2);
    private final Geometry gridGeo = new Geometry("Grid", grid);
    private Material gridMat;
    // Camera
    private final int CAM_VELOCITY = 15;
    
    // Input mappings
    private static final String NEXT_OBJECT_UP = "Next Object Up";
    private static final String NEXT_OBJECT_DOWN = "Next Object Down";
    
    private static final String MOUSE_MOVEMENT = "Mouse Movement";
    private static final String CLICK_PLACE_UNIT = "Place Unit";
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        // Initializes essential variables
        this.app = (SimpleApplication)app;
        this.stateManager = stateManager;
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.rootNode = this.app.getRootNode();
        this.cam = this.app.getCamera();
        
        // Define inputs
        // - Deletes already existing camera inputs
        this.inputManager.deleteMapping(CameraInput.FLYCAM_ZOOMIN);
        this.inputManager.deleteMapping(CameraInput.FLYCAM_ZOOMOUT);
        // - Adds new mappings and triggers
        this.inputManager.addMapping(NEXT_OBJECT_UP, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        this.inputManager.addMapping(NEXT_OBJECT_DOWN, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

        // - Adds the mappings to the listeners
        this.inputManager.addListener(actionListener, NEXT_OBJECT_UP, NEXT_OBJECT_DOWN);
        
        this.inputManager.addRawInputListener(rawInputListener);
        
        // Bootstraps the world
        // - Sets up the sky
        this.skyControl = new SkyControl(this.assetManager, this.cam, rootNode, this.app.getViewPort(), stateManager);
        this.skyControl.turnOn();
        rootNode.addControl(skyControl);
        
        // - Sets up the grid
        grid.setMode(Mode.Lines);
        gridMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        gridMat.setColor("Color", ColorRGBA.Gray);
        gridGeo.setMaterial(gridMat);
        gridGeo.setLocalTranslation(-NUM_SQUARES, 0, -NUM_SQUARES);
        rootNode.attachChild(gridGeo);
        
        // - Sets up the camera
        this.app.getFlyByCamera().setMoveSpeed(25);
        
        // Loads assets
        this.objects = new ArrayList<>();
        for(String assetName : assetNames) {
            Spatial object = this.assetManager.loadModel("Models/T_" + assetName + ".j3o");
            
            objects.add(object);
        }
        currentObject = objects.get(currentIndex);
        
        // DEBUG
        rootNode.attachChild(currentObject);
        //inputManager.setCursorVisible(true);
        //this.app.getFlyByCamera().setEnabled(false);
        this.cam.setLocation(new Vector3f(0, 15, 10));
        this.cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        this.line = new Line(Vector3f.ZERO, Vector3f.UNIT_Y);
        this.lineGeo = new Geometry("DEBUG:Line", line);
        this.lineGeo.setCullHint(CullHint.Never);
        this.lineMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        this.lineMat.setColor("Color", ColorRGBA.Blue);
        this.lineGeo.setMaterial(lineMat);
        rootNode.attachChild(lineGeo);
        
        // Continues initializing
        super.initialize(stateManager, app);
    }
    
    @Override
    public void update(float tpf) {
        
    }
    
    private final ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals(NEXT_OBJECT_UP)) {
                // Mouse up
                if(++currentIndex > objects.size() - 1) {
                    currentIndex = 0;
                }
                
                rootNode.detachChild(currentObject);
                currentObject = objects.get(currentIndex);
                rootNode.attachChild(currentObject);
            } else if(name.equals(NEXT_OBJECT_DOWN)) {
                // Mouse down
                if(--currentIndex < 0) {
                    currentIndex = objects.size() - 1;
                }
                
                rootNode.detachChild(currentObject);
                currentObject = objects.get(currentIndex);
                rootNode.attachChild(currentObject);
            }
        }
    };
    
    private final RawInputListener rawInputListener = new RawInputListener() {
        @Override
        public void beginInput() {}

        @Override
        public void endInput() {}

        @Override
        public void onJoyAxisEvent(JoyAxisEvent jae) {}

        @Override
        public void onJoyButtonEvent(JoyButtonEvent jbe) {}

        @Override
        public void onMouseMotionEvent(MouseMotionEvent mme) {
            if(placingUnit) {
                // With mouse
                /*Vector2f mousePos = new Vector2f(mme.getX(), mme.getY());
                Vector3f mousePos3D = cam.getWorldCoordinates(mousePos, 0);
                Vector3f dir = cam.getWorldCoordinates(mousePos, 1).subtractLocal(mousePos3D).normalizeLocal();*/
                
                // Without mouse
                Vector3f mousePos3D = cam.getLocation();
                Vector3f dir = cam.getDirection();
                
                CollisionResults results = new CollisionResults();
                
                Ray ray = new Ray(mousePos3D, dir);
                
                rootNode.collideWith(ray, results);
                
                for(int i = 0; i < results.size(); i++) {
                    line.updatePoints(results.getClosestCollision().getContactPoint(), Vector3f.UNIT_Y);
                    System.out.println(i + ':' + results.getCollision(i).getGeometry().getName());
                    if(results.getCollision(i).getGeometry().getName().equals("Grid")) {
                        Vector2f squareCoords = getSquare(results.getCollision(i).getContactPoint());
                        if(squareCoords != null) {
                            currentObject.setLocalTranslation(squareCoords.x, 0.1f, squareCoords.y);
                        }
                    }
                }
            }
        }

        @Override
        public void onMouseButtonEvent(MouseButtonEvent mbe) {}

        @Override
        public void onKeyEvent(KeyInputEvent kie) {}

        @Override
        public void onTouchEvent(TouchEvent te) {}
    };
    
    private Vector2f getSquare(Vector3f contactPoint) {
        int x = (int)(Math.floor(contactPoint.getX() / 2f) + NUM_SQUARES / 2f) + 1;
        int z = (int)(Math.floor(contactPoint.getZ() / 2f) + NUM_SQUARES / 2f) + 1;
        if(x < -NUM_SQUARES || z < -NUM_SQUARES) {
            return null;
        }
        System.out.println("Called!\t" + contactPoint.x + '\t' + z + '\t' + contactPoint.z);
        return new Vector2f(x * 2 - 1 - NUM_SQUARES, z * 2 - 1 - NUM_SQUARES);
    }
    
    @Override
    public void cleanup() {
        
        
        // Continues cleanup
        super.cleanup();
    }
}
