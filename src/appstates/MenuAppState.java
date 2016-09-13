package appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.nifty.tools.SizeValueType;

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
    
    // GUI variables
    private boolean isLoginServerDown;
    private String errorMessage; // Used because I can't change Nifty text before it changes
    
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
        
        // Starts and loads the GUI
        this.niftyJME = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        this.nifty = this.niftyJME.getNifty();
        this.nifty.registerScreenController(this);
        // DEBUG: Start already on "hud" to skip login server need
        this.nifty.fromXml("Interface/gui.xml", "hud");
        //this.nifty.fromXml("Interface/gui.xml", "start");
        inputManager.setCursorVisible(true);
        this.app.getFlyByCamera().setEnabled(true);
        //this.loginPopup = this.nifty.createPopup("login");
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
        
        // Continues to initialize
        super.initialize(stateManager, app);
    }
    
    @Override
    public void update(float tpf) {
        if(isLoginServerDown) {
            isLoginServerDown = false;
            nifty.gotoScreen("loginFailed");
        }
        if(nifty.getCurrentScreen().getScreenId().equals("loginFailed")) {
            Label loginLabel = nifty.getCurrentScreen().findElementById("loginLabel").getNiftyControl(Label.class);
            loginLabel.setText("Login failed for the following reason(s): "  + errorMessage);
            //loginLabel.getElement().getRenderer(TextRenderer.class).setText("Login failed for the following reason(s): "  + errorMessage);
        }
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        
    }
    
    @Override
    public void onStartScreen() {
        if(nifty.getCurrentScreen().getScreenId().equals("hud")) {
            long widthPanels = Math.round(HEIGHT * 0.25);
            long widthSpace = Math.round((WIDTH * 0.9 - (3*widthPanels))/4);
            
            // Resize panels according to screen resolution
            for(int i = 1; i <= 3; i++) {
                //nifty.getCurrentScreen().findElementById("selection" + i).setWidth((int)widthPanels);
                nifty.getCurrentScreen().findElementById("selection" + i).setConstraintWidth(new SizeValue((int)widthPanels, SizeValueType.Pixel));
            }
            
            for(int i = 1; i <= 4; i++) {
                nifty.getCurrentScreen().findElementById("space" + i).setConstraintWidth(new SizeValue((int)widthSpace, SizeValueType.Pixel));
            }
            nifty.getCurrentScreen().layoutLayers();
        }
    }
    
    @Override
    public void onEndScreen() {
        
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