package units;

import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
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
    
    public Soldier(int x, int z, Spatial model, AssetManager a) {
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
        
        // DEBUG: Create AttachmentNode
        /*Node originNode = (Node)((Node)model).getChild("Model");
        SkeletonControl sc = originNode.getControl(SkeletonControl.class);
        Node attachmentNode = sc.getAttachmentsNode("Braco D");
        attachmentNode.setName("AttachmentNode");
        Node weaponNode = (Node)originNode.getChild("Weapon");
        weaponNode.removeFromParent();
        attachmentNode.attachChild(weaponNode);
        originNode.attachChild(attachmentNode);*/
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
