package squares;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


/*
 * public class Hole extends Casa
 * 
 * This is a Square called Hole. This Square is a strategic
 * space, since troops can get in it but can't leave from it.
 * Trops here can't attack frontally, so only indirect firing
 * troops are useful here (grenades FTW)
 * 
 */
public class Hole extends Square{
    
    // Contants
    public static final String NAME = "Hole";
    
    public Hole(int x, int offsetX, int z, int offsetZ, Spatial model) {
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
