package shields;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/* 
 * public class ShieldEletrico extends Shield
 * 
 * This class represents a Shield called Electric Shield. This
 * Shield is capable of protecting his unit from all kinds of
 * attacks. Every unit inside it can move and attack with it,
 * making it the perfect defense.
 * 
 */

public class ElectricShield extends Shield {
    
    // Constants
    public static final String NAME = "Electric Shield";
    
    public ElectricShield(int x, int z, Geometry model) {
        // Inits the variables
        totalShield = true;
        canAttack = true;
        canMove = true;
        life = 16;
        cost = 12;
        shieldNode = new Node("Shield: " + NAME + x + "-" + z);
        shieldNode.attachChild(model);
    }
    
    // Methods
    @Override
    public void destroy() {
        
    }
    
}
