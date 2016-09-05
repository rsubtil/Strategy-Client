package squares;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/*
 * public class Lake extends Casa
 * 
 * This is a Square called Lake. This Square is very useful, since
 * it regenerates life to all adjacent troops. However, units can't
 * do anything in order to get health.
 * 
 */
public class Lake extends Square {
    
    // Constants
    public static final String NAME = "Lake";

    public Lake(int x, int offsetX, int z, int offsetZ, Spatial model) {
        // Inits the variables
        this.x = x;
        this.z = z;
        this.squareNode = new Node("Square: "+ x + "-" + z);
        squareNode.attachChild(model);
        squareNode.move(x * 2 + offsetX, 0, z * 2 + offsetZ);
    }
    
    // Methods
    @Override
    public void destroySquare() {
        
    }
}
