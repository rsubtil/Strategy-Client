package util;

/*
 * This class is used to store global variables. It cannot be extended,
 * and all variables are static and final.
 * 
 */

public final class Fields {
    public static final int FRONTAL_ATTACK = 0;
    public static final int SEMIAERIAL_ATTACK = 1;
    public static final int AERIAL_ATTACK = 2;
    
    public static final int MIN_WIDTH = 640;
    public static final int MIN_HEIGHT = 480;
    
    public static enum MENU_OPTIONS {
        BUY(0),
        INFO(1),
        EXIT(2);
        
        private final int choice;
        
        MENU_OPTIONS(int choice) {
            this.choice = choice;
        }
        
        public int getValue() {
            return choice;
        }
    }
}
