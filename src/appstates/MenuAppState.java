package appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.HoverEffectBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.Menu;
import de.lessvoid.nifty.controls.MenuItemActivatedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.effects.impl.AutoScroll;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import gui.MenuItemActivatedEventSubscriber; 
import interfaces.ScreenResize;
import java.util.ArrayList;
import util.Fields;

public class MenuAppState extends AbstractAppState implements ScreenController {
    
    // Variables
    // Application
    private SimpleApplication app;
    private AppStateManager stateManager;
    private final NetworkAppState networkAppState;
    public int WIDTH;
    public int HEIGHT;
    
    // App's variables
    private AssetManager assetManager;
    private InputManager inputManager;
    private AudioRenderer audioRenderer;
    private ViewPort guiViewPort;
    
    // GUI
    private NiftyJmeDisplay niftyJME;
    private Nifty nifty;
    private Node guiNode;
    
    // HUD constants
    private final int MIN_SPACE_SIZE = 32; // Pixels
    private final int MAX_PANELS = 9;
    private final int MOUSE_IDLE_TIME = 9; // Seconds
    
    // GUI variables
    private boolean isLoginServerDown;
    private String errorMessage; // Used because I can't change Nifty text before it changes
    private boolean layedGUI = false;
    private ArrayList<Element> selections;
    private int currentIndex;
    private float mouseDelay = 0;
    private boolean guiHidden = false;
    //DEBUG
    private Label hideLabel;
    
    private PanelBuilder selection;          
    private PanelBuilder space;
    private PanelBuilder unit;    
    
    private ImageBuilder separatorIcon;      
    private ImageBuilder unitIcon;
    
    private HoverEffectBuilder sizeEffect;
    
    private Element popupMenu;

    
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
        
        // Starts important GUI builders before loading GUI
        selection = new PanelBuilder();
        selection.childLayoutHorizontal();
        selection.width("100%");
        
        space = new PanelBuilder();
        space.childLayoutCenter();
        space.height("100%");
        
        unit = new PanelBuilder();
        unit.childLayoutCenter();
        unit.style("nifty-panel-simple");
        unit.visibleToMouse(true);
        
        separatorIcon = new ImageBuilder();
        separatorIcon.height("100%");
        separatorIcon.filename("Interface/separator.png");
        separatorIcon.set("filter", "true");
        separatorIcon.width("32");
        separatorIcon.imageMode("resize:16,0,16,15,15,2,15,2,16,0,16,15");
        
        sizeEffect = new HoverEffectBuilder("size");
        sizeEffect.effectParameter("size", "1.05");
        
        unitIcon = new ImageBuilder();
        unitIcon.set("filter", "true");
        
        /*popup = nifty.createPopup("POPUP");
        Menu<MenuItem> menu = popup.findNiftyControl("#menu", Menu.class);
        menu.setWidth(new SizeValue("100px"));
        menu.addMenuItem("Click me!", "Interface/Icons/Info/InfoIcon1.png", new MenuItem("clickMe", "blah blah"));
        nifty.subscribe(nifty.getCurrentScreen(), menu.getId(), MenuItemActivatedEvent.class, new MenuItemActivatedEventSubscriber());
        nifty.showPopup(nifty.getCurrentScreen(), popup.getId(), null);
        */
        
        // Starts and loads the GUI
        this.niftyJME = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        this.nifty = this.niftyJME.getNifty();
        this.nifty.registerScreenController(this);
        // DEBUG: Start already on "hud" to skip login server need
        this.nifty.fromXml("Interface/gui.xml", "hud");
        //this.nifty.fromXml("Interface/gui.xml", "start");
        inputManager.setCursorVisible(true);
        inputManager.addRawInputListener(rawInputListener);
        this.app.getFlyByCamera().setEnabled(true);
        
        // DEBUG: Validates the XML file
        /*try {
            nifty.validateXml("Interface/gui.xml");
        } catch(Exception e) {
            System.out.println("erro");
            e.printStackTrace();
        }*/
        //this.nifty.fromXml("Interface/gui.xml", "start", new MenuController());
        this.app.getGuiViewPort().addProcessor(niftyJME);
        
        // Adds the ScreenResize to the GameplayAppState's list
        stateManager.getState(GameplayAppState.class).addScreenResize(screenResize);
        
