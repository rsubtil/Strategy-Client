package squares;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/*
 * public class Rock extends Casa
 * 
 * This is a Square called Rock. This Square is an important
 * defense, since it protect units and troops behind it from
 * taking frontsal damage. However, it can be destroyed with
 * explosions.
 * 
 */
public class Rock extends Square {
    
    // Contants
    public static final String NAME = "Rock";
    
    public Rock(int x, int offsetX, int z, int offsetZ, Spatial model) {
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
