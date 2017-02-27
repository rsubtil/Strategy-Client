package units;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import util.Fields;
import util.Methods;

/*
 * public class Cannon extends Unit
 * 
 * This Unit is a Cannon. The Cannon is a useful unit since it can
 * take out troops easily and damage other units. But it can only
 * attack frontally.
 * 
 */

public class Cannon extends Unit {
    
    // Constants
    public static final String NAME = "Cannon";
    
    public Cannon(int x, int z, Spatial model) {
        // inits the variables
        this.attack = 3;
        this.typeAttack = Fields.FRONTAL_ATTACK;
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
}