        // DEBUG: To show hiding value
        hideLabel = nifty.getCurrentScreen().findNiftyControl("stats", Label.class);
        
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
        } else if(getCurrentScreen().equals("hud") && !layedGUI && nifty.getCurrentScreen().findElementById("units_selector").getChildrenCount() == 0) {
            layoutResponsiveGUI(WIDTH, HEIGHT);
        }
        updateTimers(tpf);
        if(!guiHidden) {
            hideLabel.setText("DEBUG: GUI will hide in " + String.format(java.util.Locale.US, "%.2f", MOUSE_IDLE_TIME - mouseDelay) + "s. (Don't move mouse or it will reset)");
        }
        // Hides GUI if there's no mouse movement for a while
        if(mouseDelay >= MOUSE_IDLE_TIME && !guiHidden) {
            inputManager.setCursorVisible(false);
            hideLabel.setText("See ya");
            nifty.getCurrentScreen().findElementById("stats").
                    startEffect(EffectEventId.onCustom);
            nifty.getCurrentScreen().findElementById("selectables").
                    startEffect(EffectEventId.onCustom);
            nifty.closePopup(popupMenu.getId());
            guiHidden = true;
        }
    }
    
    private void updateTimers(float tpf) {
        mouseDelay += tpf;
        
    }
    
    private final ScreenResize screenResize = new ScreenResize() {
        @Override
        public void onScreenResize(int width, int height) {
            layedGUI = false;
            Element element = nifty.getCurrentScreen().findElementById("units_selector");
            for(Element child : element.getChildren()) {
                child.markForRemoval();
            }
            nifty.closePopup(popupMenu.getId());
            //layoutResponsiveGUI(width, height);
            WIDTH = width;
            HEIGHT = height;
        }
    };
    
    private void layoutResponsiveGUI(int width, int height) {
        if(!layedGUI) {
            // Gets the true width and height for the panels.
            // Real width is equal to width minus the arrows's size (which is 10%)
            int guiWidth = Math.round(width * 0.9f);
            
            // Real height is just the panel's size, 15%
            int guiHeight = Math.round(height * 0.15f);
            
            // Gets width of the selection panels, which is 95% of the guiHeight
            int widthPanels = (int)FastMath.floor(0.95f * guiHeight);
                          
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
            
            int panelsForLastSelection = numPanels == 0 ? MAX_PANELS % 1 : MAX_PANELS % numPanels;
            
            int numSelections = (int)FastMath.ceil(MAX_PANELS / (float)numPanels);
            int numFilledSelections = panelsForLastSelection > 0 ? numSelections - 1 : numSelections;
            
            int lastSpaceSize = panelsForLastSelection > 0 ? Math.round((guiWidth - panelsForLastSelection * widthPanels) / (panelsForLastSelection + 1)) : 0;
            
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
            System.out.println("lastSpaceSize: " + lastSpaceSize);
            
            Element panel = nifty.getCurrentScreen().findElementById("units_selector");
            
            selections = new ArrayList<Element>();
            currentIndex = 0;
            
            space.width(spaceSize + "px");
            unit.width(widthPanels + "px");
            unit.height(widthPanels + "px");
            
            // DEBUG
            //guiNode.detachAllChildren();
            
            int absoluteSpaceID = 0;
            // Starts by creating the filled selections
            for(int i = 0; i < numFilledSelections; i++) {
                selection.id("selection-" + i);
                Element selectionElement = selection.build(nifty, nifty.getCurrentScreen(), panel);
                for(int n = 0; n < numPanels; n++) {
                    absoluteSpaceID++;
                    // Creates a space
                    space.id("space-" + i + "-" + n + ":" + absoluteSpaceID);
                    Element spaceElement = space.build(nifty, nifty.getCurrentScreen(), selectionElement);
                    
                    // Sets the image for the number of the panel and also it's size
                    unitIcon.filename("Interface/Icons/Units/Icon_" + absoluteSpaceID + ".png");
                    unitIcon.height(widthPanels + "px");
                    unitIcon.width(widthPanels + "px");
                    
                    // Creates a panel
                    unit.id("panel-" + i + "-" + n + ":" + absoluteSpaceID);
                    unit.onHoverEffect(sizeEffect);
                    unit.interactOnClick("unitClick(" + absoluteSpaceID + ")");
                    
                    Element panelElement = unit.build(nifty, nifty.getCurrentScreen(), selectionElement);
                    
                    // Adds unit Icon to the panel
                    unitIcon.id(panelElement.getId() + "(image)");
                    unitIcon.build(nifty, nifty.getCurrentScreen(), panelElement);
                    
                    if(absoluteSpaceID == 5 || absoluteSpaceID == 8) {
                        separatorIcon.id(spaceElement.getId() + "(image)");
                        separatorIcon.build(nifty, nifty.getCurrentScreen(), spaceElement);
                    }
                }
                
                space.id("space-" + i + "-" + numPanels + ":" + (absoluteSpaceID + 1));
                Element lastSpaceElement = space.build(nifty, nifty.getCurrentScreen(), selectionElement);
                        
                if(absoluteSpaceID == 4 || absoluteSpaceID == 7) {
                    separatorIcon.id(lastSpaceElement.getId() + "(image)");
                    separatorIcon.build(nifty, nifty.getCurrentScreen(), lastSpaceElement);
                }
                selections.add(selectionElement);
                currentIndex++;
            }
            
            // In case there is an incomplete selection, create it
            if(panelsForLastSelection > 0) {
                selection.id("selection-" + currentIndex);
                Element selectionElement = selection.build(nifty, nifty.getCurrentScreen(), panel);
                for(int n = 0; n < panelsForLastSelection; n++) {
                    absoluteSpaceID++;
                    // Creates a space
                    space.id("space-" + currentIndex + "-" + n + ":" + absoluteSpaceID);
                    space.width(lastSpaceSize + "px");
                    Element spaceElement = space.build(nifty, nifty.getCurrentScreen(), selectionElement);
                    
                    // Sets the image for the number of the panel and also it's size
                    unitIcon.filename("Interface/Icons/Units/Icon_" + absoluteSpaceID + ".png");
                    unitIcon.height(widthPanels + "px");
                    unitIcon.width(widthPanels + "px");
                    
                    // Creates a panel
                    unit.id("panel-" + currentIndex + "-" + n + ":" + absoluteSpaceID);
                    unit.onHoverEffect(sizeEffect);
                    unit.interactOnClick("unitClick(" + absoluteSpaceID + ")");
                    
                    Element panelElement = unit.build(nifty, nifty.getCurrentScreen(), selectionElement);
                    
                    // Adds unit Icon to the panel
                    unitIcon.id(panelElement.getId() + "(image)");
                    unitIcon.build(nifty, nifty.getCurrentScreen(), panelElement);
                    
                    if(absoluteSpaceID == 5 || absoluteSpaceID == 8) {
                        separatorIcon.id(spaceElement.getId() + "(image)");
                        separatorIcon.build(nifty, nifty.getCurrentScreen(), spaceElement);
                    }
                }
                selections.add(selectionElement);
            }
            
            // Hides elements that technically souldn't be visible
            currentIndex = 0;
            loadSelection(currentIndex);
            manageArrows(currentIndex);
            
            // Gets values for autoScroll effects
            nifty.getCurrentScreen().findElementById("stats").
                    getEffects(EffectEventId.onCustom, AutoScroll.class).
                    get(0).getParameters().setProperty
                    ("end", String.valueOf(Math.round(-height * 0.1f)));
            
            nifty.getCurrentScreen().findElementById("selectables").
                    getEffects(EffectEventId.onCustom, AutoScroll.class).
                    get(0).getParameters().setProperty
                    ("end", String.valueOf(Math.round(height * 0.25)));
            
            layedGUI = true;
        }
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        if(screen.getScreenId().equals("hud")) {
            createPopupMenu();
        }
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
    
    private void createPopupMenu() {
        this.popupMenu = nifty.createPopup("popupMenu");
        
        Menu<Integer> menu = popupMenu.findNiftyControl("#menu", Menu.class);
        menu.setWidth(new SizeValue("150px"));
        menu.addMenuItem("Buy", Fields.MENU_OPTIONS.BUY.getValue());
        menu.addMenuItem("Info", Fields.MENU_OPTIONS.INFO.getValue());
        menu.addMenuItemSeparator();
        menu.addMenuItem("Exit", Fields.MENU_OPTIONS.BUY.EXIT.getValue());
        
        nifty.subscribe(nifty.getCurrentScreen(), menu.getId(), MenuItemActivatedEvent.class, new MenuItemActivatedEventSubscriber());
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
    
    public void moveSelection(String isLeft) {
        boolean isLeftBoolean = Boolean.parseBoolean(isLeft);
        if(isLeftBoolean) {
            // Move selection to the left
            int index = currentIndex - 1;
            if(index >= 0) {
                loadSelection(index);
                currentIndex = index;
            }
        } else {
            // Move selection to the right
            int index = currentIndex + 1;
            if(index < selections.size()) {
                loadSelection(index);
                currentIndex = index;
            }
        }
        manageArrows(currentIndex);
    }
    
    private void loadSelection(int index) {
        for(Element element : selections) {
            if(element.getId().contains("selection-" + index)) {
                element.show();
            } else {
                element.hide();
            }
        }
    }
    
    private void manageArrows(int index) {
        if(index == 0) {
            // Deactivates left arrow
            nifty.getCurrentScreen().findElementById("left_arrow").disable();
            ImageRenderer imageRenderer = nifty.getCurrentScreen().findElementById("left_arrow_icon").getRenderer(ImageRenderer.class);
            imageRenderer.getImage().dispose();
            imageRenderer.setImage(nifty.createImage("Interface/arrow_left_dark.png", false));
        } else {
            // Activates left arrow
            nifty.getCurrentScreen().findElementById("left_arrow").enable();
            ImageRenderer imageRenderer = nifty.getCurrentScreen().findElementById("left_arrow_icon").getRenderer(ImageRenderer.class);
            imageRenderer.getImage().dispose();
            imageRenderer.setImage(nifty.createImage("Interface/arrow_left.png", false));
        }
        if (index == selections.size() - 1) {
            // Deactivate right arrow
            nifty.getCurrentScreen().findElementById("right_arrow").disable();
            ImageRenderer imageRenderer = nifty.getCurrentScreen().findElementById("right_arrow_icon").getRenderer(ImageRenderer.class);
            imageRenderer.getImage().dispose();
            imageRenderer.setImage(nifty.createImage("Interface/arrow_right_dark.png", false));
        } else {
            // Activates right arrow
            nifty.getCurrentScreen().findElementById("right_arrow").enable();
            ImageRenderer imageRenderer = nifty.getCurrentScreen().findElementById("right_arrow_icon").getRenderer(ImageRenderer.class);
            imageRenderer.getImage().dispose();
            imageRenderer.setImage(nifty.createImage("Interface/arrow_right.png", false));
        }
    }
    
    public void mouseMoved() {
        mouseDelay = 0;
        guiHidden = false;
        //try {
            nifty.getCurrentScreen().findElementById("stats").
                    resetSingleEffect(EffectEventId.onCustom);
            nifty.getCurrentScreen().findElementById("selectables").
                    resetSingleEffect(EffectEventId.onCustom);
            inputManager.setCursorVisible(true);
        //} catch(NullPointerException npe) {
            // Nifty didn't finish initializing GUI
        //}
    }
    
    public String getLoginError() {
        return errorMessage;
    }
    
    public String getCurrentScreen() {
        return nifty.getCurrentScreen().getScreenId();
    }
    
    public void unitClick(String id) {
        System.out.println("Unit [" + id + "] was clicked!");
        nifty.showPopup(nifty.getCurrentScreen(), popupMenu.getId(), null);
    }
    
    public void closeMenu() {
        nifty.closePopup(popupMenu.getId());
    }
    
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
            mouseMoved();
        }

        @Override
        public void onMouseButtonEvent(MouseButtonEvent mbe) {
            mouseMoved();
        }

        @Override
        public void onKeyEvent(KeyInputEvent kie) {}

        @Override
        public void onTouchEvent(TouchEvent te) {}
    };
    
    @Override
    public void cleanup() {
        app.stop();
        // Continues to clean up
        super.cleanup();
    }
}

class MenuItem {
    public String id;
    public String name;
    public MenuItem(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

class MenuItemActivatedEventSubscriber implements EventTopicSubscriber<MenuItemActivatedEvent> {
    
    @Override
    public void onEvent(final String id, final MenuItemActivatedEvent event) {
        MenuItem item = (MenuItem)event.getItem();
        if(item.id.equals("clickMe")) {
            System.out.println("Oki doki!");
        }
    }
}
