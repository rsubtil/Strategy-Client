package util;

import com.jme3.math.Quaternion;

/*
 * This class is used to store global variables. It cannot be extended,
 * and all variables are static and final.
 * 
 */

public final class Fields {
    public static final int FRONTAL_ATTACK = 0;
    public static final int SEMIAERIAL_ATTACK = 1;
    public static final int AERIAL_ATTACK = 2;
    
    public static final Quaternion ISOMETRIC_ROTATION = new Quaternion(0.78147f,
                                                                        0.35355f,
                                                                        0.37322f,
                                                                        0.35356f);
}
