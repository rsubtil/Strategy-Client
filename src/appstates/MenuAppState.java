package appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture2D;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.dynamic.PanelCreator;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import interfaces.ScreenResize;
import java.util.ArrayList;

public class MenuAppState extends AbstractAppState implements ScreenController {
    
    // Variables
    // Application
    private SimpleApplication app;
    private AppStateManager stateManager;
    private final NetworkAppState networkAppState;
    public final int WIDTH;
    public final int HEIGHT;
    
    // App's variables
    private AssetManager assetManager;
    private InputManager inputManager;
    private AudioRenderer audioRenderer;
    private ViewPort guiViewPort;
    
    // GUI
    private NiftyJmeDisplay niftyJME;
    private Nifty nifty;
    private Node guiNode;
    
    // Rendering 3D objects on guiNode
    private Camera offCamera;
    private ViewPort modelView;
    private FrameBuffer offBuffer;
    private Texture2D offTex;
    
    // HUD constants
    private final int MIN_SPACE_SIZE = 32;
    private final int MAX_PANELS = 9;
    
    // GUI variables
    private boolean isLoginServerDown;
    private String errorMessage; // Used because I can't change Nifty text before it changes
    private boolean layedGUI = false;
    
    public MenuAppState(NetworkAppState networkAppState, int WIDTH, int HEIGHT) {
        this.networkAppState = networkAppState;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = (SimpleApplication)app;
        this.stateManager = stateManager;
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.audioRenderer = this.app.getAudioRenderer();
        this.guiViewPort = this.app.getGuiViewPort();
        this.guiNode = this.app.getGuiNode();
        
        // Starts and loads the GUI
        this.niftyJME = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        this.nifty = this.niftyJME.getNifty();
        this.nifty.registerScreenController(this);
        // DEBUG: Start already on "hud" to skip login server need
        this.nifty.fromXml("Interface/gui.xml", "hud");
        //this.nifty.fromXml("Interface/gui.xml", "start");
        inputManager.setCursorVisible(true);
        this.app.getFlyByCamera().setEnabled(true);
        
        // DEBUG: Validates the XML file
        try {
            nifty.validateXml("Interface/gui.xml");
        } catch(Exception e) {
            System.out.println("erro");
            e.printStackTrace();
        }
        //this.nifty.fromXml("Interface/gui.xml", "start", new MenuController());
        this.app.getGuiViewPort().addProcessor(niftyJME);
        
        // Enalbes the mouse and disable's the flyCam
        //inputManager.setCursorVisible(true);
        //this.app.getFlyByCamera().setEnabled(false);
        
        // Adds the ScreenResize to the GameplayAppState's list
        stateManager.getState(GameplayAppState.class).addScreenResize(screenResize);
        
        // Adds a light to guiNode so the selector's models are visible
        LightList lightList = this.app.getRootNode().getWorldLightList();
        for(Light light : lightList) {
            guiNode.addLight(light);
        }
        
        // Continues to initialize
        super.initialize(stateManager, app);
    }
    
    @Override
    public void update(float tpf) {
        if(isLoginServerDown) {
            isLoginServerDown = false;
            nifty.gotoScreen("loginFailed");
        }
        if(getCurrentScreen().equals("loginFailed")) {
            Label loginLabel = nifty.getCurrentScreen().findElementById("loginLabel").getNiftyControl(Label.class);
            loginLabel.setText("Login failed for the following reason(s): "  + errorMessage);
            //loginLabel.getElement().getRenderer(TextRenderer.class).setText("Login failed for the following reason(s): "  + errorMessage);
        } else if(getCurrentScreen().equals("hud") && !layedGUI) {
            layedGUI = true;
            layoutResponsiveGUI(WIDTH, HEIGHT);
            System.out.println("test");
        }
    }
    
    private final ScreenResize screenResize = new ScreenResize() {
        public void onScreenResize(int width, int height) {
            layoutResponsiveGUI(width, height);
        }
    };
    
