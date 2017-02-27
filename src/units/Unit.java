package units;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.scene.Node;

/*
 * public abstract class Unit
 *
 * This class represents a Unit in the game. Every Unit, by
 * default, has the ability to move, and attack, and common
 * variables. It also offers an AnimControl and AnimChannel
 * fields for controlling animations.
 */

public abstract class Unit {
    
    // Variables
    protected int attack;
    protected int typeAttack;
    protected int life;
    protected int cost;
    
    protected Node unitNode;
    protected AnimControl animControl;
    protected AnimChannel animChannel;
    
    // Methods
    public void attack(Unit target) {
        target.damage(this.attack);
    }
    
    public abstract void move(int x, int z);
    // TODO: Add logic for different types of attack
    public void damage(int quantity) {
        this.life -= quantity;
        if(life <= 0) {
            this.eliminate();
        }
    }
    
    public void setAnim(String anim) {
        animChannel.setAnim(anim, 0);
    }
    
    public abstract void eliminate();
    
    
    // Getters
    public int getLife() {
        return life;
    }
    
    public Node getUnitNode() {
        return unitNode;
    }
    
    public AnimControl getAnimControl() {
        return animControl;
    }
}
