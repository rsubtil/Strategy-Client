package squares;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

/* 
 * public abstract class Square
 * 
 * This class represents a Square from the Board. It must be
 * extended by Grass, Tree, Rock, etc... It has coordinates
 * in space and internal methods to manage the game logic.
 */
public abstract class Square {
    
    // Variables
    protected Node squareNode; // Node to hold the square's geometry
    protected int x, z; // Coords
    
    // Methods
    public Vector2f getCoords(boolean trueCoords) {
        // If true, return the true coordinates in the 3D space. If false, returns the coords in the board.
        if(trueCoords) {
            return new Vector2f(squareNode.getWorldTranslation().x, squareNode.getWorldTranslation().z);
        } else {
            return new Vector2f(x, z);
        }
    }
    
    public Node getSquareNode() {
        return squareNode;
    }
    
    // TODO: Since toString was implemented, getName() is useless. Remove it.
    public String getName() {
        return "Square: " + x + "-" + z;
    }
    
    public int getX() {
        return x;
    }
    
    public int getZ() {
        return z;
    }
    
    public abstract void destroySquare();
    
    @Override
    public String toString() {
        return "Square: " + x + "-" + z;
    }
}
