/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package squares;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


/*
 * public class Tree extends Casa
 * 
 * This is a Square called Tree. This Square is perfect for Snipers.
 * They can climb it and hit any troop. However, it can be destroyed
 * with explosions.
 * 
*/

public class Tree extends Square {
    
    // Constants
    public static final String NAME = "Tree";
    
    public Tree(int x, int offsetX, int z, int offsetZ, Spatial model) {
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
