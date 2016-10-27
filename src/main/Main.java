package main;

import appstates.GameplayAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.system.AppSettings;
import com.jme3.texture.FrameBuffer;

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
    }
    
    
    
    @Override
    public void simpleUpdate(float tpf) {
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
