package units;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import util.Fields;
import util.Methods;

/*
 * public class Mortar extends Unit
 * 
 * This Unit is a Mortar. A Mortar is a ver strategic unit, since
 * it attacks indirectly, being able to hit anything in ground.
 * However, it is weak on health and needs protection.
 * 
 */

public class Mortar extends Unit {
    
    // Constants
    public static final String NAME = "Mortar";
    
    public Mortar(int x, int z, Spatial model) {
        // Inits the variables
        this.attack = 3;
        this.typeAttack = Fields.AERIAL_ATTACK;
        this.life = 4;
        this.cost = 4;
        this.unitNode = new Node("Unit: " + NAME + x + "-" + z);
        this.unitNode.attachChild(model);
        unitNode.move(x * 2, 0, z * 2);
        Methods.blockyTexture(model);
    }
    
    // Methods
    @Override
    public void move(int x, int z) {
        throw new IllegalStateException("\n\nAlgo tentou mover uma contrução. Ou eu fiz algo muito mal, ou andas a hackear!");
    }
    
    @Override
    public void eliminate() {
        
    }
    
    public Node getRotaryModel() {
        Node temp = (Node)this.getUnitNode().getChild("Node");
        return (Node)temp.getChild("Mortar");
    }
}
