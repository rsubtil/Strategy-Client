/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shields;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/*
 * public class ParedeFibra extends Shield
 * 
 * This class represents a Shield called Fiber Wall. This
 * Shield can protect unit from some bullets and hold some
 * explosions. But the units behind it can't frontally
 * attack.
 * 
 */
public class FiberWall extends Shield {
    
    // Constants
    public static final String NAME = "Fiber Wall";
    
    public FiberWall(int x, int z, Geometry model) {
        // Inits the variables
        totalShield = false;
        canMove = false;
        canAttack = false;
        life = 7;
        cost = 5;
        shieldNode = new Node("Shield: " + NAME + x + "-" + z);
        shieldNode.attachChild(model);
    }
    
    // Methods
    @Override
    public void destroy() {
        
    }
}
