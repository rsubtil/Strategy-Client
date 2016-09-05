package shields;

import com.jme3.scene.Node;


/*
 * public abstract class Escudo
 * 
 * The Shield class is abstract, being extended by it's variations.
 * The Shield is an important mechaninc of this game, since gamers
 * will depend on they to create complex strategys and to protect
 * units.
 * 
 */
public abstract class Shield {
    
    // Variables
    protected int life;
    protected int cost;
    protected Node shieldNode; // To save the shield's model
    protected boolean totalShield; // If the shield can totally protect a unit
    protected boolean canMove; // If units can move
    protected boolean canAttack; // If units can attack
    
    // Getters
    public boolean isTotalShield() {
        return totalShield;
    }
    
    public boolean isCanMove() {
        return canMove;
    }
    
    public boolean isCanAttack() {
        return canAttack;
    }
    
    public int getLife() {
        return life;
    }
    
    public int getCost() {
        return cost;
    }

    // Methods
    public void damage(boolean isExplosion) {
        // If the damage comes from an explosion, ir receives double damage.
        if(isExplosion) {
            life -= 2;
        } else {
            life--;
        }
        
        // If the shield's life is depleted, it should be destroyed.
        if(life <= 0) {
            destroy();
        }
    }
    
    public abstract void destroy();
}
