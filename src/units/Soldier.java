package units;

import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import util.Fields;
import util.Methods;

/*
 * public class Soldier extends Unit
 * 
 * This Unit is a Soldier. The Soldiers are the regular units, able
 * to hit troops and units and they can take advantage of holes.
 * But, of course, they are very fragile.
 */

public class Soldier extends Unit {
    
    // Constants
    public static final String NAME = "Soldier";
    
    public static final String ANIM_WALK = "Walk";
    public static final String ANIM_SHOOT = "Shoot";
    public static final String ANIM_DIE = "Die";
    
    public Soldier(int x, int z, Spatial model) {
        // Inits the variables
        this.attack = 3;
        this.typeAttack = Fields.FRONTAL_ATTACK;
        this.life = 1;
        this.cost = 2;
        this.unitNode = new Node("Unit: " + NAME + x + "-" + z);
        this.unitNode.attachChild(model);
        unitNode.move(x, 0, z);
        animControl = ((Node)model).getChild("Model").getControl(AnimControl.class);
        animChannel = animControl.createChannel();
        //TODO: Using software skinning because hardware skinning breaks the model. Fix that later.
        SkeletonControl sc = ((Node)model).getChild("Model").getControl(SkeletonControl.class);
        sc.setHardwareSkinningPreferred(false);
        Methods.blockyTexture(model);
    }
    
    // Methods
    @Override
    public void move(int x, int z) {
        unitNode.move(x, 0, z);
    }
    
    @Override
    public void eliminate() {
        
    }
}
