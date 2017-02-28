package controls;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.util.Calendar;
import jme3utilities.TimeOfDay;
import util.Methods;


public class SkyControl extends AbstractControl{
    
    // Variables
    private final jme3utilities.sky.SkyControl sky; // Sky from SkyControl
    // TODO: The game won't have constant day/night cycle. TimeOfDay will be removed later because of it.
    private final TimeOfDay time; // Needed to simulate passage of time
    
    public SkyControl(AssetManager assetManager, Camera cam, Node rootNode, ViewPort viewPort, AppStateManager stateManager) {
        // Inits the sky and adds it to rootNode
        sky = new jme3utilities.sky.SkyControl(assetManager, cam, 0.5f, true, true);
        rootNode.addControl(sky);
        
        // Configures the sky
        sky.getSunAndStars().setHour(18);
        sky.getSunAndStars().setObserverLatitude(45f * FastMath.DEG_TO_RAD);
        sky.getSunAndStars().setSolarLongitude(Calendar.JUNE, 23);
        sky.setCloudiness(0.65f);
        
        // Create and add lights to the Node and sky
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        //sun.setDirection(new Vector3f(1, -1, 1));
        
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        
        rootNode.addLight(sun);
        rootNode.addLight(ambient);
        
        sky.getUpdater().setMainLight(sun);
        sky.getUpdater().setAmbientLight(ambient);
        
        // Makes the sun shiny using BloomFilters
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloom.setBlurScale(2.5f);
        bloom.setExposurePower(1f);
        // TODO: This method stopped working since the 3.1 update. If the SkyControl is still alive, then get a update of it asap
        //Misc.getFpp(viewPort, assetManager).addFilter(bloom);
        Methods.getFpp(viewPort, assetManager).addFilter(bloom);
        sky.getUpdater().addBloomFilter(bloom);
        
        // Start the "time" at 12 o'clock
        time = new TimeOfDay(12f);
        stateManager.attach(time);
        sky.getSunAndStars().setHour(time.getHour());
        //sky.getUpdater().setAmbientMultiplier(2);
        time.setRate(1800f);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        // Simulates the flow of time
        float hour = time.getHour();
        sky.getSunAndStars().setHour(hour);
        
        // Changes the light's intensity according to the sun's y position
        // TODO: The intensity values needs some tweaks, the grass feels "un-natural"
        if(sky.getSunAndStars().getSunDirection().getY() >= 0) {
            sky.getUpdater().setMainMultiplier(3.5f);
        } else {
            sky.getUpdater().setMainMultiplier(.3f);
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
    public void turnOn() {
        sky.setEnabled(true);
    }
    
    public void turnOff() {
        sky.setEnabled(false);
    }
    
    public jme3utilities.sky.SkyControl getSky() {
        return sky;
    }
}
