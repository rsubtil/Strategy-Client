package main;

import appstates.GameplayAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {
    
    public static void main(String[] args) {
        Main app = new Main();
        AppSettings defs = new AppSettings(true);
        defs.setResizable(true);
        app.setSettings(defs);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        GameplayAppState gameplayAppState = new GameplayAppState(settings);
        stateManager.attach(gameplayAppState);
        
        // DEBUG
        setPauseOnLostFocus(false);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