    private void layoutResponsiveGUI(int width, int height) {
        if(getCurrentScreen().equals("hud")) {
            // Gets the true width and height for the panels.
            // Real width is equal to width minus the arrows's size (which is 10%)
            int guiWidth = Math.round(width * 0.9f);
            
            // Real height is just the panel's size, 15%
            int guiHeight = Math.round(height * 0.15f);
            
            // Gets width of the selection panels, which is just the guiHeight
            int widthPanels = guiHeight;
                          
            // Get the number of panels that fit in each section, ensuring every space has at least MIN_SPACE_SIZE pixels
            int numPanels = (int)FastMath.floor(guiWidth / widthPanels);
            int numSpaces = numPanels + 1;
            
            numPanels = numPanels > MAX_PANELS ? MAX_PANELS : numPanels;
            numSpaces = numSpaces > numPanels + 1 ? numPanels + 1: numSpaces;
            
            int spaceSizeTotal = guiWidth - numPanels * widthPanels;
            int spaceSize = Math.round(spaceSizeTotal / numSpaces);
            
            while(spaceSize < MIN_SPACE_SIZE) {
                spaceSize = MIN_SPACE_SIZE;
                spaceSizeTotal = spaceSize * numSpaces;
                while((spaceSizeTotal + (numPanels * widthPanels)) > guiWidth) {
                    numPanels--;
                    numSpaces--;
                    spaceSizeTotal = guiWidth - numPanels * widthPanels;
                    spaceSize = Math.round(spaceSizeTotal / numSpaces);
                }
            } 
            
            int panelsForLastSelection = MAX_PANELS % numPanels;
            
            int numSelections = (int)FastMath.ceil(MAX_PANELS / (float)numPanels);
            int numFilledSelections = panelsForLastSelection > 0 ? numSelections - 1 : numSelections;
            
            System.out.println("\n\n\n\n\n\n\n\n");
            System.out.println("guiWidth: " + guiWidth);
            System.out.println("guiHeight: " + guiHeight);
            System.out.println("widthPanels: " + widthPanels);
            System.out.println("numPanels: " + numPanels);
            System.out.println("numSpaces: " + numSpaces);
            System.out.println("spaceSizeTotal: " + spaceSizeTotal);
            System.out.println("spaceSize: " + spaceSize);
            System.out.println("panelsForLastSelection: " + panelsForLastSelection);
            System.out.println("numSelections: " + numSelections);
            System.out.println("numFilledSelections: " + numFilledSelections);
            
            Element panel = nifty.getCurrentScreen().findElementById("units_selector");
            
            // Removes all children from the panel
            for(Element element : panel.getChildren()) {
                nifty.removeElement(nifty.getCurrentScreen(), element);
            }
            
            ArrayList<Element> selections = new ArrayList<Element>();
            
            PanelCreator selection = new PanelCreator();
            selection.setChildLayout("horizontal");
            selection.setWidth("100%");
             
            PanelCreator space = new PanelCreator();
            space.setChildLayout("center");
            space.setHeight("100%");
            space.setWidth(spaceSize + "px");
            
            PanelCreator unit = new PanelCreator();
            unit.setChildLayout("center");
            unit.setHeight("100%");
            unit.setWidth(widthPanels + "px");
            unit.setStyle("nifty-panel-simple");
            
            ImageBuilder image = new ImageBuilder();
            image.height("100%");
            image.filename("Interface/separator.png");
            image.set("filter", "true");
            image.width("32");
            image.imageMode("resize:16,0,16,15,15,2,15,2,16,0,16,15");
            
            ImageBuilder unitIcon = new ImageBuilder();
            
            unitIcon.set("filter", "true");
            
            
            // DEBUG
            //guiNode.detachAllChildren();
            
            int absoluteSpaceID = 0;
            for(int i = 0; i < numFilledSelections; i++) {
                Element selectionElement = selection.create(nifty, nifty.getCurrentScreen(), panel);
                selectionElement.setId("selection-" + i);
                for(int n = 0; n < numPanels; n++) {
                    absoluteSpaceID++;
                    // Creates a space
                    Element spaceElement = space.create(nifty, nifty.getCurrentScreen(), selectionElement);
                    spaceElement.setId("space-" + i + "-" + n + ":" + absoluteSpaceID);
                    
                    // Sets the image for the number of the panel and also it's size
                    // (since all images have different sizes)
                    switch(absoluteSpaceID) {
                        case 1:
                            unitIcon.filename("Interface/Icons/Units/Tr_Soldier.png");
                            unitIcon.height(widthPanels + "px");
                            unitIcon.width((int)(widthPanels * 228 / 475f) + "px");
                            break;
                        case 2:
                            unitIcon.filename("Interface/Icons/Units/Tr_Cannon.png");
                            unitIcon.height(widthPanels + "px");
                            unitIcon.width((int)(widthPanels * 390 / 407f) + "px");
                            break;
                        case 3:
                            unitIcon.filename("Interface/Icons/Units/Tr_Sniper.png");
                            unitIcon.height(widthPanels + "px");
                            unitIcon.width((int)(widthPanels * 166 / 487f) + "px");
                            break;
                        case 4:
                            unitIcon.filename("Interface/Icons/Units/Tr_Mortar.png");
                            unitIcon.height(widthPanels + "px");
                            unitIcon.width((int)(widthPanels * 392 / 407f) + "px");
                            break;
                        case 5:
                            unitIcon.filename("Interface/Icons/Units/Tr_Soldier.png");
                            unitIcon.height(widthPanels + "px");
                            unitIcon.width((int)(widthPanels * 228 / 475f) + "px");
                            break;
                        case 6:
                            unitIcon.filename("Interface/Icons/Units/Tr_Soldier.png");
                            unitIcon.height(widthPanels + "px");
                            unitIcon.width((int)(widthPanels * 228 / 475f) + "px");
                            break;
                        case 7:
                            unitIcon.filename("Interface/Icons/Units/Tr_Soldier.png");
                            unitIcon.height(widthPanels + "px");
                            unitIcon.width((int)(widthPanels * 228 / 475f) + "px");
                            break;
                        case 8:
                            unitIcon.filename("Interface/Icons/Units/Tr_Soldier.png");
                            unitIcon.height(widthPanels + "px");
                            unitIcon.width((int)(widthPanels * 228 / 475f) + "px");
                            break;
                        case 9:
                            unitIcon.filename("Interface/Icons/Units/Tr_Soldier.png");
                            unitIcon.height(widthPanels + "px");
                            unitIcon.width((int)(widthPanels * 228 / 475f) + "px");
                            break;
                        case 10:
                            unitIcon.filename("Interface/Icons/Units/Tr_Soldier.png");
                            unitIcon.height(widthPanels + "px");
                            unitIcon.width((int)(widthPanels * 228 / 475f) + "px");
                            break;
                        default:
                            
                    }
                    
                    // Creates a panel
                    Element panelElement = unit.create(nifty, nifty.getCurrentScreen(), selectionElement);
                    panelElement.setId("panel-" + i + "-" + n + ":" + absoluteSpaceID);
                    
                    // Adds unit Icon to the panel
                    unitIcon.build(nifty, nifty.getCurrentScreen(), panelElement);
                    
                    if(absoluteSpaceID == 5 || absoluteSpaceID == 8) {
                        image.build(nifty, nifty.getCurrentScreen(), spaceElement);
                    }
                }
                
                absoluteSpaceID++;
                Element lastSpaceElement = space.create(nifty, nifty.getCurrentScreen(), selectionElement);
                lastSpaceElement.setId("space-" + i + "-" + numPanels + ":" + absoluteSpaceID);
                        
                if(absoluteSpaceID == 5 || absoluteSpaceID == 8) {
                    image.build(nifty, nifty.getCurrentScreen(), lastSpaceElement);
                }
                selections.add(selectionElement);
            }
            
            // Hides elements that technically souldn't be visible
            for(Element element : selections) {
                if(!element.getId().contains("selection-0")) {
                    element.hide();
                }
            }
            
            System.out.println("selections<>: " + selections);
        }
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        
    }
    
