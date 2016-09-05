package util;

import com.jme3.asset.AssetManager;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.ViewPort;
import squares.Square;
import squares.Grass;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.Iterator;

/*
 * This class is used to store global methods to avoid code duplication.
 * The class cannot be extended, and all methods are public and static.
 */

public final class Methods {
    
    // Changes the type of textures to look blocky like in "Minecraft".
    public static void blockyTexture(Spatial spat) {
        if(spat instanceof Node) {
            Node n = (Node)spat;
            for(Spatial s : n.getChildren()) {
                blockyTexture(s);
            }
        } else if(spat instanceof Geometry) {
            Geometry g = (Geometry)spat;
            g.getMaterial().getTextureParam("DiffuseMap").getTextureValue().setMagFilter(Texture.MagFilter.Nearest);
        }
    }
    
    // Finds the Square object using the name
    // TODO: The method only accepts Lists of Grass, should be changed to Sqaures if needed
    public static Square getSquareByName(String name, ArrayList<Grass> squares) {
        for(Square square : squares) {
            if(square.getSquareNode().getName().equals(name)) {
                return square;
            }
        }
        return null;
    }
    
    // Cleans the array from null objects
    // TODO: See if there is already a method like that, probably in Arrays or Colletions class.
    public static void cleanArray(ArrayList list) {
        for(Iterator it = list.iterator(); it.hasNext();) {
            Object o = it.next();
            if(o == null) {
                it.remove();
            }
        }
    }
    
    // Gets the FilterPostProcessor of a viewPort
    public static FilterPostProcessor getFpp(ViewPort viewPort, AssetManager assetManager) {
        for(SceneProcessor processor : viewPort.getProcessors()) {
            if(processor instanceof FilterPostProcessor) {
                return (FilterPostProcessor)processor;
            }
        }
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);
        return fpp;
    }
}
