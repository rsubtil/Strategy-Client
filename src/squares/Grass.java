package squares;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/*
 * public class Grass extends Square
 * 
 * This is a Square called Grass. This Square is just a simple
 * square, that doesn't impose limitations and units can move
 * in it without problems.
 * 
 */
public class Grass extends Square {
    
    // Constants
    public static final String NAME = "Grass";
    
    public Grass(int x, int offsetX, int z, int offsetZ, Spatial model) {
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