    @Override
    public void onStartScreen() {
        if(getCurrentScreen().equals("hud")) {
            layoutResponsiveGUI(WIDTH, HEIGHT);
        }
    }
    
    @Override
    public void onEndScreen() {
        if(getCurrentScreen().equals("hud")) {
            layedGUI = false;
        }
    }
    
    // Nifty's methods
    public void startGame() {
        nifty.gotoScreen("hud");
        inputManager.setCursorVisible(false);
        app.getFlyByCamera().setEnabled(true);
    }
    
    public void gotoLogin() {
        nifty.gotoScreen("login");
    }
    
    public void endGame() {
        cleanup();
    }
    
    public void cancelLogin() {
        nifty.gotoScreen("start");
    }
    
    public void login() {
        // Gets the elements that contain the login credentials and stores them into Strings
        Element username = nifty.getCurrentScreen().findElementById("usernameTextField");
        Element password = nifty.getCurrentScreen().findElementById("passwordTextField");
        String usernameString = username.findNiftyControl(username.getId(), TextField.class).getRealText();
        String passwordString = password.findNiftyControl(password.getId(), TextField.class).getRealText();
        // Prevents sending null Strings
        if(!(usernameString.isEmpty() || passwordString.isEmpty())) {
            nifty.gotoScreen("loginWait");
            networkAppState.loginIntoServer(usernameString, passwordString);
        }
    }

    public void loginResult(int type) {
        // TODO: Change numbers to constants
        if(type == 1) {
            nifty.gotoScreen("loginSuccessful");
        } else if(type == 2) {
            this.errorMessage = "Username doesn't exist!";
            nifty.gotoScreen("loginFailed");
        } else if(type == 3) {
            this.errorMessage = "Password didn't match!";
            nifty.gotoScreen("loginFailed");
        } else if(type == 4) {
            // Since Nifty only switches screen in another thread, I need to wait at least 1 frame for it to switch to loginFailed
            isLoginServerDown = true;
            this.errorMessage = "Login server isn't online! Check your internet connection or go to the forum and ask for help.";
        }
    }
    
    public String getLoginError() {
        return errorMessage;
    }
    
    public String getCurrentScreen() {
        return nifty.getCurrentScreen().getScreenId();
    }
    
    @Override
    public void cleanup() {
        app.stop();
        // Continues to clean up
        super.cleanup();
    }
}