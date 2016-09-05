package shields;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/*
 * public class BrickWall extends Shield
 * 
 * This class represents a Shild called Brick Wall. This
 * Shield is a great defense to units. It is stronger than
 * the Fiber Wall and, thanks to the tiny holes in the wall,
 * troops can shoot and move with it.
 * 
 */
public class BrickWall extends Shield {
    
    // Constants
    public static final String NAME = "Brick Wall";
    
    public BrickWall(int x, int z, Geometry model) {
        // Inits the variables
        totalShield = false;
        canAttack = true;
        canMove = true;
        life = 20;
        cost = 10;
        shieldNode = new Node("Shield: " + NAME + x + "-" + z);
        shieldNode.attachChild(model);
    }
    
    // Methods
    @Override
    public void destroy() {
        
    }
}
