package shields;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/*
 * public class AbrigoFibras extends Shield
 * 
 * This class represents a Shield called FiberShelter. This
 * Shield can be placed over a unit and protect him
 * completely unitl it is destroyed. The protected unit
 * can move with the shield, but cannot attack.
 * 
 */
public class FiberShelter extends Shield {
    
    // Constants
    public static final String NAME = "Fiber Shelter";
    
    public FiberShelter(int x, int z, Geometry model) {
        // Inits the variables
        totalShield = true;
        canMove = true;
        canAttack = false;
        life = 5;
        cost = 5;
        shieldNode = new Node("Shield: " + NAME + x + "-" + z);
        shieldNode.attachChild(model);
    }
    
    // Methods
    @Override
    public void destroy() {
        
    }
}
